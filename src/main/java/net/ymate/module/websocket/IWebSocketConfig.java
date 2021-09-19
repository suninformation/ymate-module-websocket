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

import net.ymate.platform.core.beans.annotation.Ignored;
import net.ymate.platform.core.support.IInitialization;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/07/12 上午 11:37
 * @since 1.0
 */
@Ignored
public interface IWebSocketConfig extends IInitialization<IWebSocket> {

    String ENABLED = "enabled";

    String ASYNC_SEND_TIMEOUT = "async_send_timeout";

    String DEFAULT_MAX_SESSION_IDLE_TIMEOUT = "default_max_session_idle_timeout";

    String DEFAULT_MAX_TEXT_MESSAGE_BUFFER_SIZE = "default_max_text_message_buffer_size";

    String DEFAULT_MAX_BINARY_MESSAGE_BUFFER_SIZE = "default_max_binary_message_buffer_size";

    /**
     * 模块是否已启用, 默认值: true
     *
     * @return 返回false表示禁用
     */
    boolean isEnabled();

    /**
     * 消息异步发送超时时间, 单位: 毫秒, 默认值: 0
     *
     * @return 返回消息异步发送超时时间毫秒值
     */
    long getAsyncSendTimeout();

    /**
     * 会话默认最大空闲超时时间, 单位: 毫秒, 默认值: 0
     *
     * @return 返回会话默认最大空闲超时时间毫秒值
     */
    long getDefaultMaxSessionIdleTimeout();

    /**
     * 文本消息默认最大缓冲区大小, 默认值: 0
     *
     * @return 返回文本消息默认最大缓冲区大小
     */
    int getDefaultMaxTextMessageBufferSize();

    /**
     * 二进制消息默认最大缓冲区大小, 默认值: 0
     *
     * @return 返回二进制消息默认最大缓冲区大小
     */
    int getDefaultMaxBinaryMessageBufferSize();
}