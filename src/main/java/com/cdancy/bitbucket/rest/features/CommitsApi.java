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
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.MediaType;
import org.jclouds.javax.annotation.Nullable;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
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

    @Named("commits:list-Changes")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm46478324982720"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/commits/{commitId}/changes")
    @Fallback(BitbucketFallbacks.ChangePageOnError.class)
    @GET
    ChangePage listChanges(@PathParam("project") String project,
                           @PathParam("repo") String repo,
                           @PathParam("commitId") String commitId,
                           @Nullable @QueryParam("start") Integer start,
                           @Nullable @QueryParam("limit") Integer limit);
}
