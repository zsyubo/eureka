/*
 * Copyright 2012 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.netflix.discovery.converters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一个字段注解，它有助于避免每次向 com.netflix.appinfo.InstanceInfo 添加额外字段时对 Converters.InstanceInfoConverter 的更改。
 * 这个注解通知 Converters.InstanceInfoConverter 自动调用 com.netflix.appinfo.InstanceInfo 类中声明的大多数原始字段。
 * <p></p>
 * A field annotation which helps in avoiding changes to
 * {@link com.netflix.discovery.converters.Converters.InstanceInfoConverter} every time additional fields are added to
 * {@link com.netflix.appinfo.InstanceInfo}.
 *
 * <p>
 * This annotation informs the {@link com.netflix.discovery.converters.Converters.InstanceInfoConverter} to
 * automatically marshall most primitive fields declared in the {@link com.netflix.appinfo.InstanceInfo} class.
 * </p>
 *
 * @author Karthik Ranganathan
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Auto {
}
