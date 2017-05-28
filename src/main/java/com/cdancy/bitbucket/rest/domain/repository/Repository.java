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

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.common.Links;
import com.cdancy.bitbucket.rest.domain.common.LinksHolder;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Repository implements ErrorsHolder, LinksHolder {

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

    Repository() {
    }

    @SerializedNames({ "slug", "id", "name", "scmId", 
            "state", "statusMessage", "forkable", "project", 
            "public", "links", "errors" })
    public static Repository create(final String slug, 
            final int id, 
            final String name, 
            final String scmId,
            final String state, 
            final String statusMessage, 
            final boolean forkable,
            final Project project, 
            final boolean _public, 
            final Links links, 
            final List<Error> errors) {
        
        return new AutoValue_Repository(Utils.nullToEmpty(errors), 
                links, 
                slug, 
                id, 
                name, 
                scmId, 
                state,
                statusMessage, 
                forkable, 
                project, 
                _public);
    }
}
