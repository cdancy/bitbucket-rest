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

package com.cdancy.bitbucket.rest.domain.pullrequest;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.Links;
import com.cdancy.bitbucket.rest.domain.common.LinksHolder;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Change implements LinksHolder {

    public abstract String contentId();

    @Nullable
    public abstract String fromContentId();

    public abstract Path path();

    public abstract boolean executable();

    public abstract int percentUnchanged();

    @Nullable
    public abstract String type();

    @Nullable
    public abstract String nodeType();

    @Nullable
    public abstract Path srcPath();

    public abstract boolean srcExecutable();

    @Nullable
    public abstract Conflict conflict();

    Change() {
    }

    @SerializedNames({ "contentId", "fromContentId", "path",
            "executable", "percentUnchanged", "type",
            "nodeType", "srcPath", "srcExecutable",
            "links", "conflict" })
    public static Change create(final String contentId,
            final String fromContentId,
            final Path path,
            final boolean executable,
            final int percentUnchanged,
            final String type,
            final String nodeType,
            final Path srcPath,
            final boolean srcExecutable,
            final Links links,
            final Conflict conflict) {

        return new AutoValue_Change(links,
                contentId,
                fromContentId,
                path,
                executable,
                percentUnchanged,
                type,
                nodeType,
                srcPath,
                srcExecutable,
                conflict);
    }
}
