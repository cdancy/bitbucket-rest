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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class MinimalRepository {

    public abstract String slug();

    @Nullable
    public abstract String name();

    public abstract ProjectKey project();

    MinimalRepository() {
    }

    @SerializedNames({ "slug", "name", "project" })
    public static MinimalRepository create(String slug, String name, ProjectKey project) {
        return new AutoValue_MinimalRepository(slug, name, project);
    }

    public static TypeAdapter<MinimalRepository> typeAdapter(Gson gson) {
        return new AutoValue_MinimalRepository.GsonTypeAdapter(gson);
    }
}
