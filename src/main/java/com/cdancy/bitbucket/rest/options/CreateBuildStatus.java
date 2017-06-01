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

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class CreateBuildStatus {

    public enum STATE {
        SUCCESSFUL,
        FAILED,
        INPROGRESS
    }

    public abstract String state();

    public abstract String key();
    
    @Nullable
    public abstract String name();

    public abstract String url();
    
    @Nullable
    public abstract String description();

    CreateBuildStatus() {
    }

    @SerializedNames({ "state", "key", "name", "url", "description" })
    public static CreateBuildStatus create(STATE state, String key, String name, String url, String description) {
        return new AutoValue_CreateBuildStatus(state != null ? state.toString() : null, key, name, url, description);
    }
}
