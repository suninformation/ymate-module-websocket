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

import net.ymate.module.websocket.WebSocket;
import net.ymate.platform.core.event.Events;
import net.ymate.platform.core.event.IEventListener;
import net.ymate.platform.core.event.IEventRegister;
import net.ymate.platform.core.event.annotation.EventRegister;
import net.ymate.platform.webmvc.WebEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/7/13 上午10:34
 * @version 1.0
 */
@EventRegister
public class WSServletContextEventProcessor implements IEventRegister, IEventListener<WebEvent> {

    private static final Log _LOG = LogFactory.getLog(WSServletContextEventProcessor.class);

    @Override
    public void register(Events events) throws Exception {
        events.registerListener(Events.MODE.NORMAL, WebEvent.class, this);
    }

    @Override
    public boolean handle(WebEvent context) {
        switch (context.getEventName()) {
            case SERVLET_CONTEXT_INITED:
                WebSocket.get().registerServerEndpoints(((ServletContextEvent) context.getEventSource()).getServletContext());
                WebSocket.get().registerClientEndpoints();
                break;
        }
        return false;
    }
}
