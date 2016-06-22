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

import com.cdancy.bitbucket.rest.domain.pullrequest.Links;
import com.cdancy.bitbucket.rest.domain.pullrequest.Person;
import com.cdancy.bitbucket.rest.domain.pullrequest.Reference;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class CreatePullRequest {

   public abstract String title();

   @Nullable
   public abstract String description();

   // set to "OPEN" for creating new PR's
   public abstract String state();

   // set to TRUE for creating new PR's
   public abstract boolean open();

   // set to FALSE for creating new PR's
   public abstract boolean closed();

   public abstract Reference fromRef();

   public abstract Reference toRef();

   // set to FALSE for creating new PR's
   public abstract boolean locked();

   // default to empty List if null
   @Nullable
   public abstract List<Person> reviewers();

   // default to eventually empty list Link if null
   @Nullable
   public abstract Links links();

   CreatePullRequest() {
   }

   @SerializedNames({ "title", "description", "state", "open", "closed", "fromRef", "toRef", "locked", "reviewers", "links" })
   public static CreatePullRequest create(String title, String description, Reference fromRef,
                                          Reference toRef, List<Person> reviewers, Links links) {
      return new AutoValue_CreatePullRequest(title, description, "OPEN", true, false,
              fromRef, toRef, false, reviewers != null ? ImmutableList.copyOf(reviewers) :
              ImmutableList.<Person> of(), links != null ? links : Links.create(null, null));
   }
}
