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

import com.cdancy.bitbucket.rest.domain.activities.ActivitiesCommit;
import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Activities {

    public enum ActivitiesType {
        RESCOPED,
        APPROVED,
        REVIEWED,
        COMMENTED,
        OPENED
    }

    public abstract long id();

    public abstract long createdDate();

    public abstract User user();

    public abstract ActivitiesType action();

    public abstract String fromHash();

    public abstract String previousFromHash();

    public abstract String previousToHash();

    public abstract String toHash();

    public abstract ActivitiesCommit added();

    public abstract ActivitiesCommit removed();

    @SerializedNames({"id", "createdDate", "size", "user", "action", "fromHash", "previousFromHash", "previousToHash", "toHash", "added", "removed"})
    public static Activities create(long id, long createdDate, User user, ActivitiesType action, String fromHash,
                                        String previousFromHash, String previousToHash, String toHash,
                                        ActivitiesCommit added, ActivitiesCommit removed) {
        return new AutoValue_Activities(id, createdDate, user, action, fromHash, previousFromHash, previousToHash,
            toHash, added, removed);
    }
}