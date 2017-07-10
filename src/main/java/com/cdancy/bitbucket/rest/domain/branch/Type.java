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

package com.cdancy.bitbucket.rest.domain.branch;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Type {

    public enum TypeId {
        BUGFIX,
        FEATURE,
        HOTFIX,
        RELEASE
    }

    public abstract TypeId id();

    @Nullable
    public abstract String displayName();

    public abstract String prefix();

    public abstract boolean enabled();

    Type() {
    }

    @Deprecated
    public static Type create(TypeId id, String displayName, String prefix) {
        return create(id, displayName, prefix, null);
    }

    @SerializedNames({ "id", "displayName", "prefix", "enabled" })
    public static Type create(TypeId id, String displayName, String prefix, Boolean enabled) {
        return new AutoValue_Type(id, displayName, prefix, enabled != null ? enabled : false);
    }

    public static TypeAdapter<Type> typeAdapter(Gson gson) {
        return new AutoValue_Type.GsonTypeAdapter(gson);
    }
}
