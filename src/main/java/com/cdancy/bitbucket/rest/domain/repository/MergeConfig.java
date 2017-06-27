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
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class MergeConfig {

    public enum MergeConfigType {
        REPOSITORY,
        DEFAULT
    }

    public abstract MergeStrategy defaultStrategy();

    public abstract List<MergeStrategy> strategies();

    public abstract MergeConfigType type();

    @SerializedNames({ "defaultStrategy", "strategies", "type"})
    public static MergeConfig create(MergeStrategy defaultStrategy, List<MergeStrategy> strategies, MergeConfigType type) {
        return new AutoValue_MergeConfig(defaultStrategy, strategies, type);
    }

    public static TypeAdapter<MergeConfig> typeAdapter(Gson gson) {
        return new AutoValue_MergeConfig.GsonTypeAdapter(gson);
    }
}
