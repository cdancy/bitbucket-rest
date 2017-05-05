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
import javax.inject.Named;
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

import com.cdancy.bitbucket.rest.domain.repository.PermissionsPage;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import com.cdancy.bitbucket.rest.options.CreateRepository;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
@Path("/rest/api/{jclouds.api-version}/projects")
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
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @DELETE
    boolean delete(@PathParam("project") String project,
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

    @Named("repository:list-Permissions-Group")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/groups")
    @GET
    PermissionsPage listPermissionsGroup(@PathParam("project") String project,
                                    @PathParam("repo") String repo,
                                    @Nullable @QueryParam("start") Integer start,
                                    @Nullable @QueryParam("limit") Integer limit);

    @Named("repository:create-Permissions-Group")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/groups")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @PUT
    boolean createPermissionsGroup(@PathParam("project") String project,
                                         @PathParam("repo") String repo,
                                         @Nullable @QueryParam("permission") String permission,
                                         @Nullable @QueryParam("name") String name);

    @Named("repository:delete-Permissions-Group")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/groups")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @DELETE
    boolean deletePermissionsGroup(@PathParam("project") String project,
                                @PathParam("repo") String repo,
                                @Nullable @QueryParam("name") String name);


    @Named("repository:list-Permissions-User")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/users")
    @GET
    PermissionsPage listPermissionsUser(@PathParam("project") String project,
                                         @PathParam("repo") String repo,
                                         @Nullable @QueryParam("start") Integer start,
                                         @Nullable @QueryParam("limit") Integer limit);

    @Named("repository:create-Permissions-Users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/users")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @PUT
    boolean createPermissionsUser(@PathParam("project") String project,
                                   @PathParam("repo") String repo,
                                   @Nullable @QueryParam("permission") String permission,
                                   @Nullable @QueryParam("name") String name);

    @Named("repository:delete-Permissions-Users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/permissions/users")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @DELETE
    boolean deletePermissionsUser(@PathParam("project") String project,
                                   @PathParam("repo") String repo,
                                   @Nullable @QueryParam("name") String name);
}
