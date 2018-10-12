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

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;

import com.google.auto.value.AutoValue;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class User implements ErrorsHolder {

    @Nullable
    public abstract String name();

    @Nullable
    public abstract String emailAddress();

    @Nullable
    public abstract Integer id();

    @Nullable
    public abstract String displayName();

    @Nullable
    public abstract Boolean active();

    @Nullable
    public abstract String slug();

    @Nullable
    public abstract String type();

    @Nullable
    public abstract String directoryName();

    @Nullable
    public abstract Boolean deletable();

    @Nullable
    public abstract Long lastAuthenticationTimestamp();

    @Nullable
    public abstract Boolean mutableDetails();

    @Nullable
    public abstract Boolean mutableGroups();

    User() {
    }

    public static User create(final String name, final String emailAddress, final int id, final String displayName,
                              final boolean active, final String slug, final String type) {

        return new AutoValue_User(Collections.emptyList(), name, emailAddress, id, displayName, active, slug, type,
            null, null, null, null, null);
    }

    @SerializedNames({ "errors", "name", "emailAddress",
            "id", "displayName", "active",
            "slug", "type", "directoryName",
            "deletable", "lastAuthenticationTimestamp",
            "mutableDetails", "mutableGroups" })
    public static User create(final List<Error> errors, final String name,
            final String emailAddress, final int id,
            final String displayName,final boolean active,
            final String slug, final String type,
            final String directoryName, final boolean deletable,
            final long lastAuthenticationTimestamp, final boolean mutableDetails,
            final boolean mutableGroups) {

        return new AutoValue_User(BitbucketUtils.nullToEmpty(errors), name, emailAddress, id, displayName,
            active, slug, type, directoryName, deletable, lastAuthenticationTimestamp, mutableDetails, mutableGroups);
    }
}
