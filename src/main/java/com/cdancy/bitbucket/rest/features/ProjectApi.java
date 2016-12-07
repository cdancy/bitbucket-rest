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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.project.ProjectPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import com.cdancy.bitbucket.rest.options.CreateProject;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
@Path("/rest/api/{jclouds.api-version}/projects")
public interface ProjectApi {

    @Named("project:create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Fallback(BitbucketFallbacks.ProjectOnError.class)
    @POST
    Project create(@BinderParam(BindToJsonPayload.class) CreateProject createProject);

    @Named("project:get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}")
    @Fallback(BitbucketFallbacks.ProjectOnError.class)
    @GET
    Project get(@PathParam("project") String project);

    @Named("project:delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @DELETE
    boolean delete(@PathParam("project") String project);

    @GET
    @Named("project:list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Fallback(BitbucketFallbacks.ProjectOnError.class)
    ProjectPage list(@Nullable @QueryParam("start") Integer start,
                     @Nullable @QueryParam("limit") Integer limit,
                     @Nullable @QueryParam("name") String name,
                     @Nullable @QueryParam("permission") String permission);
}
