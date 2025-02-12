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

package com.cdancy.bitbucket.rest.parsers;

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.repository.HookSettings;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.gson.JsonElement;
import java.io.IOException;
import jakarta.inject.Singleton;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.Strings2;

@Singleton
public class HookSettingsParser implements Function<HttpResponse, HookSettings> {

    @Override
    public HookSettings apply(final HttpResponse input) {
        final int statusCode = input.getStatusCode();
        try {
            final String payload;
            switch (statusCode) {
                case 200: // means we have actual settings
                    payload = Strings2.toStringAndClose(input.getPayload().openStream());
                    break;
                case 204: // means we have no settings
                    payload = null;
                    break;
                default:
                    throw new RuntimeException(input.getStatusLine());
            }
            final JsonElement settings = BitbucketUtils.nullToJsonElement(payload);
            return HookSettings.of(settings);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
