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
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Comments implements ErrorsHolder, LinksHolder {

    public abstract Map<String, String> properties();

    public abstract int id();

    public abstract int version();

    @Nullable
    public abstract String text();

    @Nullable
    public abstract Author author();

    public abstract long createdDate();

    public abstract long updatedDate();

    public abstract List<Comments> comments();

    @Nullable
    public abstract Link link();

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
        return new AutoValue_Comments(Utils.nullToEmpty(errors), links, Utils.nullToEmpty(properties),
                id, version, text, author, createdDate, updatedDate, Utils.nullToEmpty(comments), link);
    }
}
