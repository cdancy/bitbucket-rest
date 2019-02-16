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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.cdancy.bitbucket.rest.annotations.Documentation;
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.CommentPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.CommentPageOnError;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.CommentsOnError;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.CreateComment;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.ResponseParser;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/api/{jclouds.api-version}/projects")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface CommentsApi {

    @Named("comments:comment")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278076336"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments")
    @Payload("%7B \"text\": \"{comment}\" %7D")
    @Fallback(CommentsOnError.class)
    @POST
    Comments comment(@PathParam("project") String project,
                     @PathParam("repo") String repo,
                     @PathParam("pullRequestId") int pullRequestId,
                     @PayloadParam("comment") String comment);

    @Named("comments:create")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278076336"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments")
    @Fallback(CommentsOnError.class)
    @POST
    Comments create(@PathParam("project") String project,
                    @PathParam("repo") String repo,
                    @PathParam("pullRequestId") int pullRequestId,
                    @BinderParam(BindToJsonPayload.class) CreateComment createComment);

    @Named("comments:get")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278070112"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments/{commentId}")
    @Fallback(CommentsOnError.class)
    @GET
    Comments get(@PathParam("project") String project,
                 @PathParam("repo") String repo,
                 @PathParam("pullRequestId") int pullRequestId,
                 @PathParam("commentId") int commentId);

    @Deprecated
    @Named("comments:file-comments-deprecated")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278617264"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments")
    @Fallback(CommentPageOnError.class)
    @GET
    CommentPage fileComments(@PathParam("project") String project,
                                @PathParam("repo") String repo,
                                @PathParam("pullRequestId") int pullRequestId,
                                @QueryParam("path") String pathToFile,
                                @Nullable @QueryParam("start") Integer start,
                                @Nullable @QueryParam("limit") Integer limit);
    
    @Named("comments:file-comments")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278617264"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments")
    @Fallback(CommentPageOnError.class)
    @GET
    CommentPage fileComments(@PathParam("project") String project,
                                @PathParam("repo") String repo,
                                @PathParam("pullRequestId") int pullRequestId,
                                @QueryParam("path") String pathToFile,
                                @Nullable @QueryParam("anchorState") String anchorState,
                                @Nullable @QueryParam("diffType") String diffType,
                                @Nullable @QueryParam("fromHash") String fromHash,
                                @Nullable @QueryParam("toHash") String toHash,
                                @Nullable @QueryParam("start") Integer start,
                                @Nullable @QueryParam("limit") Integer limit);

    @Named("comments:delete")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278021232"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments/{commentId}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus delete(@PathParam("project") String project,
                   @PathParam("repo") String repo,
                   @PathParam("pullRequestId") int pullRequestId,
                   @PathParam("commentId") int commentId,
                   @QueryParam("version") int version);
}
