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

package com.cdancy.bitbucket.rest.domain.postwebhooks;

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class PostWebHook implements ErrorsHolder {
    public abstract boolean branchCreated();

    public abstract boolean branchDeleted();

    @Nullable
    public abstract String branchesToIgnore();

    @Nullable
    public abstract String committersToIgnore();

    public abstract boolean enabled();

    public abstract boolean prCommented();

    public abstract boolean prCreated();

    public abstract boolean prDeclined();

    public abstract boolean prMerged();

    public abstract boolean prReopened();

    public abstract boolean prRescoped();

    public abstract boolean prUpdated();

    public abstract boolean repoMirrorSynced();

    public abstract boolean repoPush();

    public abstract boolean tagCreated();

    @Nullable
    public abstract String title();

    @Nullable
    public abstract String url();


    PostWebHook() {
    }

    @SerializedNames({"branchCreated", "branchDeleted", "branchesToIgnore", "committersToIgnore",
            "enabled", "prCommented", "prCreated", "prDeclined", "prMerged", "prReopened", "prRescoped", "prUpdated",
            "repoMirrorSynced", "repoPush", "tagCreated", "title", "url", "errors"})
    public static PostWebHook create(final boolean branchCreated,
                                     final boolean branchDeleted,
                                     final String branchesToIgnore,
                                     final String committersToIgnore,
                                     final boolean enabled,
                                     final boolean prCommented,
                                     final boolean prCreated,
                                     final boolean prDeclined,
                                     final boolean prMerged,
                                     final boolean prReopened,
                                     final boolean prRescoped,
                                     final boolean prUpdated,
                                     final boolean repoMirrorSynced,
                                     final boolean repoPush,
                                     final boolean tagCreated,
                                     final String title,
                                     final String url,
                                     @Nullable final List<Error> errors) {

        return new AutoValue_PostWebHook(BitbucketUtils.nullToEmpty(errors),
            branchCreated,
            branchDeleted,
            branchesToIgnore,
            committersToIgnore,
            enabled,
            prCommented,
            prCreated,
            prDeclined,
            prMerged,
            prReopened,
            prRescoped,
            prUpdated,
            repoMirrorSynced,
            repoPush,
            tagCreated,
            title,
            url);
    }
}
