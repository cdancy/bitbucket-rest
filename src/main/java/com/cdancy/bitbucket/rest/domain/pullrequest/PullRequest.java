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

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PullRequest implements ErrorsHolder {

    public abstract int id();

    public abstract int version();

    @Nullable
    public abstract String title();

    @Nullable
    public abstract String description();

    @Nullable
    public abstract String state();

    public abstract boolean open();

    public abstract boolean closed();

    public abstract long createdDate();

    public abstract long updatedDate();

    @Nullable
    public abstract Reference fromRef();

    @Nullable
    public abstract Reference toRef();

    public abstract boolean locked();

    @Nullable
    public abstract Person author();

    public abstract List<Person> reviewers();

    public abstract List<Person> participants();

    @Nullable
    public abstract Links links();

    PullRequest() {
    }

    @SerializedNames({ "id", "version", "title", "description", "state", "open", "closed", "createdDate", "updatedDate",
            "fromRef", "toRef", "locked", "author", "reviewers", "participants", "links", "errors" })
    public static PullRequest create(int id, int version, String title, String description, String state, boolean open,
                                     boolean closed, long createdDate, long updatedDate, Reference fromRef, Reference toRef, boolean locked,
                                     Person author, List<Person> reviewers, List<Person> participants, Links links, List<Error> errors) {
        return new AutoValue_PullRequest(Utils.nullToEmpty(errors), id, version, title, description, state, open, closed, createdDate,
                updatedDate, fromRef, toRef, locked, author, Utils.nullToEmpty(reviewers), Utils.nullToEmpty(participants), links);
    }
}
