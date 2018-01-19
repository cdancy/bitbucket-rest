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

package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.annotations.Documentation;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.CreateCondition;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.inject.Named;
import java.util.List;
import org.jclouds.rest.annotations.ResponseParser;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/default-reviewers/{jclouds.api-version}/projects")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface DefaultReviewersApi {

    @Named("default-reviewers:list-conditions")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-default-reviewers-rest.html#idm46188393150720"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/conditions")
    @GET
    List<Condition> listConditions(@PathParam("project") String project,
                                   @PathParam("repo") String repo);

    @Named("default-reviewers:create-condition")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-default-reviewers-rest.html#idm46188393242752"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/condition")
    @Fallback(BitbucketFallbacks.ConditionOnError.class)
    @POST
    Condition createCondition(@PathParam("project") String project,
                              @PathParam("repo") String repo,
                              @BinderParam(BindToJsonPayload.class) CreateCondition condition);

    @Named("default-reviewers:update-condition")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-default-reviewers-rest.html#idm46188393163680"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/condition/{id}")
    @Fallback(BitbucketFallbacks.ConditionOnError.class)
    @PUT
    Condition updateCondition(@PathParam("project") String project,
                              @PathParam("repo") String repo,
                              @PathParam("id") long id,
                              @BinderParam(BindToJsonPayload.class) CreateCondition condition);

    @Named("default-reviewers:delete-condition")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-default-reviewers-rest.html#idm46188393163680"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/condition/{id}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deleteCondition(@PathParam("project") String project,
                              @PathParam("repo") String repo,
                              @PathParam("id") long id);

}
