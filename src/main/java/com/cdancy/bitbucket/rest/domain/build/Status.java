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

package com.cdancy.bitbucket.rest.domain.build;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Status {

    public enum StatusState {
        SUCCESSFUL,
        FAILED,
        INPROGRESS
    }

    public abstract long dateAdded();

    public abstract String description();

    public abstract String key();

    public abstract String name();

    public abstract StatusState state();

    public abstract String url();

    @SerializedNames({"dateAdded", "description", "key", "name", "state", "url"})
    public static Status create(long dateAdded, String description, String key, String name, StatusState state,
                                String url) {
        return new AutoValue_Status(dateAdded, description, key, name, state, url);
    }

    public static TypeAdapter<Status> typeAdapter(Gson gson) {
        return new AutoValue_Status.GsonTypeAdapter(gson);
    }
}
