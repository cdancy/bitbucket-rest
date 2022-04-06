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

package com.cdancy.bitbucket.rest.domain.sshkey;

import java.util.List;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.BitbucketUtils;

@AutoValue
public abstract class AccessKey implements ErrorsHolder {

    public enum PermissionType {
        REPO_WRITE,
        REPO_READ,
        PROJECT_WRITE,
        PROJECT_READ
    }

    @Nullable
    public abstract Key key();

    @Nullable
    public abstract Repository repository();

    @Nullable
    public abstract Project project();

    @Nullable
    public abstract PermissionType permission();

    @SerializedNames({"key", "repository", "project", "permission", "errors"})
    public static AccessKey create(final Key key,
            final Repository repository,
            final Project project,
            final PermissionType permission,
            final List<Error> errors) {
        return new AutoValue_AccessKey(BitbucketUtils.nullToEmpty(errors), key, repository, project, permission);
    }
}
