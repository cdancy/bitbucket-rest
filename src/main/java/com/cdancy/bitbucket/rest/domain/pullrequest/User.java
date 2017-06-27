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

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class User {

    public abstract String name();

    public abstract String emailAddress();

    public abstract int id();

    public abstract String displayName();

    public abstract boolean active();

    public abstract String slug();

    public abstract String type();

    User() {
    }

    @SerializedNames({ "name", "emailAddress", "id", "displayName", "active", "slug", "type" })
    public static User create(String name, String emailAddress, int id,
                              String displayName, boolean active, String slug, String type) {
        return new AutoValue_User(name, emailAddress, id, displayName, active, slug, type);
    }

    public static TypeAdapter<User> typeAdapter(Gson gson) {
        return new AutoValue_User.GsonTypeAdapter(gson);
    }
}
