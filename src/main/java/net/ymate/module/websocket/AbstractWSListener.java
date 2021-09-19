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
/*
 * Copyright (c) 2007-2021, the original author or authors. All rights reserved.
 *
 * This program licensed under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package net.ymate.module.websocket;

import net.ymate.platform.commons.util.RuntimeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.websocket.*;
import java.nio.ByteBuffer;

/**
 * @author 刘镇 (suninformation@163.com) on 2021/9/18 7:47 下午
 * @since 2.0.0
 */
public abstract class AbstractWSListener extends Endpoint {

    private static final Log LOG = LogFactory.getLog(AbstractWSListener.class);

    /**
     * 连接开启后执行逻辑
     *
     * @param session 会话
     * @throws Exception 可能产生的任何异常
     */
    protected abstract void afterConnectionOpened(Session session) throws Exception;

    /**
     * 连接关闭后执行逻辑
     *
     * @param session     会话
     * @param closeReason 关闭原因
     * @throws Exception 可能产生的任何异常
     */
    protected abstract void afterConnectionClosed(Session session, CloseReason closeReason) throws Exception;

    /**
     * 文字消息处理逻辑
     *
     * @param session 会话
     * @param message 消息内容
     * @param isLast  否是要传递的整个消息的最后一条
     */
    protected abstract void handleTextMessage(Session session, String message, boolean isLast);

    /**
     * 二进制消息处理逻辑
     *
     * @param session 会话
     * @param message 消息内容
     * @param isLast  否是要传递的整个消息的最后一条
     */
    protected abstract void handleBinaryMessage(Session session, ByteBuffer message, boolean isLast);

    /**
     * PONG消息处理逻辑
     *
     * @param session 会话
     * @param message 消息内容
     */
    protected abstract void handlePongMessage(Session session, PongMessage message);

    /**
     * 异常错误处理逻辑
     *
     * @param session 会话
     * @param thr     异常
     * @throws Exception 可能产生的任何异常
     */
    protected abstract void handleError(Session session, Throwable thr) throws Exception;

    protected boolean isPartialEnabled() {
        return false;
    }

    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        if (isPartialEnabled()) {
            session.addMessageHandler(String.class, (message, isLast) -> handleTextMessage(session, message, isLast));
            session.addMessageHandler(ByteBuffer.class, (message, isLast) -> handleBinaryMessage(session, message, isLast));
        } else {
            session.addMessageHandler(String.class, message -> handleTextMessage(session, message, true));
            session.addMessageHandler(ByteBuffer.class, message -> handleBinaryMessage(session, message, true));
        }
        //
        session.addMessageHandler(PongMessage.class, message -> handlePongMessage(session, message));
        //
        try {
            afterConnectionOpened(session);
        } catch (Throwable e) {
            doTryCloseWithError(session, e);
        }
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        try {
            afterConnectionClosed(session, closeReason);
        } catch (Throwable e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(String.format("Unhandled error for %s", session), RuntimeUtils.unwrapThrow(e));
            }
        }
    }

    @Override
    public void onError(Session session, Throwable thr) {
        try {
            handleError(session, thr);
        } catch (Throwable e) {
            doTryCloseWithError(session, e);
        }
    }

    protected void doTryCloseWithError(Session session, Throwable thr) {
        if (LOG.isErrorEnabled()) {
            LOG.error(String.format("Closing session due to exception for %s", session), RuntimeUtils.unwrapThrow(thr));
        }
        if (session.isOpen()) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "SERVER_ERROR"));
            } catch (Throwable ignore) {
            }
        }
    }
}
