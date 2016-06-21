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
package com.cdancy.bitbucket.rest.fallbacks;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

import java.util.Iterator;
import java.util.List;

import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.pullrequest.MergeStatus;
import com.sun.scenario.effect.Merge;
import org.jclouds.Fallback;

import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.error.Error;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class BitbucketFallbacks {

   private static final JsonParser parser = new JsonParser();

    public static final class FalseOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable t) throws Exception {
            if (checkNotNull(t, "throwable") != null) {
                return Boolean.FALSE;
            }
            throw propagate(t);
        }
    }

    public static final class ProjectOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable t) throws Exception {
            if (checkNotNull(t, "throwable") != null) {
                return createProjectFromErrors(getErrors(t.getMessage()));
            }
            throw propagate(t);
        }
    }

   public static final class PullRequestOnError implements Fallback<Object> {
      public Object createOrPropagate(Throwable t) throws Exception {
         if (checkNotNull(t, "throwable") != null) {
            return createPullRequestFromErrors(getErrors(t.getMessage()));
         }
         throw propagate(t);
      }
   }

   public static final class MergeStatusOnError implements Fallback<Object> {
      public Object createOrPropagate(Throwable t) throws Exception {
         if (checkNotNull(t, "throwable") != null) {
            return createMergeStatusFromErrors(getErrors(t.getMessage()));
         }
         throw propagate(t);
      }
   }

    public static List<Error> getErrors(String output) {
        JsonElement element = parser.parse(output);
        JsonObject object = element.getAsJsonObject();
        JsonArray errorsArray = object.get("errors").getAsJsonArray();

        List<Error> errors = Lists.newArrayList();
        Iterator<JsonElement> it = errorsArray.iterator();
        while (it.hasNext()) {
            JsonObject obj = it.next().getAsJsonObject();
            JsonElement context = obj.get("context");
            JsonElement message = obj.get("message");
            JsonElement exceptionName = obj.get("exceptionName");
            Error error = Error.create(!context.isJsonNull() ? context.getAsString() : null,
                    !message.isJsonNull() ? message.getAsString() : null,
                    !exceptionName.isJsonNull() ? exceptionName.getAsString() : null);
            errors.add(error);
        }

        return errors;
    }

    public static Project createProjectFromErrors(List<Error> errors) {
        return Project.create(null, -1, null, null, false, null, null, errors);
    }

    public static PullRequest createPullRequestFromErrors(List<Error> errors) {
      return PullRequest.create(-1, -1, null, null, null,
              false, false, 0, 0, null,
              null, false, null, null, null,
              null, errors);
    }

    public static MergeStatus createMergeStatusFromErrors(List<Error> errors) {
        return MergeStatus.create(false, false, null, errors);
    }
}
