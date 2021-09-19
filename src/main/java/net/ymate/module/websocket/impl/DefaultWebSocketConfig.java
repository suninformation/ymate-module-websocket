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
package net.ymate.module.websocket.impl;

import net.ymate.module.websocket.IWebSocket;
import net.ymate.module.websocket.IWebSocketConfig;
import net.ymate.module.websocket.annotation.WebSocketConf;
import net.ymate.platform.core.configuration.IConfigReader;
import net.ymate.platform.core.module.IModuleConfigurer;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/07/12 上午 11:37
 * @since 1.0
 */
public final class DefaultWebSocketConfig implements IWebSocketConfig {

    private boolean enabled = true;

    private long asyncSendTimeout;

    private long maxSessionIdleTimeout;

    private int maxTextMessageBufferSize;

    private int maxBinaryMessageBufferSize;

    private boolean initialized;

    public static DefaultWebSocketConfig defaultConfig() {
        return builder().build();
    }

    public static DefaultWebSocketConfig create(IModuleConfigurer moduleConfigurer) {
        return new DefaultWebSocketConfig(null, moduleConfigurer);
    }

    public static DefaultWebSocketConfig create(Class<?> mainClass, IModuleConfigurer moduleConfigurer) {
        return new DefaultWebSocketConfig(mainClass, moduleConfigurer);
    }

    public static Builder builder() {
        return new Builder();
    }

    private DefaultWebSocketConfig() {
    }

    private DefaultWebSocketConfig(Class<?> mainClass, IModuleConfigurer moduleConfigurer) {
        IConfigReader configReader = moduleConfigurer.getConfigReader();
        //
        WebSocketConf confAnn = mainClass == null ? null : mainClass.getAnnotation(WebSocketConf.class);
        //
        enabled = configReader.getBoolean(ENABLED, confAnn == null || confAnn.enabled());
        asyncSendTimeout = configReader.getLong(ASYNC_SEND_TIMEOUT, confAnn != null ? confAnn.asyncSendTimeout() : 0);
        maxSessionIdleTimeout = configReader.getLong(MAX_SESSION_IDLE_TIMEOUT, confAnn != null ? confAnn.maxSessionIdleTimeout() : 0);
        maxTextMessageBufferSize = configReader.getInt(MAX_TEXT_MESSAGE_BUFFER_SIZE, confAnn != null ? confAnn.maxTextMessageBufferSize() : 0);
        maxBinaryMessageBufferSize = configReader.getInt(MAX_BINARY_MESSAGE_BUFFER_SIZE, confAnn != null ? confAnn.maxBinaryMessageBufferSize() : 0);
    }

    @Override
    public void initialize(IWebSocket owner) throws Exception {
        if (!initialized) {
            if (enabled) {
                // TODO What to do?
            }
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (!initialized) {
            this.enabled = enabled;
        }
    }

    @Override
    public long getAsyncSendTimeout() {
        return asyncSendTimeout;
    }

    public void setAsyncSendTimeout(long asyncSendTimeout) {
        if (!initialized) {
            this.asyncSendTimeout = asyncSendTimeout;
        }
    }

    @Override
    public long getMaxSessionIdleTimeout() {
        return maxSessionIdleTimeout;
    }

    public void setMaxSessionIdleTimeout(long maxSessionIdleTimeout) {
        if (!initialized) {
            this.maxSessionIdleTimeout = maxSessionIdleTimeout;
        }
    }

    @Override
    public int getMaxTextMessageBufferSize() {
        return maxTextMessageBufferSize;
    }

    public void setMaxTextMessageBufferSize(int maxTextMessageBufferSize) {
        if (!initialized) {
            this.maxTextMessageBufferSize = maxTextMessageBufferSize;
        }
    }

    @Override
    public int getMaxBinaryMessageBufferSize() {
        return maxBinaryMessageBufferSize;
    }

    public void setMaxBinaryMessageBufferSize(int maxBinaryMessageBufferSize) {
        if (!initialized) {
            this.maxBinaryMessageBufferSize = maxBinaryMessageBufferSize;
        }
    }

    public static final class Builder {

        private final DefaultWebSocketConfig config = new DefaultWebSocketConfig();

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            config.setEnabled(enabled);
            return this;
        }

        public Builder asyncSendTimeout(long asyncSendTimeout) {
            config.setAsyncSendTimeout(asyncSendTimeout);
            return this;
        }

        public Builder maxSessionIdleTimeout(long maxSessionIdleTimeout) {
            config.setMaxSessionIdleTimeout(maxSessionIdleTimeout);
            return this;
        }

        public Builder maxTextMessageBufferSize(int maxTextMessageBufferSize) {
            config.setMaxTextMessageBufferSize(maxTextMessageBufferSize);
            return this;
        }

        public Builder maxBinaryMessageBufferSize(int maxBinaryMessageBufferSize) {
            config.setMaxBinaryMessageBufferSize(maxBinaryMessageBufferSize);
            return this;
        }

        public DefaultWebSocketConfig build() {
            return config;
        }
    }
}