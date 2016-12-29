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

import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks.PullRequestOnError;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import com.cdancy.bitbucket.rest.options.CreateComment;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

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

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
@Path("/rest/api/{jclouds.api-version}/projects")
public interface CommentsApi {

    @Named("comments:comment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments")
    @Payload("%7B \"text\": \"{comment}\" %7D")
    @Fallback(PullRequestOnError.class)
    @POST
    Comments comment(@PathParam("project") String project,
                     @PathParam("repo") String repo,
                     @PathParam("pullRequestId") int pullRequestId,
                     @PayloadParam("comment") String comment);

    @Named("comments:create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments")
    @Fallback(PullRequestOnError.class)
    @POST
    Comments create(@PathParam("project") String project,
                    @PathParam("repo") String repo,
                    @PathParam("pullRequestId") int pullRequestId,
                    @BinderParam(BindToJsonPayload.class) CreateComment createComment);

    @Named("comments:get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments/{commentId}")
    @Fallback(PullRequestOnError.class)
    @GET
    Comments get(@PathParam("project") String project,
                 @PathParam("repo") String repo,
                 @PathParam("pullRequestId") int pullRequestId,
                 @PathParam("commentId") int commentId);

    @Named("comments:delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments/{commentId}")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @DELETE
    boolean delete(@PathParam("project") String project,
                   @PathParam("repo") String repo,
                   @PathParam("pullRequestId") int pullRequestId,
                   @PathParam("commentId") int commentId,
                   @QueryParam("version") int version);
}
