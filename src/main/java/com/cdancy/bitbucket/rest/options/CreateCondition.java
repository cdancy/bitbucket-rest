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

package com.cdancy.bitbucket.rest.options;

import com.cdancy.bitbucket.rest.domain.branch.Matcher;
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class CreateCondition {

    @Nullable
    public abstract Long id();

    public abstract Repository repository();

    public abstract Matcher sourceMatcher();

    public abstract Matcher targetMatcher();

    public abstract List<User> reviewers();

    public abstract Long requiredApprovals();

    @SerializedNames({ "id", "repository", "sourceMatcher", "targetMatcher", "reviewers", "requiredApprovals"})
    public static CreateCondition create(Long id, Repository repository, Matcher sourceMatcher,
                                   Matcher targetMatcher, List<User> reviewers, Long requiredApprovals) {
        return new AutoValue_CreateCondition(id, repository, sourceMatcher, targetMatcher, reviewers, requiredApprovals);
    }

    public static CreateCondition create(Condition condition) {
        return new AutoValue_CreateCondition(condition.id(), condition.repository(), condition.sourceRefMatcher(),
            condition.targetRefMatcher(), condition.reviewers(), condition.requiredApprovals());
    }
}
