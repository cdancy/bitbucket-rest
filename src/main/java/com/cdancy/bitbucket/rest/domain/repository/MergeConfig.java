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

import java.util.List;

@AutoValue
public abstract class MergeConfig {

    public enum MergeConfigType {
        REPOSITORY,
        DEFAULT,
        PROJECT
    }

    public abstract MergeStrategy defaultStrategy();

    public abstract List<MergeStrategy> strategies();

    public abstract MergeConfigType type();

    @Nullable
    public abstract Integer commitSummaries();

    @SerializedNames({ "defaultStrategy", "strategies", "type", "commitSummaries"})
    public static MergeConfig create(final MergeStrategy defaultStrategy,
            final List<MergeStrategy> strategies,
            final MergeConfigType type,
            final Integer commitSummaries) {

        return new AutoValue_MergeConfig(defaultStrategy, strategies, type, commitSummaries);
    }
}
