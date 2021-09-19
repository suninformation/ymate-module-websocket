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

import net.ymate.module.websocket.support.WSClientConnectionManager;
import net.ymate.module.websocket.support.WSServerEndpointConfigurator;
import net.ymate.platform.core.IApplication;
import net.ymate.platform.core.beans.annotation.Ignored;
import net.ymate.platform.core.support.IDestroyable;
import net.ymate.platform.core.support.IInitialization;

import javax.servlet.ServletContext;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/07/12 上午 11:37
 * @since 1.0
 */
@Ignored
public interface IWebSocket extends IInitialization<IApplication>, IDestroyable {

    String MODULE_NAME = "module.websocket";

    /**
     * 获取所属应用容器
     *
     * @return 返回所属应用容器实例
     */
    IApplication getOwner();

    /**
     * 获取配置
     *
     * @return 返回配置对象
     */
    IWebSocketConfig getConfig();

    /**
     * 注册服务端点监听器
     *
     * @param targetClass 服务端点监听接口类型
     * @throws Exception 可能产生的异常
     */
    void registerServer(Class<? extends WSServerListener> targetClass) throws Exception;

    /**
     * 注册服务端点监听器
     *
     * @param serverEndpointConfigurator 服务端点配置
     */
    void registerServer(WSServerEndpointConfigurator serverEndpointConfigurator);

    /**
     * 注册客户端点监听器
     *
     * @param targetClass 客户端点监听接口类型
     * @throws Exception 可能产生的异常
     */
    void registerClient(Class<? extends WSClientListener> targetClass) throws Exception;

    /**
     * 注册客户端点监听器
     *
     * @param clientConnectionManager 客户端连接管理器
     */
    void registerClient(WSClientConnectionManager clientConnectionManager);

    /**
     * 向容器注册并初始化服务端点
     *
     * @param servletContext Web服务容器对象
     */
    void registerServerEndpoints(ServletContext servletContext);

    /**
     * 向容器注册并初始化客户端点
     */
    void registerClientEndpoints();
}
