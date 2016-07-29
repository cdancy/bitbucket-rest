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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import se.bjurr.jmib.anotations.GenerateMethodInvocationBuilder;

import com.cdancy.bitbucket.rest.domain.pullrequest.MergeStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.MergeStatusOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.PullRequestOnError;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import com.cdancy.bitbucket.rest.options.CreatePullRequest;

@GenerateMethodInvocationBuilder
@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
@Path("/rest/api/{jclouds.api-version}/projects")
public interface PullRequestApi {

    @Named("pull-request:get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}")
    @Fallback(PullRequestOnError.class)
    @GET
    PullRequest get(@PathParam("project") String project,
                    @PathParam("repo") String repo,
                    @PathParam("pullRequestId") int pullRequestId);

    @Named("pull-request:create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests")
    @Fallback(PullRequestOnError.class)
    @POST
    PullRequest create(@PathParam("project") String project,
                       @PathParam("repo") String repo,
                       @BinderParam(BindToJsonPayload.class) CreatePullRequest createPullRequest);

    @Named("pull-request:merge")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/merge")
    @Fallback(PullRequestOnError.class)
    @POST
    PullRequest merge(@PathParam("project") String project,
                      @PathParam("repo") String repo,
                      @PathParam("pullRequestId") int pullRequestId,
                      @QueryParam("version") int version);

    @Named("pull-request:can-merge")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/merge")
    @Fallback(MergeStatusOnError.class)
    @GET
    MergeStatus canMerge(@PathParam("project") String project,
                         @PathParam("repo") String repo,
                         @PathParam("pullRequestId") int pullRequestId);

    @Named("pull-request:decline")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/decline")
    @Fallback(PullRequestOnError.class)
    @POST
    PullRequest decline(@PathParam("project") String project,
                        @PathParam("repo") String repo,
                        @PathParam("pullRequestId") int pullRequestId,
                        @QueryParam("version") int version);

    @Named("pull-request:reopen")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/reopen")
    @Fallback(PullRequestOnError.class)
    @POST
    PullRequest reopen(@PathParam("project") String project,
                       @PathParam("repo") String repo,
                       @PathParam("pullRequestId") int pullRequestId,
                       @QueryParam("version") int version);
}
