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

package com.cdancy.bitbucket.rest.domain.repository;

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class WebHook implements ErrorsHolder {

    @Nullable
    public abstract String id();

    @Nullable
    public abstract String name();

    public abstract long createdDate();

    public abstract long updatedDate();

    @Nullable
    public abstract List<EventType> events();

    @Nullable
    public abstract WebHookConfiguration configuration();

    @Nullable
    public abstract String url();

    public abstract boolean active();

    WebHook() {
    }

    public enum EventType {
        REPO_COMENT_ADDED("repo:comment:added"),
        REPO_COMENT_EDITED("repo:comment:edited"),
        REPO_COMENT_DELETED("repo:comment:deleted"),

        REPO_FORKED("repo:forked"),
        REPO_CHANGED("repo:refs_changed"),
        REPO_MODIFIED("repo:modified"),

        PR_COMENT_ADDED("pr:comment:added"),
        PR_COMENT_EDITED("pr:comment:edited"),
        PR_COMENT_DELETED("pr:comment:deleted"),

        PR_REVIEWER_UPDATE("pr:reviewer:updated"),
        PR_REVIEWER_UNAPPROVED("pr:reviewer:unapproved"),
        PR_REVIEWER_APPROVED("pr:reviewer:approved"),
        PR_REVIEWER_NEEDSWORK("pr:reviewer:needs_work"),

        PR_DELETED("pr:deleted"),
        PR_MERGED("pr:merged"),
        PR_MODIFIED("pr:modified"),
        PR_DECLINED("pr:declined"),
        PR_OPENED("pr:opened");

        private final String apiName;

        EventType(final String apiName) {
            this.apiName = apiName;
        }

        public String getApiName() {
            return apiName;
        }

        /**
         * Convert value from Api to enum.
         *
         * @param apiName ApiName
         * @return value
         */
        public static EventType fromValue(final String apiName) {
            for (final EventType enumType : EventType.values()) {
                if (enumType.getApiName().equals(apiName)) {
                    return enumType;
                }
            }
            throw new IllegalArgumentException("Value " + apiName + " is not a legal EventType type");
        }

        @Override
        public String toString() {
            return this.getApiName();
        }
    }

    @SerializedNames({ "id", "name", "createdDate", "updatedDate", "events",
            "configuration", "url", "active", "errors" })
    public static WebHook create(final String id,
                                 final String name,
                                 final long createdDate,
                                 final long updatedDate,
                                 @Nullable final List<EventType> events,
                                 final WebHookConfiguration configuration,
                                 final String url,
                                 final boolean active,
                                 @Nullable final List<Error> errors) {

        return new AutoValue_WebHook(BitbucketUtils.nullToEmpty(errors),
            id,
            name,
            createdDate,
            updatedDate,
            BitbucketUtils.nullToEmpty(events),
            configuration,
            url,
            active);
    }
}
