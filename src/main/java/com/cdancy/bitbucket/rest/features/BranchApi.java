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
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.BranchModel;
import com.cdancy.bitbucket.rest.domain.branch.BranchModelConfiguration;
import com.cdancy.bitbucket.rest.domain.branch.BranchPage;
import com.cdancy.bitbucket.rest.domain.branch.BranchPermission;
import com.cdancy.bitbucket.rest.domain.branch.BranchPermissionPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import com.cdancy.bitbucket.rest.options.CreateBranch;
import com.cdancy.bitbucket.rest.options.CreateBranchModelConfiguration;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
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

import javax.inject.Named;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
@Path("/rest")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface BranchApi {

    @Named("branch:list")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45295356999632"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/branches")
    @Fallback(BitbucketFallbacks.BranchPageOnError.class)
    @GET
    BranchPage list(@PathParam("project") String project,
                    @PathParam("repo") String repo,
                    @Nullable @QueryParam("base") String base,
                    @Nullable @QueryParam("details") String details,
                    @Nullable @QueryParam("filterText") String filterText,
                    @Nullable @QueryParam("orderBy") String orderBy,
                    @Nullable @QueryParam("start") Integer start,
                    @Nullable @QueryParam("limit") Integer limit);

    @Named("branch:create")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45295357022352"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/branch-utils/{jclouds.api-version}/projects/{project}/repos/{repo}/branches")
    @Fallback(BitbucketFallbacks.BranchOnError.class)
    @POST
    Branch create(@PathParam("project") String project,
                  @PathParam("repo") String repo,
                  @BinderParam(BindToJsonPayload.class) CreateBranch createBranch);

    @Named("branch:delete")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/4.10.0/bitbucket-branch-rest.html#idp47888"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/branch-utils/{jclouds.api-version}/projects/{project}/repos/{repo}/branches")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @Payload("%7B \"name\": \"{branchPath}\" %7D")
    @DELETE
    boolean delete(@PathParam("project") String project,
                   @PathParam("repo") String repo,
                   @PayloadParam("branchPath") String branchPath);

    @Named("branch:update-default")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45295356975264"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/branches/default")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @Payload("%7B \"id\": \"{id}\" %7D")
    @PUT
    boolean updateDefault(@PathParam("project") String project,
                          @PathParam("repo") String repo,
                          @PayloadParam("id") String id);

    @Named("branch:get-default")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45295356984528"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/branches/default")
    @Fallback(BitbucketFallbacks.BranchOnError.class)
    @GET
    Branch getDefault(@PathParam("project") String project,
                      @PathParam("repo") String repo);

    @Named("branch:model")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/4.10.0/bitbucket-branch-rest.html#idp27168"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/branch-utils/{jclouds.api-version}/projects/{project}/repos/{repo}/branchmodel")
    @Fallback(BitbucketFallbacks.BranchModelOnError.class)
    @GET
    BranchModel model(@PathParam("project") String project,
                      @PathParam("repo") String repo);

    @Named("branch:get-model-configuration")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/branch-utils/{jclouds.api-version}/projects/{project}/repos/{repo}/branchmodel/configuration")
    @Fallback(BitbucketFallbacks.BranchModelConfigurationOnError.class)
    @GET
    BranchModelConfiguration getModelConfiguration(@PathParam("project") String project,
                                                   @PathParam("repo") String repo);

    @Named("branch:get-model-configuration")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/branch-utils/{jclouds.api-version}/projects/{project}/repos/{repo}/branchmodel/configuration")
    @Fallback(BitbucketFallbacks.BranchModelConfigurationOnError.class)
    @PUT
    BranchModelConfiguration updateModelConfiguration(@PathParam("project") String project,
                                                      @PathParam("repo") String repo,
                                                      @BinderParam(BindToJsonPayload.class) CreateBranchModelConfiguration config);

    @Named("branch:list-branch-permission")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/4.14.1/bitbucket-ref-restriction-rest.html#idm45354011023456"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/branch-permissions/2.0/projects/{project}/repos/{repo}/restrictions")
    @Fallback(BitbucketFallbacks.BranchPermissionPageOnError.class)
    @GET
    BranchPermissionPage listBranchPermission(@PathParam("project") String project,
                                             @PathParam("repo") String repo,
                                             @Nullable @QueryParam("start") Integer start,
                                             @Nullable @QueryParam("limit") Integer limit);

    @Named("branch:update-branch-permission")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/4.14.1/bitbucket-ref-restriction-rest.html#idm45354011023456"})
    @Path("/branch-permissions/2.0/projects/{project}/repos/{repo}/restrictions")
    @Produces("application/vnd.atl.bitbucket.bulk+json")
    @Consumes(MediaType.APPLICATION_JSON)
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @POST
    boolean updateBranchPermission(@PathParam("project") String project,
                                   @PathParam("repo") String repo,
                                   @BinderParam(BindToJsonPayload.class) List<BranchPermission> listBranchPermission);

    @Named("branch:delete-branch-permission")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/4.14.1/bitbucket-ref-restriction-rest.html#idm45354011023456"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/branch-permissions/2.0/projects/{project}/repos/{repo}/restrictions/{id}")
    @Fallback(BitbucketFallbacks.FalseOnError.class)
    @DELETE
    boolean deleteBranchPermission(@PathParam("project") String project,
                                   @PathParam("repo") String repo,
                                   @PathParam("id") long id);
}
