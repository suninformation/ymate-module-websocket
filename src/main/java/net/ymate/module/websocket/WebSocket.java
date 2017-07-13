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

import net.ymate.module.websocket.annotation.WSServer;
import net.ymate.module.websocket.handle.WSServerHandler;
import net.ymate.module.websocket.impl.DefaultModuleCfg;
import net.ymate.module.websocket.support.WSServerEndpointConfigurator;
import net.ymate.platform.core.Version;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.annotation.Module;
import net.ymate.platform.core.util.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/07/12 上午 11:37
 * @version 1.0
 */
@Module
public class WebSocket implements IModule, IWebSocket {

    private static final Log _LOG = LogFactory.getLog(WebSocket.class);

    public static final Version VERSION = new Version(1, 0, 0, WebSocket.class.getPackage().getImplementationVersion(), Version.VersionType.Alphal);

    private static volatile IWebSocket __instance;

    private YMP __owner;

    private IWebSocketModuleCfg __moduleCfg;

    private boolean __inited;

    private List<WSServerEndpointConfigurator> __serverEndpointConfigurators;

    public static IWebSocket get() {
        if (__instance == null) {
            synchronized (VERSION) {
                if (__instance == null) {
                    __instance = YMP.get().getModule(WebSocket.class);
                }
            }
        }
        return __instance;
    }

    public WebSocket() {
        __serverEndpointConfigurators = new ArrayList<WSServerEndpointConfigurator>();
    }

    public String getName() {
        return IWebSocket.MODULE_NAME;
    }

    public void init(YMP owner) throws Exception {
        if (!__inited) {
            //
            _LOG.info("Initializing ymate-module-websocket-" + VERSION);
            //
            __owner = owner;
            __moduleCfg = new DefaultModuleCfg(owner);
            //
            __owner.registerHandler(WSServer.class, new WSServerHandler(this));
            //
            __inited = true;
        }
    }

    public boolean isInited() {
        return __inited;
    }

    public void registerServer(Class<? extends WSListener> targetClass) throws Exception {
        WSServer _anno = targetClass.getAnnotation(WSServer.class);
        if (_anno == null) {
            throw new IllegalArgumentException("No WSServer annotation present on class");
        }
        WSListener _target = ClassUtils.impl(targetClass, WSListener.class);
        if (_target != null) {
            WSServerEndpointConfigurator _configurator = new WSServerEndpointConfigurator(_anno.value(), _target);
            _configurator.getEncoders().addAll(Arrays.asList(_anno.encoders()));
            _configurator.getDecoders().addAll(Arrays.asList(_anno.decoders()));
            _configurator.getSubprotocols().addAll(Arrays.asList(_anno.subprotocols()));
            //
            __serverEndpointConfigurators.add(_configurator);
        }
    }

    public void registerClient(Class<? extends WSListener> targetClass) throws Exception {
    }

    public List<WSServerEndpointConfigurator> getServerEndpointConfigurators() {
        return __serverEndpointConfigurators;
    }

    public void destroy() throws Exception {
        if (__inited) {
            __inited = false;
            //
            __moduleCfg = null;
            __owner = null;
        }
    }

    public YMP getOwner() {
        return __owner;
    }

    public IWebSocketModuleCfg getModuleCfg() {
        return __moduleCfg;
    }
}
