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

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.common.Utils;
import com.cdancy.bitbucket.rest.error.Error;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Branch implements ErrorsHolder {

    @Nullable
    public abstract String id();

    @Nullable
    public abstract String displayId();

    @Nullable
    public abstract String type();

    @Nullable
    public abstract String latestCommit();

    @Nullable
    public abstract String latestChangeset();

    public abstract boolean isDefault();

    Branch() {
    }

    @SerializedNames({ "id", "displayId", "type", "latestCommit", "latestChangeset", "isDefault", "errors" })
    public static Branch create(String id, String displayId, String type,
                                String latestCommit, String latestChangeset, boolean isDefault, List<Error> errors) {
        return new AutoValue_Branch(Utils.nullToEmpty(errors), id, displayId, type, latestCommit, latestChangeset, isDefault);
    }
}
