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
package net.ymate.module.websocket.support;

import net.ymate.module.websocket.IWebSocket;
import net.ymate.module.websocket.WSClientListener;
import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.commons.util.ThreadUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.websocket.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/7/19 上午11:15
 * @since 1.0
 */
public class WSClientConnectionManager {

    private static final Log LOG = LogFactory.getLog(WSClientConnectionManager.class);

    private final IWebSocket owner;

    private final URI uri;

    private final Class<? extends WSClientListener> endpointClass;

    private boolean autoStartup;

    private boolean running = false;

    private final Object locker = new Object();

    private WebSocketContainer socketContainer;

    private final ClientEndpointConfig.Builder configBuilder = ClientEndpointConfig.Builder.create();

    private ExecutorService executorService;

    private volatile Session session;

    public WSClientConnectionManager(IWebSocket owner, URI uri, Class<? extends WSClientListener> endpointClass) {
        this.owner = owner;
        this.uri = uri;
        this.endpointClass = endpointClass;
    }

    public URI getUri() {
        return uri;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setPreferredSubprotocols(String... protocols) {
        configBuilder.preferredSubprotocols(Arrays.asList(protocols));
    }

    public void setExtensions(List<Extension> extensions) {
        configBuilder.extensions(extensions);
    }

    public void setEncoders(List<Class<? extends Encoder>> encoders) {
        configBuilder.encoders(encoders);
    }

    public void setDecoders(List<Class<? extends Decoder>> decoders) {
        configBuilder.decoders(decoders);
    }

    public void setConfigurator(ClientEndpointConfig.Configurator configurator) {
        configBuilder.configurator(configurator);
    }

    public void setWebSocketContainer(WebSocketContainer webSocketContainer) {
        socketContainer = webSocketContainer;
    }

    public WebSocketContainer getWebSocketContainer() {
        return socketContainer;
    }

    public Class<? extends WSClientListener> getEndpointClass() {
        return endpointClass;
    }

    public final void start() {
        synchronized (locker) {
            if (!isRunning()) {
                doStart();
            }
        }
    }

    protected void doStart() {
        synchronized (locker) {
            this.running = true;
            openConnection();
        }
    }

    public final void stop() {
        synchronized (locker) {
            if (isRunning()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(String.format("Stopping WSClient connection for %s", uri));
                }
                try {
                    doStop();
                } catch (Throwable ex) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error(String.format("Failed to stop WSClient connection for %s", uri), RuntimeUtils.unwrapThrow(ex));
                    }
                } finally {
                    this.running = false;
                }
            }
            try {
                if (executorService != null) {
                    executorService.shutdown();
                }
            } catch (Throwable ex) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(String.format("Failed to shutdown WSClient connection for %s", uri), RuntimeUtils.unwrapThrow(ex));
                }
            } finally {
                executorService = null;
            }
        }
    }

    public final void stop(Runnable callback) {
        synchronized (locker) {
            stop();
            callback.run();
        }
    }

    protected void doStop() throws Exception {
        if (isConnected()) {
            closeConnection();
        }
    }

    public boolean isAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public boolean isRunning() {
        synchronized (locker) {
            return this.running;
        }
    }

    protected void openConnection() {
        if (executorService == null) {
            executorService = ThreadUtils.newSingleThreadExecutor();
        }
        executorService.execute(() -> {
            try {
                if (LOG.isInfoEnabled()) {
                    LOG.info(String.format("Connecting to WSClient at %s", uri));
                }
                if (socketContainer == null) {
                    socketContainer = ContainerProvider.getWebSocketContainer();
                }
                session = socketContainer.connectToServer(owner.getOwner().getBeanFactory().getBean(endpointClass), configBuilder.build(), uri);
            } catch (Throwable ex) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(String.format("Failed to connect to WSClient at %s", uri), RuntimeUtils.unwrapThrow(ex));
                }
                stop();
            }
        });
    }

    protected void closeConnection() throws Exception {
        try {
            if (isConnected()) {
                session.close();
            }
        } finally {
            session = null;
        }
    }

    protected boolean isConnected() {
        return (session != null && session.isOpen());
    }
}
