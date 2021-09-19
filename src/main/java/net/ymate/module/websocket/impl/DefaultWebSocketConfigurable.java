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
import net.ymate.platform.core.module.IModuleConfigurer;
import net.ymate.platform.core.module.impl.DefaultModuleConfigurable;

/**
 * @author 刘镇 (suninformation@163.com) on  2021/09/17 00:43
 * @since 2.0.0
 */
public final class DefaultWebSocketConfigurable extends DefaultModuleConfigurable {

    public static Builder builder() {
        return new Builder();
    }

    private DefaultWebSocketConfigurable() {
        super(IWebSocket.MODULE_NAME);
    }

    public static final class Builder {

        private final DefaultWebSocketConfigurable configurable = new DefaultWebSocketConfigurable();

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            configurable.addConfig(IWebSocketConfig.ENABLED, String.valueOf(enabled));
            return this;
        }

        public Builder asyncSendTimeout(long asyncSendTimeout) {
            configurable.addConfig(IWebSocketConfig.ASYNC_SEND_TIMEOUT, String.valueOf(asyncSendTimeout));
            return this;
        }

        public Builder defaultMaxSessionIdleTimeout(long defaultMaxSessionIdleTimeout) {
            configurable.addConfig(IWebSocketConfig.DEFAULT_MAX_SESSION_IDLE_TIMEOUT, String.valueOf(defaultMaxSessionIdleTimeout));
            return this;
        }

        public Builder defaultMaxTextMessageBufferSize(int defaultMaxTextMessageBufferSize) {
            configurable.addConfig(IWebSocketConfig.DEFAULT_MAX_TEXT_MESSAGE_BUFFER_SIZE, String.valueOf(defaultMaxTextMessageBufferSize));
            return this;
        }

        public Builder defaultMaxBinaryMessageBufferSize(int defaultMaxBinaryMessageBufferSize) {
            configurable.addConfig(IWebSocketConfig.DEFAULT_MAX_BINARY_MESSAGE_BUFFER_SIZE, String.valueOf(defaultMaxBinaryMessageBufferSize));
            return this;
        }

        public IModuleConfigurer build() {
            return configurable.toModuleConfigurer();
        }
    }
}