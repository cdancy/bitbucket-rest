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

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Change {

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
    public abstract Links links();

    Change() {
    }

    @SerializedNames({ "contentId", "fromContentId", "path", "executable",
            "percentUnchanged", "type", "nodeType", "srcPath",
            "srcExecutable", "links" })
    public static Change create(String contentId, String fromContentId, Path path,
                                boolean executable, int percentUnchanged, String type,
                                String nodeType, Path srcPath, boolean srcExecutable,
                                Links links) {
        return new AutoValue_Change(contentId, fromContentId, path, executable,
                percentUnchanged, type, nodeType, srcPath, srcExecutable, links);
    }
}
