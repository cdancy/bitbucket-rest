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

import com.cdancy.bitbucket.rest.domain.pullrequest.ActivitiesPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.domain.pullrequest.MergeStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequestPage;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.cdancy.bitbucket.rest.annotations.Documentation;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.ChangePageOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.CommitPageOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.MergeStatusOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.PullRequestOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.PullRequestPageOnError;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import com.cdancy.bitbucket.rest.options.CreatePullRequest;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
@Path("/rest/api/{jclouds.api-version}/projects")
public interface PullRequestApi {

    @Named("pull-request:get")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278120560"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}")
    @Fallback(PullRequestOnError.class)
    @GET
    PullRequest get(@PathParam("project") String project,
                    @PathParam("repo") String repo,
                    @PathParam("pullRequestId") int pullRequestId);

    @Named("pull-request:list")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278244864"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests")
    @Fallback(PullRequestPageOnError.class)
    @GET
    PullRequestPage list(@PathParam("project") String project,
                    @PathParam("repo") String repo,
                    @Nullable @QueryParam("direction") String direction,
                    @Nullable @QueryParam("at") String at,
                    @Nullable @QueryParam("state") String state,
                    @Nullable @QueryParam("order") String order,
                    @Nullable @QueryParam("withAttributes") Boolean withAttributes,
                    @Nullable @QueryParam("withProperties") Boolean withProperties,
                    @Nullable @QueryParam("start") Integer start,
                    @Nullable @QueryParam("limit") Integer limit);

    @Named("pull-request:create")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278226704"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests")
    @Fallback(PullRequestOnError.class)
    @POST
    PullRequest create(@PathParam("project") String project,
                       @PathParam("repo") String repo,
                       @BinderParam(BindToJsonPayload.class) CreatePullRequest createPullRequest);

    @Named("pull-request:merge")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278164320"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/merge")
    @Fallback(PullRequestOnError.class)
    @POST
    PullRequest merge(@PathParam("project") String project,
                      @PathParam("repo") String repo,
                      @PathParam("pullRequestId") int pullRequestId,
                      @QueryParam("version") int version);

    @Named("pull-request:can-merge")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278176112"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/merge")
    @Fallback(MergeStatusOnError.class)
    @GET
    MergeStatus canMerge(@PathParam("project") String project,
                         @PathParam("repo") String repo,
                         @PathParam("pullRequestId") int pullRequestId);

    @Named("pull-request:decline")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278147920"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/decline")
    @Fallback(PullRequestOnError.class)
    @POST
    PullRequest decline(@PathParam("project") String project,
                        @PathParam("repo") String repo,
                        @PathParam("pullRequestId") int pullRequestId,
                        @QueryParam("version") int version);

    @Named("pull-request:reopen")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278134496"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/reopen")
    @Fallback(PullRequestOnError.class)
    @POST
    PullRequest reopen(@PathParam("project") String project,
                       @PathParam("repo") String repo,
                       @PathParam("pullRequestId") int pullRequestId,
                       @QueryParam("version") int version);

    @Named("pull-request:changes")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888279438576"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/changes")
    @Fallback(ChangePageOnError.class)
    @GET
    ChangePage changes(@PathParam("project") String project,
                                @PathParam("repo") String repo,
                                @PathParam("pullRequestId") int pullRequestId,
                                @Nullable @QueryParam("withComments") Boolean withComments,
                                @Nullable @QueryParam("limit") Integer limit,
                                @Nullable @QueryParam("start") Integer start);

    @Named("pull-request:commits")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278089280"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/commits")
    @Fallback(CommitPageOnError.class)
    @GET
    CommitPage commits(@PathParam("project") String project,
                                @PathParam("repo") String repo,
                                @PathParam("pullRequestId") int pullRequestId,
                                @Nullable @QueryParam("withCounts") Boolean withCounts,
                                @Nullable @QueryParam("limit") Integer limit,
                                @Nullable @QueryParam("start") Integer start);

    @Named("pull-request:activities")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278197104"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/activities")
    @Fallback(PullRequestOnError.class)
    @GET
    ActivitiesPage activities(@PathParam("project") String project,
                              @PathParam("repo") String repo,
                              @PathParam("pullRequestId") long pullRequestId,
                              @Nullable @QueryParam("limit") Integer limit,
                              @Nullable @QueryParam("start") Integer start);
}
