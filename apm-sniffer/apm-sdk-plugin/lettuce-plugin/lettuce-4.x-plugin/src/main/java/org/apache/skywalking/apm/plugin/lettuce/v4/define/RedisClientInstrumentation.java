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
 *
 */

package org.apache.skywalking.apm.plugin.lettuce.v4.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.plugin.lettuce.v4.RedisClientInterceptor;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static org.apache.skywalking.apm.agent.core.plugin.bytebuddy.ArgumentTypeNameMatch.takesArgumentWithType;
import static org.apache.skywalking.apm.agent.core.plugin.match.NameMatch.byName;

/**
 * 1{@link RedisClientInstrumentation} defined that {@link RedisClientInterceptor} intercept the constructor and  the
 * method newStatefulRedisConnection in the class <code>com.lambdaworks.redis.RedisClient</code>.
 * <pre>
 *  1. Record the peer when  create the <code>com.lambdaworks.redis.RedisClient</code> instance
 * </pre>
 *
 * @author liyuntao
 */
public class RedisClientInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "com.lambdaworks.redis.RedisClient";
    private static final String INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.lettuce.v4.RedisClientInterceptor";

    @Override
    public ClassMatch enhanceClass() {
        return byName(ENHANCE_CLASS);
    }

    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[] {
            new ConstructorInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getConstructorMatcher() {
                    return takesArgumentWithType(1, "com.lambdaworks.redis.RedisURI");
                }

                @Override
                public String getConstructorInterceptor() {
                    return INTERCEPT_CLASS;
                }
            }
        };
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("newStatefulRedisConnection");
                }

                @Override
                public String getMethodsInterceptor() {
                    return INTERCEPT_CLASS;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }
}