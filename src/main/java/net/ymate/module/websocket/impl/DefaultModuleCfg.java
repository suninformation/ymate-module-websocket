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
package net.ymate.module.websocket.impl;

import net.ymate.module.websocket.IWebSocket;
import net.ymate.module.websocket.IWebSocketModuleCfg;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.lang.BlurObject;

import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/07/12 上午 11:37
 * @version 1.0
 */
public class DefaultModuleCfg implements IWebSocketModuleCfg {

    private long __asyncSendTimeout;

    private long __defaultMaxSessionIdleTimeout;

    private int __defaultMaxTextMessageBufferSize;

    private int __defaultMaxBinaryMessageBufferSize;

    public DefaultModuleCfg(YMP owner) {
        Map<String, String> _moduleCfgs = owner.getConfig().getModuleConfigs(IWebSocket.MODULE_NAME);
        //
        __asyncSendTimeout = BlurObject.bind(_moduleCfgs.get("async_send_timeout")).toLongValue();
        __defaultMaxSessionIdleTimeout = BlurObject.bind(_moduleCfgs.get("default_max_session_idle_timeout")).toLongValue();
        __defaultMaxTextMessageBufferSize = BlurObject.bind(_moduleCfgs.get("default_max_text_message_buffer_size")).toIntValue();
        __defaultMaxBinaryMessageBufferSize = BlurObject.bind(_moduleCfgs.get("default_max_binary_message_buffer_size")).toIntValue();
    }

    @Override
    public long getAsyncSendTimeout() {
        return __asyncSendTimeout;
    }

    @Override
    public long getDefaultMaxSessionIdleTimeout() {
        return __defaultMaxSessionIdleTimeout;
    }

    @Override
    public int getDefaultMaxTextMessageBufferSize() {
        return __defaultMaxTextMessageBufferSize;
    }

    @Override
    public int getDefaultMaxBinaryMessageBufferSize() {
        return __defaultMaxBinaryMessageBufferSize;
    }
}