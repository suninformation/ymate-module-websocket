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

import net.ymate.platform.core.YMP;

import javax.servlet.ServletContext;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/07/12 上午 11:37
 * @version 1.0
 */
public interface IWebSocket {

    String MODULE_NAME = "module.WebSocket";

    /**
     * @return 返回所属YMP框架管理器实例
     */
    YMP getOwner();

    /**
     * @return 返回模块配置对象
     */
    IWebSocketModuleCfg getModuleCfg();

    /**
     * @return 返回模块是否已初始化
     */
    boolean isInited();

    /**
     * 注册服务端点监听器
     *
     * @param targetClass 服务端点监听接口类型
     * @throws Exception 可能产生的异常
     */
    void registerServer(Class<? extends WSServerListener> targetClass) throws Exception;

    /**
     * 注册客户端点监听器
     *
     * @param targetClass 客户端点监听接口类型
     * @throws Exception 可能产生的异常
     */
    void registerClient(Class<? extends WSClientListener> targetClass) throws Exception;

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