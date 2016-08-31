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

package com.cdancy.bitbucket.rest.domain.comment;

import com.cdancy.bitbucket.rest.domain.pullrequest.Author;
import com.cdancy.bitbucket.rest.domain.pullrequest.Links;
import com.cdancy.bitbucket.rest.error.Error;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class Comments {

    public abstract Map<String, String> properties();

    public abstract int id();

    public abstract int version();

    public abstract String text();

    public abstract Author author();

    public abstract long createdDate();

    public abstract long updatedDate();

    public abstract List<Comments> comments();

    @Nullable
    public abstract Link link();

    @Nullable
    public abstract Links links();

    @Nullable
    public abstract List<Error> errors();

    Comments() {
    }

    @SerializedNames({ "properties", "id", "version", "text", "author",
            "createdDate", "updatedDate", "comments", "link", "links", "errors" })
    public static Comments create(Map<String, String> properties,
                                  int id,
                                  int version,
                                  String text,
                                  Author author,
                                  long createdDate,
                                  long updatedDate,
                                  List<Comments> comments,
                                  Link link,
                                  Links links,
                                  List<Error> errors) {
        return new AutoValue_Comments(properties != null ? ImmutableMap.copyOf(properties) : ImmutableMap.<String, String>of(),
                id,
                version,
                text,
                author,
                createdDate,
                updatedDate,
                comments != null ? ImmutableList.copyOf(comments) : ImmutableList.<Comments>of(),
                link,
                links,
                errors != null ? ImmutableList.copyOf(errors) : ImmutableList.<Error> of());
    }
}
