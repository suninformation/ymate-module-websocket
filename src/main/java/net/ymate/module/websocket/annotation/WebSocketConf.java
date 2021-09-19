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
package net.ymate.module.websocket.annotation;

import java.lang.annotation.*;

/**
 * @author 刘镇 (suninformation@163.com) on  2021/09/17 00:43
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebSocketConf {

    /**
     * @return 模块是否已启用, 默认值: true
     */
    boolean enabled() default true;

    /**
     * @return 消息异步发送超时时间, 单位: 毫秒, 默认值: 0
     */
    long asyncSendTimeout() default 0;

    /**
     * @return 会话默认最大空闲超时时间, 单位: 毫秒, 默认值: 0
     */
    long maxSessionIdleTimeout() default 0;

    /**
     * @return 文本消息默认最大缓冲区大小, 默认值: 0
     */
    int maxTextMessageBufferSize() default 0;

    /**
     * @return 二进制消息默认最大缓冲区大小, 默认值: 0
     */
    int maxBinaryMessageBufferSize() default 0;
}
