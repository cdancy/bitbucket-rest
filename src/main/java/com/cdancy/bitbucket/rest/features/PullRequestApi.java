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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cdancy.bitbucket.rest.domain.comment.BlockerComments;
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.participants.Participants;
import com.cdancy.bitbucket.rest.domain.participants.ParticipantsPage;
import com.cdancy.bitbucket.rest.domain.activities.ActivitiesPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.*;
import com.cdancy.bitbucket.rest.options.CreateParticipants;
import com.cdancy.bitbucket.rest.options.EditPullRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.cdancy.bitbucket.rest.annotations.Documentation;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.ActivitiesPageOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.ChangePageOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.CommitPageOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.MergeStatusOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.ParticipantsOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.ParticipantsPageOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.PullRequestOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.PullRequestPageOnError;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.CreatePullRequest;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import org.jclouds.rest.annotations.ResponseParser;

import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/api/{jclouds.api-version}/projects")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
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
                    @Nullable @QueryParam("at") String branchOrTag,
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

    @Named("pull-request:edit")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idp304"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}")
    @Fallback(PullRequestOnError.class)
    @PUT
    PullRequest edit(@PathParam("project") String project,
                     @PathParam("repo") String repo,
                     @PathParam("pullRequestId") int pullRequestId,
                     @BinderParam(BindToJsonPayload.class) EditPullRequest editPullRequest);

    @Named("pull-request:delete")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/5.13.0/bitbucket-rest.html?utm_source=%2Fstatic%2Frest%2Fbitbucket-server%2Flatest%2Fbitbucket-rest.html&utm_medium=301#idm46209337261168"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @Payload("%7B \"version\": \"{version}\" %7D")
    @DELETE
    RequestStatus delete(@PathParam("project") String project,
                         @PathParam("repo") String repo,
                         @PathParam("pullRequestId") long pullRequestId,
                         @PayloadParam("version") long version);

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

    @Named("pull-request:list-activities")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278197104"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/activities")
    @Fallback(ActivitiesPageOnError.class)
    @GET
    ActivitiesPage listActivities(@PathParam("project") String project,
                              @PathParam("repo") String repo,
                              @PathParam("pullRequestId") long pullRequestId,
                              @Nullable @QueryParam("limit") Integer limit,
                              @Nullable @QueryParam("start") Integer start);

    @Named("pull-request:list-comments")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278197104"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments")
    @Fallback(BitbucketFallbacks.CommentPageOnError.class)
    @GET
    CommentPage listComments(@PathParam("project") String project,
                                  @PathParam("repo") String repo,
                                  @PathParam("pullRequestId") long pullRequestId,
                                  @Nullable @QueryParam("limit") Integer limit,
                                  @Nullable @QueryParam("start") Integer start);

    @Named("pull-request:list-blocker-comments")
    @Documentation({"https://developer.atlassian.com/server/bitbucket/rest/v815/api-group-pull-requests/#api-api-latest-projects-projectkey-repos-repositoryslug-pull-requests-pullrequestid-blocker-comments-get"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/blocker-comments")
    @Fallback(BitbucketFallbacks.BlockerCommentsPageOnError.class)
    @GET
    BlockerCommentsPage listBlockerComments(@PathParam("project") String project,
                                            @PathParam("repo") String repo,
                                            @PathParam("pullRequestId") long pullRequestId,
                                            @Nullable @QueryParam("limit") Integer limit,
                                            @Nullable @QueryParam("start") Integer start,
//                                            @Nullable @QueryParam("count") Boolean count,
                                            @Nullable @QueryParam("state") List<Comments.TaskState> states);

    @Named("pull-request:list-participants")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45627978405632"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/participants")
    @Fallback(ParticipantsPageOnError.class)
    @GET
    ParticipantsPage listParticipants(@PathParam("project") String project,
                                @PathParam("repo") String repo,
                                @PathParam("pullRequestId") long pullRequestId,
                                @Nullable @QueryParam("limit") Integer limit,
                                @Nullable @QueryParam("start") Integer start);

    @Named("pull-request:assign-participants")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45627978396928"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/participants")
    @Fallback(ParticipantsOnError.class)
    @POST
    Participants assignParticipant(@PathParam("project") String project,
                               @PathParam("repo") String repo,
                               @PathParam("pullRequestId") long pullRequestId,
                               @BinderParam(BindToJsonPayload.class) CreateParticipants participants);

    @Named("pull-request:delete-participants")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45627978369040"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/participants/{userSlug}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deleteParticipant(@PathParam("project") String project,
                               @PathParam("repo") String repo,
                               @PathParam("pullRequestId") long pullRequestId,
                               @PathParam("userSlug") String userSlug);

    @Named("pull-request:add-participant")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm46358292595040"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/participants/{userSlug}")
    @Fallback(ParticipantsOnError.class)
    @PUT
    Participants addParticipant(@PathParam("project") String project,
                                 @PathParam("repo") String repo,
                                 @PathParam("pullRequestId") long pullRequestId,
                                 @PathParam("userSlug") String userSlug,
                                 @BinderParam(BindToJsonPayload.class) CreateParticipants participants);
}
