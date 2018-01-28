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

package com.cdancy.bitbucket.rest.domain.repository;

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import java.util.List;

import java.util.Map;

import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class HookSettings implements ErrorsHolder {

    public abstract JsonElement settings();

    HookSettings() {
    }

    /**
     * Create a HookSettings instance from the passed JsonElement. Method
     * is safe for client use.
     * 
     * @param settings JsonElement representing HookSettings.
     * @return HookSettings
     */
    public static HookSettings from(final JsonElement settings) {
        return new AutoValue_HookSettings(ImmutableList.of(),
                BitbucketUtils.nullToJsonElement(settings));
    }

    /**
     * Create a HookSettings instance from the passed Map. Method
     * is safe for client use.
     * 
     * @param settings Map representing HookSettings.
     * @return HookSettings
     */
    public static HookSettings from(final Map settings) {
        return new AutoValue_HookSettings(ImmutableList.of(),
                BitbucketUtils.nullToJsonElement(settings));
    }

    /**
     * Method used internally to create a HookSettings object and NOT
     * meant for client use.
     * 
     * @param settings possible JsonElement representing HookSettings.
     * @param errors possible list of Error's.
     * @return HookSettings
     */
    @SerializedNames({ "settings", "errors" })
    public static HookSettings create(final JsonElement settings, final List<Error> errors) {
        return new AutoValue_HookSettings(BitbucketUtils.nullToEmpty(errors),
                BitbucketUtils.nullToJsonElement(settings));
    }
}
