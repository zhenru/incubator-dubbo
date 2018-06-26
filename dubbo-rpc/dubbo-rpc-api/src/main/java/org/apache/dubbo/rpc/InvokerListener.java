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
import org.apache.dubbo.common.extension.SPI;

/**
 * InvokerListener. (SPI, Singleton, ThreadSafe)
 * 这个是主要是将添加一个Ｉnvoker对象中的生命周期中的一些方法的实现。
 */
@SPI
public interface InvokerListener {

    /**
     * The invoker referred
     *　当前服务引用完成以后，会调用这个方法。
     * @param invoker
     * @throws RpcException
     * @see org.apache.dubbo.rpc.Protocol#refer(Class, URL)
     */
    void referred(Invoker<?> invoker) throws RpcException;

    /**
     * The invoker destroyed.
     *　当这个　服务销毁以后，这个时候可以使用调用这个方法。这个需要看一下当前系统后期修改一下。
     * @param invoker
     * @see org.apache.dubbo.rpc.Invoker#destroy()
     */
    void destroyed(Invoker<?> invoker);

}