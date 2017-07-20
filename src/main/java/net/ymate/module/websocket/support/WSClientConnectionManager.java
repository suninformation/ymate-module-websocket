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
package net.ymate.module.websocket.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.websocket.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/7/19 上午11:15
 * @version 1.0
 */
public class WSClientConnectionManager {

    private static final Log _LOG = LogFactory.getLog(WSClientConnectionManager.class);

    private final URI __uri;

    private final Endpoint __endpoint;

    private boolean running = false;

    private final Object __locker = new Object();

    private WebSocketContainer __webSocketContainer = ContainerProvider.getWebSocketContainer();

    private final ClientEndpointConfig.Builder __configBuilder = ClientEndpointConfig.Builder.create();

    private ExecutorService __executor;

    private volatile Session __session;

    public WSClientConnectionManager(URI uri, Endpoint endpoint) {
        __uri = uri;
        __endpoint = endpoint;
    }

    public URI getUri() {
        return __uri;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setPreferredSubprotocols(String... protocols) {
        __configBuilder.preferredSubprotocols(Arrays.asList(protocols));
    }

    public void setExtensions(List<Extension> extensions) {
        __configBuilder.extensions(extensions);
    }

    public void setEncoders(List<Class<? extends Encoder>> encoders) {
        __configBuilder.encoders(encoders);
    }

    public void setDecoders(List<Class<? extends Decoder>> decoders) {
        __configBuilder.decoders(decoders);
    }

    public void setConfigurator(ClientEndpointConfig.Configurator configurator) {
        __configBuilder.configurator(configurator);
    }

    public void setWebSocketContainer(WebSocketContainer webSocketContainer) {
        __webSocketContainer = webSocketContainer;
    }

    public WebSocketContainer getWebSocketContainer() {
        return __webSocketContainer;
    }

    public final void start() {
        synchronized (__locker) {
            if (!isRunning()) {
                __doStart();
            }
        }
    }

    protected void __doStart() {
        synchronized (__locker) {
            if (_LOG.isInfoEnabled()) {
                _LOG.info("Starting " + getClass().getSimpleName());
            }
            this.running = true;
            openConnection();
        }
    }

    public final void stop() {
        synchronized (__locker) {
            if (isRunning()) {
                if (_LOG.isInfoEnabled()) {
                    _LOG.info("Stopping " + getClass().getSimpleName());
                }
                try {
                    __doStop();
                } catch (Throwable ex) {
                    _LOG.error("Failed to stop WebSocket connection", ex);
                } finally {
                    this.running = false;
                }
            }
            try {
                if (__executor != null) {
                    __executor.shutdown();
                }
            } catch (Throwable ex) {
                _LOG.error("Failed to shutdown WebSocket connection manager", ex);
            } finally {
                __executor = null;
            }
        }
    }

    public final void stop(Runnable callback) {
        synchronized (__locker) {
            stop();
            callback.run();
        }
    }

    protected void __doStop() throws Exception {
        if (isConnected()) {
            closeConnection();
        }
    }

    public boolean isRunning() {
        synchronized (__locker) {
            return this.running;
        }
    }

    protected void openConnection() {
        if (__executor == null) {
            __executor = Executors.newSingleThreadExecutor();
        }
        __executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (_LOG.isInfoEnabled()) {
                        _LOG.info("Connecting to WebSocket at " + __uri);
                    }
                    __session = __webSocketContainer.connectToServer(__endpoint, __configBuilder.build(), __uri);
                    _LOG.info("Successfully connected to WebSocket");
                } catch (Throwable ex) {
                    _LOG.error("Failed to connect to WebSocket", ex);
                    stop();
                }
            }
        });
    }

    protected void closeConnection() throws Exception {
        try {
            if (isConnected()) {
                __session.close();
            }
        } finally {
            __session = null;
        }
    }

    protected boolean isConnected() {
        return (__session != null && __session.isOpen());
    }
}
