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

import com.cdancy.bitbucket.rest.domain.comment.MinimalAnchor;
import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class CreateTaskUpdate {

    public abstract MinimalAnchor anchor();

    public abstract int id();

    public abstract String state();

    public abstract int pullRequestId();

    public abstract int repositoryId();


    CreateTaskUpdate() {
    }

    @SerializedNames({"anchor", "id", "state", "pullRequestId", "repositoryId" })
    public static CreateTaskUpdate update(final MinimalAnchor anchor,
                                          final int id,
                                          final String state,
                                          final int pullRequestId,
                                          final int repositoryId) {
        return new AutoValue_CreateTaskUpdate(anchor, id, state, pullRequestId, repositoryId);
    }
}
