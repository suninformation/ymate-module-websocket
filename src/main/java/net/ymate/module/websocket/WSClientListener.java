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

import net.ymate.platform.core.util.RuntimeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.websocket.*;
import java.nio.ByteBuffer;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/7/17 下午4:19
 * @version 1.0
 */
public abstract class WSClientListener extends Endpoint {

    private static final Log _LOG = LogFactory.getLog(WSClientListener.class);

    protected void afterConnectionOpened(Session session) throws Exception {
    }

    protected void afterConnectionClosed(Session session, CloseReason closeReason) throws Exception {
    }

    protected void handleTextMessage(Session session, String message, boolean isLast) {
    }

    protected void handleBinaryMessage(Session session, ByteBuffer message, boolean isLast) {
    }

    protected void handlePongMessage(Session session, PongMessage message) {
    }

    protected void handleError(Session session, Throwable thr) throws Exception {
    }

    protected boolean isPartialEnabled() {
        return false;
    }

    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        if (isPartialEnabled()) {
            session.addMessageHandler(new MessageHandler.Partial<String>() {
                @Override
                public void onMessage(String message, boolean isLast) {
                    handleTextMessage(session, message, isLast);
                }
            });
            session.addMessageHandler(new MessageHandler.Partial<ByteBuffer>() {
                @Override
                public void onMessage(ByteBuffer message, boolean isLast) {
                    handleBinaryMessage(session, message, isLast);
                }
            });
        } else {
            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleTextMessage(session, message, true);
                }
            });
            session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
                @Override
                public void onMessage(ByteBuffer message) {
                    handleBinaryMessage(session, message, true);
                }
            });
        }
        //
        session.addMessageHandler(new MessageHandler.Whole<PongMessage>() {
            @Override
            public void onMessage(PongMessage message) {
                handlePongMessage(session, message);
            }
        });
        //
        try {
            afterConnectionOpened(session);
        } catch (Throwable e) {
            __doTryCloseWithError(session, e);
        }
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        try {
            afterConnectionClosed(session, closeReason);
        } catch (Throwable e) {
            if (_LOG.isErrorEnabled()) {
                _LOG.error("Unhandled error for " + session, RuntimeUtils.unwrapThrow(e));
            }
        }
    }

    @Override
    public void onError(Session session, Throwable thr) {
        try {
            handleError(session, thr);
        } catch (Throwable e) {
            __doTryCloseWithError(session, e);
        }
    }

    private void __doTryCloseWithError(Session session, Throwable thr) {
        if (_LOG.isErrorEnabled()) {
            _LOG.error("Closing session due to exception for " + session, RuntimeUtils.unwrapThrow(thr));
        }
        if (session.isOpen()) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "SERVER_ERROR"));
            } catch (Throwable ignore) {
            }
        }
    }
}
