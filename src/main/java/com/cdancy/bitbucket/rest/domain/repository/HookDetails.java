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

package com.cdancy.bitbucket.rest.domain.repository;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class HookDetails {

    public enum HookDetailsType {
        PRE_RECEIVE,
        POST_RECEIVE
    }

    public abstract String key();

    public abstract String name();

    public abstract HookDetailsType type();

    public abstract String description();

    public abstract String version();

    @Nullable
    public abstract String configFormKey();

    @SerializedNames({ "key", "name", "type", "description", "version", "configFormKey" })
    public static HookDetails create(String key, String name, HookDetailsType type, String description, String version,
                                  String configFormKey) {
        return new AutoValue_HookDetails(key, name, type, description, version, configFormKey);
    }
}
