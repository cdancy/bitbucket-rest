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

import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.pullrequest.Links;
import com.cdancy.bitbucket.rest.domain.pullrequest.ProjectKey;
import com.cdancy.bitbucket.rest.error.Error;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class Repository {

    @Nullable
    public abstract String slug();

    public abstract int id();

    @Nullable
    public abstract String name();

    @Nullable
    public abstract String scmId();

    @Nullable
    public abstract String state();

    @Nullable
    public abstract String statusMessage();

    public abstract boolean forkable();

    @Nullable
    public abstract Project project();

    public abstract boolean _public();

    @Nullable
    public abstract Links links();

    @Nullable
    public abstract List<Error> errors();

    Repository() {
    }

    @SerializedNames({ "slug", "id", "name", "scmId", "state", "statusMessage", "forkable", "project", "public", "links", "errors" })
    public static Repository create(String slug, int id, String name, String scmId,
                                    String state, String statusMessage, boolean forkable,
                                    Project project, boolean _public, Links links, List<Error> errors) {
        return new AutoValue_Repository(slug, id, name, scmId, state, statusMessage,
                forkable, project, _public, links,
                errors != null ? ImmutableList.copyOf(errors) : ImmutableList.<Error> of());
    }
}
