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

import com.cdancy.bitbucket.rest.domain.common.Reference;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.common.Links;
import com.cdancy.bitbucket.rest.domain.common.LinksHolder;
import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PullRequest implements ErrorsHolder, LinksHolder {

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

    @Nullable
    public abstract Long closedDate();

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
    public abstract Properties properties();

    PullRequest() {
    }

    @SerializedNames({ "id", "version", "title", "description",
            "state", "open", "closed", "closedDate", "createdDate",
            "updatedDate", "fromRef", "toRef", "locked",
            "author", "reviewers", "participants", "properties",
            "links", "errors" })
    public static PullRequest create(final int id,
            final int version,
            final String title,
            final String description,
            final String state,
            final boolean open,
            final boolean closed,
            final Long closedDate,
            final long createdDate,
            final long updatedDate,
            final Reference fromRef,
            final Reference toRef,
            final boolean locked,
            final Person author,
            final List<Person> reviewers,
            final List<Person> participants,
            final Properties properties,
            final Links links,
            final List<Error> errors) {

        return new AutoValue_PullRequest(BitbucketUtils.nullToEmpty(errors),
                links,
                id,
                version,
                title,
                description,
                state,
                open,
                closed,
                closedDate,
                createdDate,
                updatedDate,
                fromRef,
                toRef,
                locked,
                author,
                BitbucketUtils.nullToEmpty(reviewers),
                BitbucketUtils.nullToEmpty(participants),
                properties);
    }
}
