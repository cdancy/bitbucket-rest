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

package com.cdancy.bitbucket.rest.options;

import com.cdancy.bitbucket.rest.domain.repository.MergeConfig;
import com.cdancy.bitbucket.rest.domain.repository.PullRequestSettings;
import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class CreatePullRequestSettings {

    public abstract MergeConfig mergeConfig();

    public abstract boolean requiredAllApprovers();

    public abstract boolean requiredAllTasksComplete();

    public abstract long requiredApprovers();

    public abstract long requiredSuccessfulBuilds();

    public abstract boolean unapproveOnUpdate();

    public static CreatePullRequestSettings create(final PullRequestSettings pullRequestSettings) {
        return new AutoValue_CreatePullRequestSettings(pullRequestSettings.mergeConfig(),
            pullRequestSettings.requiredAllApprovers(), pullRequestSettings.requiredAllTasksComplete(),
            pullRequestSettings.requiredApprovers(), pullRequestSettings.requiredSuccessfulBuilds(),
            pullRequestSettings.unapproveOnUpdate());
    }

    @SerializedNames({ "mergeConfig", "requiredAllApprovers", "requiredAllTasksComplete",
            "requiredApprovers", "requiredSuccessfulBuilds", "unapproveOnUpdate" })
    public static CreatePullRequestSettings create(final MergeConfig mergeConfig,
            final boolean requiredAllApprovers,
            final boolean requiredAllTasksComplete,
            final long requiredApprovers,
            final long requiredSuccessfulBuilds,
            final boolean unapproveOnUpdate) {
        return new AutoValue_CreatePullRequestSettings(mergeConfig, requiredAllApprovers, requiredAllTasksComplete,
            requiredApprovers, requiredSuccessfulBuilds, unapproveOnUpdate);
    }
}
