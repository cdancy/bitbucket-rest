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
public abstract class MergeStrategy {

    public enum MergeStrategyId {
        no_ff("no-ff"),
        ff("ff"),
        ff_only("ff-only"),
        squash("squash"),
        squash_ff_only("squash-ff-only");

        private String apiName;

        MergeStrategyId(String apiName) {
            this.apiName = apiName;
        }

        public String getApiName() {
            return apiName;
        }

        /**
         * Convert value from Api to enum.
         *
         * @param apiName ApiName
         * @return value
         */
        public static MergeStrategyId fromValue(String apiName) {
            for (MergeStrategyId enumType : MergeStrategyId.values()) {
                if (enumType.getApiName().equals(apiName)) {
                    return enumType;
                }
            }
            throw new IllegalArgumentException("Value " + apiName + " is not a legal MergeStrategy type");
        }

        @Override
        public String toString() {
            return this.getApiName();
        }
    }

    @Nullable
    public abstract String description();

    @Nullable
    public abstract Boolean enabled();

    @Nullable
    public abstract String flag();

    public abstract MergeStrategyId id();

    @Nullable
    public abstract String name();

    @SerializedNames({ "description", "enabled", "flag", "id", "name"})
    public static MergeStrategy create(String description, Boolean enabled, String flag, MergeStrategyId id, String name) {
        return new AutoValue_MergeStrategy(description, enabled, flag, id, name);
    }
}
