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

package com.cdancy.bitbucket.rest.domain.activities;

import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class Activities {

    public enum ActivitiesType {
        DECLINED,
        RESCOPED,
        APPROVED,
        REVIEWED,
        COMMENTED,
        OPENED,
        UPDATED,
        UNAPPROVED,
        REOPENED,
        MERGED
    }

    public abstract long id();

    public abstract long createdDate();

    public abstract User user();

    public abstract ActivitiesType action();

    @Nullable
    public abstract String commentAction();
    
    @Nullable
    public abstract Comments comment();
        
    @Nullable
    public abstract String fromHash();

    @Nullable
    public abstract String previousFromHash();

    @Nullable
    public abstract String previousToHash();

    @Nullable
    public abstract String toHash();

    @Nullable
    public abstract ActivitiesCommit added();

    @Nullable
    public abstract ActivitiesCommit removed();

    @Nullable
    public abstract List<User> addedReviewers();

    @Nullable
    public abstract List<User> removedReviewers();

    @SerializedNames({"id", "createdDate", "user", "action", "commentAction", 
        "comment", "fromHash", "previousFromHash", "previousToHash",
        "toHash", "added", "removed", "addedReviewers", "removedReviewers"})
    public static Activities create(final long id, 
            final long createdDate, 
            final User user, 
            final ActivitiesType action, 
            final String commentAction, 
            final Comments comment, 
            final @Nullable String fromHash,
            final @Nullable String previousFromHash,  
            final @Nullable String previousToHash,
            final @Nullable String toHash, 
            final @Nullable ActivitiesCommit added,
            final @Nullable ActivitiesCommit removed, 
            final @Nullable List<User> addedReviewers,
            final @Nullable List<User> removedReviewers) {
        
        return new AutoValue_Activities(id, 
                createdDate, 
                user, 
                action, 
                commentAction, 
                comment, 
                fromHash, 
                previousFromHash, 
                previousToHash,
                toHash, 
                added, 
                removed, 
                Utils.nullToEmpty(addedReviewers), 
                Utils.nullToEmpty(removedReviewers));
    }
}
