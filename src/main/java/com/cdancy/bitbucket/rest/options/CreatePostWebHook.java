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

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class CreatePostWebHook {
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

    public abstract String title();

    public abstract String url();

    CreatePostWebHook() {
    }

    @SerializedNames({"branchCreated", "branchDeleted", "branchesToIgnore", "committersToIgnore",
            "enabled", "prCommented", "prCreated", "prDeclined", "prMerged", "prReopened", "prRescoped", "prUpdated",
            "repoMirrorSynced", "repoPush", "tagCreated", "title", "url"})
    public static CreatePostWebHook create(final boolean branchCreated,
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
                                           final String url) {
        return new AutoValue_CreatePostWebHook(
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
