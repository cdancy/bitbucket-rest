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

import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.common.Links;
import com.cdancy.bitbucket.rest.domain.common.LinksHolder;
import com.cdancy.bitbucket.rest.domain.pullrequest.Author;
import com.cdancy.bitbucket.rest.utils.BitbucketUtils;
import com.google.auto.value.AutoValue;
import com.google.gson.JsonElement;

@AutoValue
public abstract class Comments implements ErrorsHolder, LinksHolder {

    public abstract Map<String, JsonElement> properties();

    public abstract int id();

    public abstract int version();

    @Nullable
    public abstract String text();

    @Nullable
    public abstract Author author();

    public abstract long createdDate();

    public abstract long updatedDate();

    public abstract List<Comments> comments();
    
    public abstract List<Task> tasks();
        
    @Nullable
    public abstract Anchor anchor();
    
    @Nullable
    public abstract Link link();

    @Nullable
    public abstract PermittedOperations permittedOperations();
    
    Comments() {
    }

    @SerializedNames({ "properties", "id", "version", "text", "author",
            "createdDate", "updatedDate", "comments", "tasks", "anchor", "link", "links", 
            "permittedOperations", "errors" })
    public static Comments create(final Map<String, JsonElement> properties,
                                  final int id,
                                  final int version,
                                  final String text,
                                  final Author author,
                                  final long createdDate,
                                  final long updatedDate,
                                  final List<Comments> comments,
                                  final List<Task> tasks,
                                  final Anchor anchor,
                                  final Link link,
                                  final Links links,
                                  final PermittedOperations permittedOperations,
                                  final List<Error> errors) {
        
        return new AutoValue_Comments(BitbucketUtils.nullToEmpty(errors), 
                links, 
                BitbucketUtils.nullToEmpty(properties),
                id, 
                version, 
                text, 
                author, 
                createdDate, 
                updatedDate, 
                BitbucketUtils.nullToEmpty(comments), 
                BitbucketUtils.nullToEmpty(tasks), 
                anchor, 
                link, 
                permittedOperations);
    }
}
