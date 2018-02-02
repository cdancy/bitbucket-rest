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
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequestPage;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.domain.tags.Tag;
import com.cdancy.bitbucket.rest.domain.tags.TagPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.CreateTag;
import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.MediaType;
import org.jclouds.javax.annotation.Nullable;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/api/{jclouds.api-version}/projects")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface TagApi {

    @Named("tag:create")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278801952"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/tags")
    @Fallback(BitbucketFallbacks.TagOnError.class)
    @POST
    Tag create(@PathParam("project") String project,
               @PathParam("repo") String repo,
               @BinderParam(BindToJsonPayload.class) CreateTag createTag);

    @Named("tag:get")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888278800832"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/tags/{tag}")
    @Fallback(BitbucketFallbacks.TagOnError.class)
    @GET
    Tag get(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("tag") String tag);

    @Named("tag:list")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/5.7.0/bitbucket-rest.html#idm45568367769888"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/tags")
    @Fallback(BitbucketFallbacks.TagPageOnError.class)
    @GET
    TagPage list(@PathParam("project") String project,
                    @PathParam("repo") String repo,
                    @Nullable @QueryParam("filterText") String filterText,
                    @Nullable @QueryParam("orderBy") String orderBy,
                    @Nullable @QueryParam("start") Integer start,
                    @Nullable @QueryParam("limit") Integer limit);
}
