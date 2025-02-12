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
import jakarta.inject.Named;
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

import com.cdancy.bitbucket.rest.domain.project.ProjectPermissionsPage;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.project.ProjectPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import org.jclouds.rest.annotations.ResponseParser;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface ProjectApi {

    @Named("/api/{jclouds.api-version}/projects:create")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277995712"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects")
    @Fallback(BitbucketFallbacks.ProjectOnError.class)
    @POST
    Project create(@BinderParam(BindToJsonPayload.class) CreateProject createProject);

    @Named("project:get")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277922400"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}")
    @Fallback(BitbucketFallbacks.ProjectOnError.class)
    @GET
    Project get(@PathParam("project") String project);

    @Named("project:delete")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277932528"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus delete(@PathParam("project") String project);

    @Named("/api/{jclouds.api-version}/projects/project:list")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45888277975392"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects")
    @Fallback(BitbucketFallbacks.ProjectPageOnError.class)
    @GET
    ProjectPage list(@Nullable @QueryParam("name") String name,
                     @Nullable @QueryParam("permission") String permission,
                     @Nullable @QueryParam("start") Integer start,
                     @Nullable @QueryParam("limit") Integer limit);

    @Named("/api/{jclouds.api-version}/project:create-permissions-by-user")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054938032"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/permissions/users")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @PUT
    RequestStatus createPermissionsByUser(@PathParam("project") String project,
                                          @QueryParam("permission") String permission,
                                          @QueryParam("name") String name);

    @Named("project:delete-permissions-by-user")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054938032"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/permissions/users")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deletePermissionsByUser(@PathParam("project") String project,
                                          @QueryParam("name") String name);

    @Named("project:list-permissions-by-user")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054938032"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/permissions/users")
    @Fallback(BitbucketFallbacks.ProjectPermissionsPageOnError.class)
    @GET
    ProjectPermissionsPage listPermissionsByUser(@PathParam("project") String project,
                                                 @Nullable @QueryParam("start") Integer start,
                                                 @Nullable @QueryParam("limit") Integer limit);

    @Named("project:create-permissions-by-group")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054969200"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/permissions/groups")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @PUT
    RequestStatus createPermissionsByGroup(@PathParam("project") String project,
                                           @QueryParam("permission") String permission,
                                           @QueryParam("name") String name);

    @Named("project:delete-permissions-by-group")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054969200"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/permissions/groups")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deletePermissionsByGroup(@PathParam("project") String project,
                                           @QueryParam("name") String name);

    @Named("project:list-permissions-by-group")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.0/bitbucket-rest.html#idm45659054969200"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/permissions/groups")
    @Fallback(BitbucketFallbacks.ProjectPermissionsPageOnError.class)
    @GET
    ProjectPermissionsPage listPermissionsByGroup(@PathParam("project") String project,
                                                  @Nullable @QueryParam("start") Integer start,
                                                  @Nullable @QueryParam("limit") Integer limit);
}
