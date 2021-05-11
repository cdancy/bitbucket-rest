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
import com.cdancy.bitbucket.rest.domain.postwebhooks.PostWebHook;
import com.cdancy.bitbucket.rest.domain.postwebhooks.PostWebHooks;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.Strings2;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class PostWebHooksParser implements Function<HttpResponse, PostWebHooks> {

    @Override
    public PostWebHooks apply(final HttpResponse input) {
        final int statusCode = input.getStatusCode();
        if (statusCode >= 200 && statusCode < 400) {
            try {

                final List<PostWebHook> webHooks = Lists.newArrayList();
                final String payload = Strings2.toStringAndClose(input.getPayload().openStream());
                final JsonElement payloadAsElement = BitbucketUtils.nullToJsonElement(payload);
                final JsonArray jsonArray = payloadAsElement.getAsJsonArray();
                for (JsonElement elem : jsonArray) {
                    JsonObject jsonObject = elem.getAsJsonObject();
                    PostWebHook webHook = PostWebHook.create(jsonObject.getAsJsonPrimitive("branchCreated").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("branchDeleted").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("branchesToIgnore").getAsString(),
                        jsonObject.getAsJsonPrimitive("committersToIgnore").getAsString(),
                        jsonObject.getAsJsonPrimitive("enabled").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("prCommented").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("prCreated").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("prDeclined").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("prMerged").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("prReopened").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("prRescoped").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("prUpdated").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("repoMirrorSynced").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("repoPush").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("tagCreated").getAsBoolean(),
                        jsonObject.getAsJsonPrimitive("title").getAsString(),
                        jsonObject.getAsJsonPrimitive("url").getAsString(),
                        null);
                    webHooks.add(webHook);
                }

                return PostWebHooks.create(webHooks, null);

            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        } else {
            throw new RuntimeException(input.getStatusLine());
        }
    }
}
