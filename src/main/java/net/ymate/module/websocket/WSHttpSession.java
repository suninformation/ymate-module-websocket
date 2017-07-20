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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingListener;
import javax.websocket.EndpointConfig;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/7/18 下午2:56
 * @version 1.0
 */
public class WSHttpSession {

    public static HttpSession bind(ServerEndpointConfig config, HandshakeRequest request) {
        return bind(config, request, null);
    }

    public static HttpSession bind(ServerEndpointConfig config, HandshakeRequest request, HttpSessionBindingListener listener) {
        HttpSession _session = (HttpSession) request.getHttpSession();
        if (_session != null) {
            config.getUserProperties().put(HttpSession.class.getName(), _session);
            if (listener != null) {
                _session.setAttribute(HttpSessionBindingListener.class.getName(), listener);
            }
        }
        return _session;
    }

    public static HttpSession bind(EndpointConfig config) {
        return (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
    }
}
