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

package com.cdancy.bitbucket.rest.domain.defaultreviewers;

import com.cdancy.bitbucket.rest.domain.branch.Matcher;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class Condition implements ErrorsHolder {

    @Nullable
    public abstract Long id();

    @Nullable
    public abstract Repository repository();

    @Nullable
    public abstract Matcher sourceRefMatcher();

    @Nullable
    public abstract Matcher targetRefMatcher();

    @Nullable
    public abstract List<User> reviewers();

    @Nullable
    public abstract Long requiredApprovals();

    @SerializedNames({ "id", "repository", "sourceRefMatcher", "targetRefMatcher", "reviewers", "requiredApprovals", "errors"})
    public static Condition create(Long id, Repository repository, Matcher sourceRefMatcher,
                                   Matcher targetRefMatcher, List<User> reviewers, Long requiredApprovals,
                                   @Nullable List<Error> errors) {
        return new AutoValue_Condition(Utils.nullToEmpty(errors), id, repository, sourceRefMatcher, targetRefMatcher,
            reviewers, requiredApprovals);
    }
}
