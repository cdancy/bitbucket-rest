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

import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.sshkey.AccessKey;
import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class BranchRestriction {
    @Nullable
    public abstract Long id();

    public abstract BranchRestrictionEnumType type();

    public abstract Matcher matcher();

    public abstract List<User> users();

    public abstract List<String> groups();

    @Nullable
    public abstract List<Long> accessKeys();

    public static BranchRestriction createWithId(final Long id, 
            final BranchRestrictionEnumType type, 
            final Matcher matcher,
            final List<User> users, 
            final List<String> groups, 
            final List<Long> accessKeysId) {
        
        return new AutoValue_BranchRestriction(id, type, matcher, users, groups, accessKeysId);
    }

    @SerializedNames({"id", "type", "matcher", "users", "groups", "accessKeys"})
    public static BranchRestriction create(final Long id, 
            final BranchRestrictionEnumType type, 
            final Matcher matcher,
            final List<User> users, 
            final List<String> groups, 
            final @Nullable List<AccessKey> accessKeys) {
        
        final List<Long> accessKeyId = new ArrayList<>();
        if (accessKeys != null) {
            for (final AccessKey accessKey : accessKeys) {
                accessKeyId.add(accessKey.key().id());
            }
        }
        return BranchRestriction.createWithId(id, type, matcher, users, groups, accessKeyId);
    }
}
