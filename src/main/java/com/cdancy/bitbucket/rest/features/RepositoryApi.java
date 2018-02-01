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
import com.cdancy.bitbucket.rest.domain.repository.PermissionsPage;
import com.cdancy.bitbucket.rest.domain.repository.PullRequestSettings;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.CreatePullRequestSettings;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import com.cdancy.bitbucket.rest.parsers.DeleteRepositoryParser;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import com.google.inject.name.Named;
import org.jclouds.javax.annotation.Nullable;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.jclouds.rest.annotations.ResponseParser;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/api/{jclouds.api-version}/projects")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface RepositoryApi {

    @Named("repository:create")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277587248"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos")
    @Fallback(BitbucketFallbacks.RepositoryOnError.class)
    @POST
    Repository create(@PathParam("project") String project,
                      @BinderParam(BindToJsonPayload.class) CreateRepository createRepository);

    @Named("repository:get")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277593152"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}")
    @Fallback(BitbucketFallbacks.RepositoryOnError.class)
    @GET
    Repository get(@PathParam("project") String project,
                   @PathParam("repo") String repo);

    @Named("repository:delete")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277567792"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(DeleteRepositoryParser.class)
    @DELETE
    RequestStatus delete(@PathParam("project") String project,
                         @PathParam("repo") String repo);

    @Named("repository:list")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277593152"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos")
    @Fallback(BitbucketFallbacks.RepositoryPageOnError.class)
    @GET
    RepositoryPage list(@PathParam("project") String project,
                        @Nullable @QueryParam("start") Integer start,
                        @Nullable @QueryParam("limit") Integer limit);

    @Named("repository:get-pullrequest-settings")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054915136"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/settings/pull-requests")
    @Fallback(BitbucketFallbacks.PullRequestSettingsOnError.class)
    @GET
    PullRequestSettings getPullRequestSettings(@PathParam("project") String project,
                                               @PathParam("repo") String repo);

    @Named("repository:update-pullrequest-settings")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054915136"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/settings/pull-requests")
    @Fallback(BitbucketFallbacks.PullRequestSettingsOnError.class)
    @POST
    PullRequestSettings updatePullRequestSettings(@PathParam("project") String project,
                                                  @PathParam("repo") String repo,
                                                  @BinderParam(BindToJsonPayload.class) CreatePullRequestSettings createPullRequestSettings);

    @Named("repository:create-permissions-by-user")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054938032"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/users")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @PUT
    RequestStatus createPermissionsByUser(@PathParam("project") String project,
                                    @PathParam("repo") String repo,
                                    @QueryParam("permission") String permission,
                                    @QueryParam("name") String name);

    @Named("repository:delete-permissions-by-user")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054938032"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/users")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deletePermissionsByUser(@PathParam("project") String project,
                                    @PathParam("repo") String repo,
                                    @QueryParam("name") String name);

    @Named("repository:list-permissions-by-user")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054938032"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/users")
    @Fallback(BitbucketFallbacks.PermissionsPageOnError.class)
    @GET
    PermissionsPage listPermissionsByUser(@PathParam("project") String project,
                                          @PathParam("repo") String repo,
                                          @Nullable @QueryParam("start") Integer start,
                                          @Nullable @QueryParam("limit") Integer limit);

    @Named("repository:create-permissions-by-group")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054969200"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/groups")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @PUT
    RequestStatus createPermissionsByGroup(@PathParam("project") String project,
                                     @PathParam("repo") String repo,
                                     @QueryParam("permission") String permission,
                                     @QueryParam("name") String name);

    @Named("repository:delete-permissions-by-group")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054969200"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/groups")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deletePermissionsByGroup(@PathParam("project") String project,
                                     @PathParam("repo") String repo,
                                     @QueryParam("name") String name);

    @Named("repository:list-permissions-by-group")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054969200"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/groups")
    @Fallback(BitbucketFallbacks.PermissionsPageOnError.class)
    @GET
    PermissionsPage listPermissionsByGroup(@PathParam("project") String project,
                                         @PathParam("repo") String repo,
                                         @Nullable @QueryParam("start") Integer start,
                                         @Nullable @QueryParam("limit") Integer limit);
}
