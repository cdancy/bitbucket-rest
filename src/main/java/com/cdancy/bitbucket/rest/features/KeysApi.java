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
import jakarta.inject.Named;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.QueryParam;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Fallback;

import com.cdancy.bitbucket.rest.options.CreateAccessKey;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import com.cdancy.bitbucket.rest.domain.sshkey.AccessKey;
import com.cdancy.bitbucket.rest.domain.sshkey.AccessKeyPage;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/keys/{jclouds.api-version}")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface KeysApi {

    @Named("keys:list-by-repo")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/latest/bitbucket-ssh-rest.html#idp9"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/ssh")
    @Fallback(BitbucketFallbacks.AccessKeyPageOnError.class)
    @GET
    AccessKeyPage listByRepo(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @Nullable @QueryParam("start") Integer start,
            @Nullable @QueryParam("limit") Integer limit);

    @Named("keys:create-for-repo")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/latest/bitbucket-ssh-rest.html#idp10"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/ssh")
    @Fallback(BitbucketFallbacks.AccessKeyOnError.class)
    @POST
    AccessKey createForRepo(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @BinderParam(BindToJsonPayload.class) CreateAccessKey createAccessKey);

    @Named("keys:get-for-repo")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/latest/bitbucket-ssh-rest.html#idp12"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/ssh/{id}")
    @Fallback(BitbucketFallbacks.AccessKeyOnError.class)
    @GET
    AccessKey getForRepo(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("id") long id);

    @Named("keys:delete-from-repo")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/latest/bitbucket-ssh-rest.html#idp13"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/ssh/{id}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deleteFromRepo(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("id") long id);

    @Named("keys:list-by-project")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/latest/bitbucket-ssh-rest.html#idp17"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/ssh")
    @Fallback(BitbucketFallbacks.AccessKeyPageOnError.class)
    @GET
    AccessKeyPage listByProject(@PathParam("project") String project,
            @Nullable @QueryParam("start") Integer start,
            @Nullable @QueryParam("limit") Integer limit);

    @Named("keys:create-for-project")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/latest/bitbucket-ssh-rest.html#idp18"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/ssh")
    @Fallback(BitbucketFallbacks.AccessKeyOnError.class)
    @POST
    AccessKey createForProject(@PathParam("project") String project,
            @BinderParam(BindToJsonPayload.class) CreateAccessKey createAccessKey);

    @Named("keys:get-for-project")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/latest/bitbucket-ssh-rest.html#idp20"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/ssh/{id}")
    @Fallback(BitbucketFallbacks.AccessKeyOnError.class)
    @GET
    AccessKey getForProject(@PathParam("project") String project,
            @PathParam("id") long id);

    @Named("keys:delete-from-project")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/latest/bitbucket-ssh-rest.html#idp21"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/ssh/{id}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deleteFromProject(@PathParam("project") String project,
            @PathParam("id") long id);

}
