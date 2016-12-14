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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.comment.Link;
import com.cdancy.bitbucket.rest.domain.common.Links;
import com.cdancy.bitbucket.rest.domain.common.LinksHolder;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Author implements LinksHolder {

    public abstract String name();

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
    public abstract Link link();

    Author() {
    }

    @SerializedNames({ "name", "emailAddress", "id", "displayName", "active", "slug", "type", "link", "links" })
    public static Author create(String name,
                                String emailAddress,
                                Integer id,
                                String displayName,
                                Boolean active,
                                String slug,
                                String type,
                                Link link,
                                Links links) {
        return new AutoValue_Author(links, name, emailAddress, id, displayName, active, slug, type, link);
    }
}
