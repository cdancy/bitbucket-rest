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

package com.cdancy.bitbucket.rest.domain.branch;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class Branch implements ErrorsHolder {

    @Nullable
    public abstract String id();

    @Nullable
    public abstract String displayId();

    @Nullable
    public abstract String type();

    @Nullable
    public abstract String latestCommit();

    @Nullable
    public abstract String latestChangeset();

    public abstract boolean isDefault();

    // This map consists of data provided by plugins and so
    // is non-standard in how it's returned and the fields
    // it has. As such we return the raw JsonElement and instead
    // let the caller iterate through the returned plugin data
    // for what they are looking for.
    public abstract Map<String, JsonElement> metadata();

    Branch() {
    }

    @SerializedNames({ "id", "displayId", "type", "latestCommit", "latestChangeset", "isDefault", "metadata", "errors" })
    public static Branch create(String id, String displayId, String type,
                                String latestCommit, String latestChangeset,
                                boolean isDefault, Map<String, JsonElement> metadata, List<Error> errors) {

        return new AutoValue_Branch(Utils.nullToEmpty(errors), id, displayId, type,
                latestCommit, latestChangeset, isDefault, Utils.nullToEmpty(metadata));
    }

    public static TypeAdapter<Branch> typeAdapter(Gson gson) {
        return new AutoValue_Branch.GsonTypeAdapter(gson);
    }
}
