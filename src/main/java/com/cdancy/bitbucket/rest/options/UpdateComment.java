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

import com.cdancy.bitbucket.rest.domain.comment.Anchor;
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.comment.Parent;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class UpdateComment {

    @Nullable
    public abstract String text();

    @Nullable
    public abstract Parent parent();

    @Nullable
    public abstract Anchor anchor();

    @Nullable
    public abstract Comments.Severity severity();

    @Nullable
    public abstract Comments.TaskState state();

    public abstract int version();

    UpdateComment() {
    }

    @SerializedNames({ "text", "parent", "anchor", "severity", "state", "version" })
    public static UpdateComment create(final String text,
                                       final Parent parent,
                                       final Anchor anchor,
                                       final Comments.Severity severity,
                                       final Comments.TaskState state,
                                       final int version) {
        return new AutoValue_UpdateComment(text, parent, anchor, severity, state, version);
    }
}
