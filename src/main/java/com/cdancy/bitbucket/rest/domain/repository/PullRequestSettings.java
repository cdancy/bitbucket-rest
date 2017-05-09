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
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class PullRequestSettings {

    public abstract MergeConfig mergeConfig();

    public abstract boolean requiredAllApprovers();

    public abstract boolean requiredAllTasksComplete();

    public abstract long requiredApprovers();

    public abstract long requiredSuccessfulBuilds();

    @SerializedNames({ "mergeConfig", "requiredAllApprovers", "requiredAllTasksComplete", "requiredApprovers",
            "requiredSuccessfulBuilds" })
    public static PullRequestSettings create(MergeConfig mergeConfig, boolean requiredAllApprovers,
                                             boolean requiredAllTasksComplete, long requiredApprovers,
                                             long requiredSuccessfulBuilds) {
        return new AutoValue_PullRequestSettings(mergeConfig, requiredAllApprovers, requiredAllTasksComplete,
            requiredApprovers, requiredSuccessfulBuilds);
    }
}
