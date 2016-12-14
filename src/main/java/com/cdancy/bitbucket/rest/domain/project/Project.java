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

package com.cdancy.bitbucket.rest.domain.project;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.common.Utils;
import com.cdancy.bitbucket.rest.domain.pullrequest.Links;
import com.cdancy.bitbucket.rest.error.Error;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Project implements ErrorsHolder {

    @Nullable
    public abstract String key();

    public abstract int id();

    @Nullable
    public abstract String name();

    @Nullable
    public abstract String description();

    public abstract boolean _public();

    @Nullable
    public abstract String type();

    @Nullable
    public abstract Links links();

    Project() {
    }

    @SerializedNames({ "key", "id", "name", "description", "public", "type", "links", "errors" })
    public static Project create(String key, int id, String name, String description,
                                 boolean _public, String type, Links links, List<Error> errors) {
        return new AutoValue_Project(Utils.nullToEmpty(errors), key, id, name, description, _public, type, links);
    }
}
