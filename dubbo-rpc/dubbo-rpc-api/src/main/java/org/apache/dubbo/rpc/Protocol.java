/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

/**
 * Protocol是整个dubbo框架rpc的和核心
 * Protocol是整个系统的服务域，负责对实体域Invoker的暴露和发布。
 * 服务域类似于工厂，实体域类似于机器流水线。　会话域类似于产品。
 * Protocol. (API/SPI, Singleton, ThreadSafe)
 */
@SPI("dubbo")
public interface Protocol {

    /**
     * Get default port when user doesn't config the port.
     *
     * 如果当前的服务没有指定端口，这个时候可以使用默认的端口实现。
     *
     * @return default port
     */
    int getDefaultPort();

    /**
     * Export service for remote invocation: <br>  将本地的service暴露给远端调用。
     * 1. Protocol should record request source address after receive a request:
     * RpcContext.getContext().setRemoteAddress();<br>
     *  Protocol在收到请求以后应该记录请求的源地址在ＲpcContext中。在其他的地方可能会使用到这个接口
     *
     * 2. export() must be idempotent, that is, there's no difference between invoking once and invoking twice when
     * export the same URL<br>
     * 3. Invoker instance is passed in by the framework, protocol needs not to care <br>
     *
     * 暴露一个远端服务端口。
     * @param <T>     Service type
     * @param invoker Service invoker
     * @return exporter reference for exported service, useful for unexport the service later
     * @throws RpcException thrown when error occurs during export the service, for example: port is occupied
     */
    @Adaptive
    <T> Exporter<T> export(Invoker<T> invoker) throws RpcException;

    /**
     * Refer a remote service: <br>
     *     引用一个远端服务　　根据ＵＲＬ和　class类型　生成一个　　Ｉnvoker对象
     * 1. When user calls `invoke()` method of `Invoker` object which's returned from `refer()` call, the protocol
     * needs to correspondingly execute `invoke()` method of `Invoker` object <br>
     *     当调用 invoke方法的时候，协议应当同时调用　远端的Invoker方法中的　invoke方法。
     *
     * 2. It's protocol's responsibility to implement `Invoker` which's returned from `refer()`. Generally speaking,
     * protocol sends remote request in the `Invoker` implementation. <br>
     *　Protocol的职责是将从refer方法中返回的Ｉnvoker对象，protocol通过Ｉnvoker发送请求，。
     *
     * 3. When there's check=false set in URL, the implementation must not throw exception but try to recover when
     * connection fails.
     * 如果ＵＲＬ中　check=false　如果链接失败，这个时候不要抛出一场。
     *
     * @param <T>  Service type
     * @param type Service class
     * @param url  URL address for the remote service
     * @return invoker service's local proxy
     * @throws RpcException when there's any error while connecting to the service provider
     */
    @Adaptive
    <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException;

    /**
     * Destroy protocol: <br>
     *     销毁当前协议对象
     * 1. Cancel all services this protocol exports and refers <br>
     *     删除所有的当前服务薄如的接口和引用
     * 2. Release all occupied resources, for example: connection, port, etc. <br>
     *     释放所有占用的资源。　连接端口等
     * 3. Protocol can continue to export and refer new service even after it's destroyed.
     *   Protocol能够继续　暴露服务和引用新的服务。
     */
    void destroy();

}