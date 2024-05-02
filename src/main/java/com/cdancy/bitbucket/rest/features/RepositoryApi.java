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
import com.cdancy.bitbucket.rest.domain.labels.LabelsPage;
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
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.jclouds.rest.annotations.ResponseParser;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface RepositoryApi {

    @Named("repository:create")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277587248"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos")
    @Fallback(BitbucketFallbacks.RepositoryOnError.class)
    @POST
    Repository create(@PathParam("project") String project,
                      @BinderParam(BindToJsonPayload.class) CreateRepository createRepository);

    @Named("repository:get")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277593152"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}")
    @Fallback(BitbucketFallbacks.RepositoryOnError.class)
    @GET
    Repository get(@PathParam("project") String project,
                   @PathParam("repo") String repo);

    @Named("repository:fork")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277587248"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}")
    @Payload("%7B \"name\": \"{newRepo}\", \"project\": %7B \"key\": \"{newProject}\" %7D %7D")
    @Fallback(BitbucketFallbacks.RepositoryOnError.class)
    @POST
    Repository fork(@PathParam("project") String project,
                    @PathParam("repo") String repo,
                    @PayloadParam("newProject") String newProject,
                    @PayloadParam("newRepo") String newRepo);


    @Named("repository:mode")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277587248"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}")
    @Payload("%7B \"name\": \"{repo}\", \"forkable\": false, \"project\": %7B \"key\": \"{newProject}\" %7D,\"public\": false %7D")
    @Fallback(BitbucketFallbacks.RepositoryOnError.class)
    @POST
    Repository move(@PathParam("project") String project,
                    @PathParam("repo") @PayloadParam("repo") String repo,
                    @PayloadParam("newProject") String newProject
                    // @PayloadParam("newRepo") String newRepo
                    );




    @Named("repository:delete")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277567792"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(DeleteRepositoryParser.class)
    @DELETE
    RequestStatus delete(@PathParam("project") String project,
                         @PathParam("repo") String repo);

    @Named("repository:list")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277593152"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos")
    @Fallback(BitbucketFallbacks.RepositoryPageOnError.class)
    @GET
    RepositoryPage list(@PathParam("project") String project,
                        @Nullable @QueryParam("start") Integer start,
                        @Nullable @QueryParam("limit") Integer limit);

    @Named("repository:list-all")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/5.0.0/bitbucket-rest.html#idm45659055274784"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/repos")
    @Fallback(BitbucketFallbacks.RepositoryPageOnError.class)
    @GET
    RepositoryPage listAll(@Nullable @QueryParam("projectname") String project,
                           @Nullable @QueryParam("name") String repo,
                           @Nullable @QueryParam("permission") String permission,
                           @Nullable @QueryParam("visibility") String visibility,
                           @Nullable @QueryParam("start") Integer start,
                           @Nullable @QueryParam("limit") Integer limit);

    @Named("repository:get-pullrequest-settings")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054915136"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/settings/pull-requests")
    @Fallback(BitbucketFallbacks.PullRequestSettingsOnError.class)
    @GET
    PullRequestSettings getPullRequestSettings(@PathParam("project") String project,
                                               @PathParam("repo") String repo);

    @Named("repository:update-pullrequest-settings")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054915136"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/settings/pull-requests")
    @Fallback(BitbucketFallbacks.PullRequestSettingsOnError.class)
    @POST
    PullRequestSettings updatePullRequestSettings(@PathParam("project") String project,
                                                  @PathParam("repo") String repo,
                                                  @BinderParam(BindToJsonPayload.class) CreatePullRequestSettings createPullRequestSettings);

    @Named("repository:create-permissions-by-user")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054938032"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/permissions/users")
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
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/permissions/users")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deletePermissionsByUser(@PathParam("project") String project,
                                    @PathParam("repo") String repo,
                                    @QueryParam("name") String name);

    @Named("repository:list-permissions-by-user")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054938032"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/permissions/users")
    @Fallback(BitbucketFallbacks.PermissionsPageOnError.class)
    @GET
    PermissionsPage listPermissionsByUser(@PathParam("project") String project,
                                          @PathParam("repo") String repo,
                                          @Nullable @QueryParam("start") Integer start,
                                          @Nullable @QueryParam("limit") Integer limit);

    @Named("repository:create-permissions-by-group")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054969200"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/permissions/groups")
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
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/permissions/groups")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deletePermissionsByGroup(@PathParam("project") String project,
                                     @PathParam("repo") String repo,
                                     @QueryParam("name") String name);

    @Named("repository:list-permissions-by-group")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054969200"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/permissions/groups")
    @Fallback(BitbucketFallbacks.PermissionsPageOnError.class)
    @GET
    PermissionsPage listPermissionsByGroup(@PathParam("project") String project,
                                         @PathParam("repo") String repo,
                                         @Nullable @QueryParam("start") Integer start,
                                         @Nullable @QueryParam("limit") Integer limit);

    @Named("repository:getLabels")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idp273"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/labels")
    @Fallback(BitbucketFallbacks.RepositoryOnError.class)
    @GET
    LabelsPage getLabels(@PathParam("project") String project,
                         @PathParam("repo") String repo);
}
