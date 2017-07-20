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
package net.ymate.module.websocket.handle;

import net.ymate.module.websocket.IWebSocket;
import net.ymate.module.websocket.WSClientListener;
import net.ymate.platform.core.beans.IBeanHandler;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/7/12 下午5:43
 * @version 1.0
 */
public class WSClientHandler implements IBeanHandler {

    private IWebSocket __owner;

    public WSClientHandler(IWebSocket owner) {
        __owner = owner;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object handle(Class<?> targetClass) throws Exception {
        __owner.registerClient((Class<? extends WSClientListener>) targetClass);
        return null;
    }
}
