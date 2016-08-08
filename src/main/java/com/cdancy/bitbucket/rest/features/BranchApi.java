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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import se.bjurr.jmib.anotations.GenerateMethodInvocationBuilder;

import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.BranchModel;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import com.cdancy.bitbucket.rest.options.CreateBranch;

@GenerateMethodInvocationBuilder
@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
@Path("/rest")
public interface BranchApi {

    @Named("branch:create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/branch-utils/{jclouds.api-version}/projects/{project}/repos/{repo}/branches")
    @Fallback(BitbucketFallbacks.BranchOnError.class)
    @POST
    Branch create(@PathParam("project") String project,
                  @PathParam("repo") String repo,
                  @BinderParam(BindToJsonPayload.class) CreateBranch createBranch);

    @Named("branch:delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/branch-utils/{jclouds.api-version}/projects/{project}/repos/{repo}/branches")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @Payload("%7B \"name\": \"{branchPath}\" %7D")
    @DELETE
    boolean delete(@PathParam("project") String project,
                   @PathParam("repo") String repo,
                   @PayloadParam("branchPath") String branchPath);

    @Named("branch:update-default")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/branches/default")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @Payload("%7B \"id\": \"{id}\" %7D")
    @PUT
    boolean updateDefault(@PathParam("project") String project,
                          @PathParam("repo") String repo,
                          @PayloadParam("id") String id);

    @Named("branch:get-default")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/branches/default")
    @Fallback(BitbucketFallbacks.BranchOnError.class)
    @GET
    Branch getDefault(@PathParam("project") String project,
                      @PathParam("repo") String repo);

    @Named("branch:model")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/branch-utils/{jclouds.api-version}/projects/{project}/repos/{repo}/branchmodel")
    @Fallback(BitbucketFallbacks.BranchOnError.class)
    @GET
    BranchModel model(@PathParam("project") String project,
                      @PathParam("repo") String repo);
}
