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

package com.cdancy.bitbucket.rest.domain.participants;

import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Participants {

    public enum Role {
        AUTHOR,
        REVIEWER,
        PARTICIPANT
    }

    public enum Status {
        APPROVED,
        UNAPPROVED,
        NEEDS_WORK
    }

    public abstract User user();

    @Nullable
    public abstract String lastReviewedCommit();

    public abstract Role role();

    public abstract boolean approved();

    public abstract Status status();

    @SerializedNames({"user", "lastReviewedCommit", "role", "approved", "status"})
    public static Participants create(User user, @Nullable String lastReviewedCommit, Role role,
                                      boolean approved, Status status) {
        return new AutoValue_Participants(user, lastReviewedCommit, role, approved, status);
    }
}
