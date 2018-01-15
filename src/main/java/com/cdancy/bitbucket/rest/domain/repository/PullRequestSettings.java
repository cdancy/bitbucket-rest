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

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class PullRequestSettings implements ErrorsHolder {

    @Nullable
    public abstract MergeConfig mergeConfig();

    @Nullable
    public abstract Boolean requiredAllApprovers();

    @Nullable
    public abstract Boolean requiredAllTasksComplete();

    @Nullable
    public abstract Long requiredApprovers();

    @Nullable
    public abstract Long requiredSuccessfulBuilds();

    @SerializedNames({ "mergeConfig", "requiredAllApprovers", 
            "requiredAllTasksComplete", "requiredApprovers",
            "requiredSuccessfulBuilds", "errors" })
    public static PullRequestSettings create(final MergeConfig mergeConfig, 
            final Boolean requiredAllApprovers,
            final Boolean requiredAllTasksComplete, 
            final Long requiredApprovers,
            final Long requiredSuccessfulBuilds, 
            @Nullable final List<Error> errors) {
        
        return new AutoValue_PullRequestSettings(BitbucketUtils.nullToEmpty(errors), 
                mergeConfig, 
                requiredAllApprovers,
                requiredAllTasksComplete, 
                requiredApprovers, 
                requiredSuccessfulBuilds);
    }
}
