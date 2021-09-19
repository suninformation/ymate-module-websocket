/*
 * Copyright 2007-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ymate.module.websocket;

import net.ymate.module.websocket.annotation.WSClient;
import net.ymate.module.websocket.annotation.WSServer;
import net.ymate.module.websocket.handle.WSClientHandler;
import net.ymate.module.websocket.handle.WSServerHandler;
import net.ymate.module.websocket.impl.DefaultWebSocketConfig;
import net.ymate.module.websocket.support.WSClientConnectionManager;
import net.ymate.module.websocket.support.WSServerEndpointConfigurator;
import net.ymate.platform.commons.util.ClassUtils;
import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.core.*;
import net.ymate.platform.core.beans.BeanMeta;
import net.ymate.platform.core.beans.IBeanLoadFactory;
import net.ymate.platform.core.beans.IBeanLoader;
import net.ymate.platform.core.event.Events;
import net.ymate.platform.core.event.IEventListener;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.IModuleConfigurer;
import net.ymate.platform.core.module.impl.DefaultModuleConfigurer;
import net.ymate.platform.webmvc.WebEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.server.ServerContainer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/07/12 上午 11:37
 * @since 1.0
 */
public final class WebSocket implements IModule, IWebSocket {

    private static final Log LOG = LogFactory.getLog(WebSocket.class);

    private static volatile IWebSocket instance;

    private IApplication owner;

    private IWebSocketConfig config;

    private boolean initialized;

    private final List<WSServerEndpointConfigurator> serverEndpointConfigurators = new ArrayList<>();

    private final List<WSClientConnectionManager> clientConnectionManagers = new ArrayList<>();

    private ServerContainer serverContainer;

    public static IWebSocket get() {
        IWebSocket inst = instance;
        if (inst == null) {
            synchronized (WebSocket.class) {
                inst = instance;
                if (inst == null) {
                    instance = inst = YMP.get().getModuleManager().getModule(WebSocket.class);
                }
            }
        }
        return inst;
    }

    public WebSocket() {
    }

    public WebSocket(IWebSocketConfig config) {
        this.config = config;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void initialize(IApplication owner) throws Exception {
        if (!initialized) {
            //
            YMP.showVersion("Initializing ymate-module-websocket-websocket-${version}", new Version(2, 0, 0, WebSocket.class, Version.VersionType.Release));
            //
            this.owner = owner;
            //
            IApplicationConfigureFactory configureFactory = owner.getConfigureFactory();
            if (configureFactory != null) {
                IApplicationConfigurer configurer = configureFactory.getConfigurer();
                if (configurer != null) {
                    IBeanLoadFactory beanLoaderFactory = configurer.getBeanLoadFactory();
                    if (beanLoaderFactory != null) {
                        IBeanLoader beanLoader = beanLoaderFactory.getBeanLoader();
                        if (beanLoader != null) {
                            beanLoader.registerHandler(WSServer.class, new WSServerHandler(this));
                            beanLoader.registerHandler(WSClient.class, new WSClientHandler(this));
                        }
                    }
                }
                if (config == null) {
                    IModuleConfigurer moduleConfigurer = configurer == null ? null : configurer.getModuleConfigurer(MODULE_NAME);
                    if (moduleConfigurer != null) {
                        config = DefaultWebSocketConfig.create(configureFactory.getMainClass(), moduleConfigurer);
                    } else {
                        config = DefaultWebSocketConfig.create(configureFactory.getMainClass(), DefaultModuleConfigurer.createEmpty(MODULE_NAME));
                    }
                }
            }
            if (config == null) {
                config = DefaultWebSocketConfig.defaultConfig();
            }
            if (!config.isInitialized()) {
                config.initialize(this);
            }
            if (config.isEnabled()) {
                owner.getEvents().registerListener(Events.MODE.NORMAL, WebEvent.class, (IEventListener<WebEvent>) context -> {
                    if (context.getEventName() == WebEvent.EVENT.SERVLET_CONTEXT_INITIALIZED) {
                        registerServerEndpoints(((ServletContextEvent) context.getEventSource()).getServletContext());
                        registerClientEndpoints();
                    }
                    return false;
                });
            }
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void close() throws Exception {
        if (initialized) {
            initialized = false;
            //
            if (config.isEnabled()) {
                for (WSClientConnectionManager clientConnectionManager : clientConnectionManagers) {
                    clientConnectionManager.stop();
                }
                serverContainer = null;
            }
            //
            config = null;
            owner = null;
        }
    }

    @Override
    public IApplication getOwner() {
        return owner;
    }

    @Override
    public IWebSocketConfig getConfig() {
        return config;
    }

    @Override
    public void registerServer(Class<? extends WSServerListener> targetClass) throws Exception {
        WSServer serverAnn = targetClass.getAnnotation(WSServer.class);
        if (serverAnn == null) {
            throw new IllegalArgumentException("No WSServer annotation present on class");
        }
        WSServerEndpointConfigurator serverEndpointConfigurator = serverAnn.configurator().getConstructor(IWebSocket.class, String.class, Class.class).newInstance(this, serverAnn.value(), targetClass);
        serverEndpointConfigurator.setEncoders(Arrays.asList(serverAnn.encoders()));
        serverEndpointConfigurator.setDecoders(Arrays.asList(serverAnn.decoders()));
        serverEndpointConfigurator.setSubprotocols(Arrays.asList(serverAnn.subprotocols()));
        if (!serverAnn.extensible().equals(IWSExtensible.class)) {
            IWSExtensible extensible = ClassUtils.impl(serverAnn.extensible(), IWSExtensible.class);
            if (extensible != null) {
                serverEndpointConfigurator.setExtensions(extensible.getExtensions());
            }
        }
        if (!serverAnn.handshakeModifier().equals(IWSHandshakeModifier.class)) {
            IWSHandshakeModifier handshakeModifier = ClassUtils.impl(serverAnn.handshakeModifier(), IWSHandshakeModifier.class);
            if (handshakeModifier != null) {
                serverEndpointConfigurator.setHandshakeModifier(handshakeModifier);
            }
        }
        registerServer(serverEndpointConfigurator);
    }

    @Override
    public void registerServer(WSServerEndpointConfigurator serverEndpointConfigurator) {
        if (serverEndpointConfigurator != null) {
            BeanMeta beanMeta = BeanMeta.create(serverEndpointConfigurator.getEndpointClass());
            beanMeta.setInterfaceIgnored(true);
            owner.getBeanFactory().registerBean(beanMeta);
            //
            if (owner.isDevEnv() && LOG.isDebugEnabled()) {
                LOG.debug(String.format("--> [WSServer]: %s : %s", serverEndpointConfigurator.getPath(), serverEndpointConfigurator.getEndpointClass().getName()));
            }
            serverEndpointConfigurators.add(serverEndpointConfigurator);
        }
    }

    @Override
    public void registerClient(Class<? extends WSClientListener> targetClass) throws Exception {
        WSClient clientAnn = targetClass.getAnnotation(WSClient.class);
        if (clientAnn == null) {
            throw new IllegalArgumentException("No WSClient annotation present on class");
        }
        owner.getBeanFactory().registerBean(BeanMeta.create(targetClass, true));
        URI uri = new URI(clientAnn.value());
        WSClientConnectionManager clientConnectionManager = new WSClientConnectionManager(this, uri, targetClass);
        clientConnectionManager.setEncoders(Arrays.asList(clientAnn.encoders()));
        clientConnectionManager.setDecoders(Arrays.asList(clientAnn.decoders()));
        clientConnectionManager.setPreferredSubprotocols(clientAnn.subprotocols());
        if (!clientAnn.configurator().equals(ClientEndpointConfig.Configurator.class)) {
            ClientEndpointConfig.Configurator configurator = ClassUtils.impl(clientAnn.configurator(), ClientEndpointConfig.Configurator.class);
            if (configurator != null) {
                clientConnectionManager.setConfigurator(configurator);
            }
        }
        if (!clientAnn.extensible().equals(IWSExtensible.class)) {
            IWSExtensible extensible = ClassUtils.impl(clientAnn.extensible(), IWSExtensible.class);
            if (extensible != null) {
                clientConnectionManager.setExtensions(extensible.getExtensions());
            }
        }
        clientConnectionManager.setAutoStartup(clientAnn.autoStartup());
        //
        registerClient(clientConnectionManager);
    }

    @Override
    public void registerClient(WSClientConnectionManager clientConnectionManager) {
        if (clientConnectionManager != null) {
            if (owner.isDevEnv() && LOG.isDebugEnabled()) {
                LOG.debug(String.format("--> [WSClient]: %s : %s", clientConnectionManager.getUri(), clientConnectionManager.getEndpointClass().getName()));
            }
            clientConnectionManagers.add(clientConnectionManager);
        }
    }

    private ServerContainer doInitServerContainerIfNeed(ServletContext servletContext) {
        if (serverContainer == null) {
            if (servletContext != null) {
                serverContainer = (ServerContainer) servletContext.getAttribute(ServerContainer.class.getName());
                if (serverContainer != null) {
                    if (config.getAsyncSendTimeout() > 0) {
                        serverContainer.setAsyncSendTimeout(config.getAsyncSendTimeout());
                    }
                    if (config.getDefaultMaxSessionIdleTimeout() > 0) {
                        serverContainer.setDefaultMaxSessionIdleTimeout(config.getDefaultMaxSessionIdleTimeout());
                    }
                    if (config.getDefaultMaxTextMessageBufferSize() > 0) {
                        serverContainer.setDefaultMaxTextMessageBufferSize(config.getDefaultMaxTextMessageBufferSize());
                    }
                    if (config.getDefaultMaxBinaryMessageBufferSize() > 0) {
                        serverContainer.setDefaultMaxBinaryMessageBufferSize(config.getDefaultMaxBinaryMessageBufferSize());
                    }
                } else if (LOG.isWarnEnabled()) {
                    LOG.warn("Attribute 'javax.websocket.server.ServerContainer' not found in ServletContext.");
                }
            } else if (LOG.isWarnEnabled()) {
                LOG.warn("A ServletContext is required to access the javax.websocket.server.ServerContainer instance.");
            }
        }
        return serverContainer;
    }

    @Override
    public void registerServerEndpoints(ServletContext servletContext) {
        if (doInitServerContainerIfNeed(servletContext) != null) {
            try {
                for (WSServerEndpointConfigurator serverEndpointConfigurator : serverEndpointConfigurators) {
                    serverContainer.addEndpoint(serverEndpointConfigurator);
                }
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(StringUtils.EMPTY, RuntimeUtils.unwrapThrow(e));
                }
            }
        }
    }

    @Override
    public void registerClientEndpoints() {
        for (WSClientConnectionManager clientConnectionManager : clientConnectionManagers) {
            if (clientConnectionManager.isAutoStartup()) {
                clientConnectionManager.start();
            }
        }
    }
}
