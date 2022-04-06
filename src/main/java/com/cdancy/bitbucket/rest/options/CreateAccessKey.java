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

import com.cdancy.bitbucket.rest.domain.sshkey.AccessKey.PermissionType;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class CreateAccessKey {

    public abstract CreateKey key();

    public abstract PermissionType permission();

    CreateAccessKey() {
    }

    @SerializedNames({ "key", "permission" })
    public static CreateAccessKey create(final CreateKey key, final PermissionType permission) {
        return new AutoValue_CreateAccessKey(key, permission);
    }
}
