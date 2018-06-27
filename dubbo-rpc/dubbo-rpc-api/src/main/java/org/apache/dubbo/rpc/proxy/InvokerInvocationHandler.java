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
package org.apache.dubbo.rpc.proxy;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.support.RpcUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 这个是从Invoker到一个Proxy的InvocationHandler的实现类。
 * InvokerHandler
 *
 */
public class InvokerInvocationHandler implements InvocationHandler {

    private final Invoker<?> invoker;

    public InvokerInvocationHandler(Invoker<?> handler) {
        this.invoker = handler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }

        RpcInvocation invocation;
        //如果这个对象是　一个异步的方法。其中这里对异步的方法名称做了一个特殊的处理，就是在方法名称尾部加上了一个ＡＳＹＮＣ－ＳＵＦＦＩＸ
        if (RpcUtils.isAsyncFuture(method)) {
            Class<?> clazz = method.getDeclaringClass();
            String syncMethodName = methodName.substring(0, methodName.length() - Constants.ASYNC_SUFFIX.length());
            //获取到对应的方法实体。
            Method syncMethod = clazz.getMethod(syncMethodName, method.getParameterTypes());
            //将这个对象封装为一个RpcInvocation对象。同时将远端调用移动到远端去。
            invocation = new RpcInvocation(syncMethod, args);
            invocation.setAttachment(Constants.FUTURE_KEY, "true");
            invocation.setAttachment(Constants.ASYNC_KEY, "true");
        } else {
            invocation = new RpcInvocation(method, args);
        }
        //通过当前的invoker去调用Invocation对象来实现对与远端的调用。
        return invoker.invoke(invocation).recreate();
    }


}
