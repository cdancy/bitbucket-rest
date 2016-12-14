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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class Commit {

    public abstract String id();

    public abstract String displayId();

    public abstract Author author();

    public abstract long authorTimestamp();

    public abstract String message();

    public abstract List<Parents> parents();

    Commit() {
    }

    @SerializedNames({ "id", "displayId", "author", "authorTimestamp",
            "message", "parents" })
    public static Commit create(String id, String displayId, Author author,
                                long authorTimestamp, String message, List<Parents> parents) {
        return new AutoValue_Commit(id, displayId, author, authorTimestamp, message,
                parents != null ? ImmutableList.copyOf(parents) : ImmutableList.<Parents>of());
    }
}
