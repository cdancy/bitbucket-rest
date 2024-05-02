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
import com.cdancy.bitbucket.rest.domain.commit.Commit;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import jakarta.inject.Named;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import jakarta.ws.rs.core.MediaType;
import org.jclouds.javax.annotation.Nullable;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/api/{jclouds.api-version}/projects")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface CommitsApi {

    @Named("commits:get")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888279116176"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/commits/{commitId}")
    @Fallback(BitbucketFallbacks.CommitOnError.class)
    @GET
    Commit get(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("commitId") String commitId,
            @Nullable @QueryParam("path") String path);

    @Named("commits:list-changes")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm46478324982720"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/commits/{commitId}/changes")
    @Fallback(BitbucketFallbacks.ChangePageOnError.class)
    @GET
    ChangePage listChanges(@PathParam("project") String project,
                           @PathParam("repo") String repo,
                           @PathParam("commitId") String commitId,
                           @Nullable @QueryParam("limit") Integer limit,
                           @Nullable @QueryParam("start") Integer start);

    @Named("commits:list")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm140236729804608"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/commits")
    @Fallback(BitbucketFallbacks.CommitPageOnError.class)
    @GET
    CommitPage list(@PathParam("project") String project,
                    @PathParam("repo") String repo,
                    @Nullable @QueryParam("withCounts") Boolean withCounts,
                    @Nullable @QueryParam("followRenames") Boolean followRenames,
                    @Nullable @QueryParam("ignoreMissing") Boolean ignoreMissing,
                    @Nullable @QueryParam("merges") String merges,
                    @Nullable @QueryParam("path") String path,
                    @Nullable @QueryParam("since") String since,
                    @Nullable @QueryParam("until") String until,
                    @Nullable @QueryParam("limit") Integer limit,
                    @Nullable @QueryParam("start") Integer start);
}
