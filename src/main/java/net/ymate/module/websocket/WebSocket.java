/*
 * Copyright 2007-2017 the original author or authors.
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
import net.ymate.module.websocket.impl.DefaultModuleCfg;
import net.ymate.module.websocket.support.WSClientConnectionManager;
import net.ymate.module.websocket.support.WSServerEndpointConfigurator;
import net.ymate.platform.core.Version;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.beans.BeanMeta;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.annotation.Module;
import net.ymate.platform.core.util.ClassUtils;
import net.ymate.platform.core.util.RuntimeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerContainer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/07/12 上午 11:37
 * @version 1.0
 */
@Module
public class WebSocket implements IModule, IWebSocket {

    private static final Log _LOG = LogFactory.getLog(WebSocket.class);

    public static final Version VERSION = new Version(1, 0, 0, WebSocket.class.getPackage().getImplementationVersion(), Version.VersionType.Alphal);

    private static volatile IWebSocket __instance;

    private YMP __owner;

    private IWebSocketModuleCfg __moduleCfg;

    private boolean __inited;

    private List<Class<? extends WSServerListener>> __serverListeners;

    private List<Class<? extends WSClientListener>> __clientListeners;

    private List<WSClientConnectionManager> __managers;

    private ServerContainer __serverContainer;

    private WebSocketContainer __webSocketContainer;

    public static IWebSocket get() {
        if (__instance == null) {
            synchronized (VERSION) {
                if (__instance == null) {
                    __instance = YMP.get().getModule(WebSocket.class);
                }
            }
        }
        return __instance;
    }

    public WebSocket() {
        __serverListeners = new ArrayList<Class<? extends WSServerListener>>();
        __clientListeners = new ArrayList<Class<? extends WSClientListener>>();
        //
        __managers = new ArrayList<WSClientConnectionManager>();
    }

    @Override
    public String getName() {
        return IWebSocket.MODULE_NAME;
    }

    @Override
    public void init(YMP owner) throws Exception {
        if (!__inited) {
            //
            _LOG.info("Initializing ymate-module-websocket-" + VERSION);
            //
            __owner = owner;
            __moduleCfg = new DefaultModuleCfg(owner);
            //
            __owner.registerHandler(WSServer.class, new WSServerHandler(this));
            __owner.registerHandler(WSClient.class, new WSClientHandler(this));
            //
            __inited = true;
        }
    }

    @Override
    public boolean isInited() {
        return __inited;
    }

    @Override
    public void registerServer(Class<? extends WSServerListener> targetClass) throws Exception {
        if (targetClass.getAnnotation(WSServer.class) == null) {
            throw new IllegalArgumentException("No WSServer annotation present on class");
        }
        __owner.registerBean(BeanMeta.create(targetClass.newInstance(), targetClass));
        __serverListeners.add(targetClass);
    }

    @Override
    public void registerClient(Class<? extends WSClientListener> targetClass) throws Exception {
        if (targetClass.getAnnotation(WSClient.class) == null) {
            throw new IllegalArgumentException("No WSClient annotation present on class");
        }
        __owner.registerBean(BeanMeta.create(targetClass.newInstance(), targetClass));
        __clientListeners.add(targetClass);
    }

    @Override
    public void registerServerEndpoints(ServletContext servletContext) {
        if (__serverContainer == null) {
            if (servletContext != null) {
                __serverContainer = (ServerContainer) servletContext.getAttribute(ServerContainer.class.getName());
                if (__serverContainer != null) {
                    if (__moduleCfg.getAsyncSendTimeout() > 0) {
                        __serverContainer.setAsyncSendTimeout(__moduleCfg.getAsyncSendTimeout());
                    }
                    if (__moduleCfg.getDefaultMaxSessionIdleTimeout() > 0) {
                        __serverContainer.setDefaultMaxSessionIdleTimeout(__moduleCfg.getDefaultMaxSessionIdleTimeout());
                    }
                    if (__moduleCfg.getDefaultMaxTextMessageBufferSize() > 0) {
                        __serverContainer.setDefaultMaxTextMessageBufferSize(__moduleCfg.getDefaultMaxTextMessageBufferSize());
                    }
                    if (__moduleCfg.getDefaultMaxBinaryMessageBufferSize() > 0) {
                        __serverContainer.setDefaultMaxBinaryMessageBufferSize(__moduleCfg.getDefaultMaxBinaryMessageBufferSize());
                    }
                    //
                    try {
                        for (Class<? extends WSServerListener> _item : __serverListeners) {
                            WSServerListener _target = __owner.getBean(_item);
                            if (_target != null) {
                                WSServer _serverAnno = _item.getAnnotation(WSServer.class);
                                //
                                WSServerEndpointConfigurator _configurator = _serverAnno.configurator().getConstructor(String.class, Endpoint.class).newInstance(_serverAnno.value(), _target);
                                //
                                _configurator.setEncoders(Arrays.asList(_serverAnno.encoders()));
                                _configurator.setDecoders(Arrays.asList(_serverAnno.decoders()));
                                _configurator.setSubprotocols(Arrays.asList(_serverAnno.subprotocols()));
                                //
                                if (_target instanceof IWSExtensible) {
                                    _configurator.setExtensions(((IWSExtensible) _target).getExtensions());
                                }
                                if (_target instanceof IWSHandshakeModifier) {
                                    _configurator.setHandshakeModifier((IWSHandshakeModifier) _target);
                                }
                                //
                                __serverContainer.addEndpoint(_configurator);
                            }
                        }
                    } catch (Exception e) {
                        _LOG.error("", RuntimeUtils.unwrapThrow(e));
                    }
                } else {
                    _LOG.warn("Attribute 'javax.websocket.server.ServerContainer' not found in ServletContext.");
                }
            } else {
                _LOG.warn("A ServletContext is required to access the javax.websocket.server.ServerContainer instance.");
            }
        }
    }

    @Override
    public void registerClientEndpoints() {
        if (__webSocketContainer == null) {
            __webSocketContainer = ContainerProvider.getWebSocketContainer();
            //
            if (__moduleCfg.getAsyncSendTimeout() > 0) {
                __webSocketContainer.setAsyncSendTimeout(__moduleCfg.getAsyncSendTimeout());
            }
            if (__moduleCfg.getDefaultMaxSessionIdleTimeout() > 0) {
                __webSocketContainer.setDefaultMaxSessionIdleTimeout(__moduleCfg.getDefaultMaxSessionIdleTimeout());
            }
            if (__moduleCfg.getDefaultMaxTextMessageBufferSize() > 0) {
                __webSocketContainer.setDefaultMaxTextMessageBufferSize(__moduleCfg.getDefaultMaxTextMessageBufferSize());
            }
            if (__moduleCfg.getDefaultMaxBinaryMessageBufferSize() > 0) {
                __webSocketContainer.setDefaultMaxBinaryMessageBufferSize(__moduleCfg.getDefaultMaxBinaryMessageBufferSize());
            }
            //
            for (Class<? extends WSClientListener> _item : __clientListeners) {
                WSClientListener _target = __owner.getBean(_item);
                if (_target != null) {
                    WSClient _clientAnno = _item.getAnnotation(WSClient.class);
                    URI _uri = null;
                    try {
                        _uri = new URI(_clientAnno.value());
                    } catch (Exception e) {
                        _LOG.error("", RuntimeUtils.unwrapThrow(e));
                    }
                    if (_uri != null) {
                        WSClientConnectionManager _manager = new WSClientConnectionManager(_uri, _target);
                        __managers.add(_manager);
                        //
                        _manager.setEncoders(Arrays.asList(_clientAnno.encoders()));
                        _manager.setDecoders(Arrays.asList(_clientAnno.decoders()));
                        _manager.setPreferredSubprotocols(_clientAnno.subprotocols());
                        //
                        if (!_clientAnno.configurator().equals(ClientEndpointConfig.Configurator.class)) {
                            ClientEndpointConfig.Configurator _configurator = ClassUtils.impl(_clientAnno.configurator(), ClientEndpointConfig.Configurator.class);
                            if (_configurator != null) {
                                _manager.setConfigurator(_configurator);
                            }
                        }
                        //
                        if (_target instanceof IWSExtensible) {
                            _manager.setExtensions(((IWSExtensible) _target).getExtensions());
                        }
                        //
                        if (_clientAnno.autoStartup()) {
                            _manager.start();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        if (__inited) {
            __inited = false;
            //
            for (WSClientConnectionManager _item : __managers) {
                _item.stop();
            }
            __managers = null;
            __serverContainer = null;
            //
            __moduleCfg = null;
            __owner = null;
        }
    }

    @Override
    public YMP getOwner() {
        return __owner;
    }

    @Override
    public IWebSocketModuleCfg getModuleCfg() {
        return __moduleCfg;
    }
}
