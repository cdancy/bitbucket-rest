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
import com.cdancy.bitbucket.rest.domain.postwebhooks.PostWebHook;
import com.cdancy.bitbucket.rest.domain.postwebhooks.PostWebHooks;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.CreatePostWebHook;
import com.cdancy.bitbucket.rest.parsers.PostWebHooksParser;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.binders.BindToJsonPayload;

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
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface PostWebHookApi {
    @Named("postwebhook:list")
    @Documentation({"https://support.cloudbees.com/hc/en-us/articles/115000083932-Generate-webHooks-in-Bitbucket-Server-via-REST-API-for-Pipeline-Multibranch?page=45#getexistingwebhooks"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/webhook/{jclouds.api-version}/projects/{project}/repos/{repo}/webhook/configurations")
    @ResponseParser(PostWebHooksParser.class)
    @GET
    PostWebHooks list(@PathParam("project") String project,
                      @PathParam("repo") String repo);

    @Named("postwebhook:update-postwebhook")
    @Documentation({"https://support.cloudbees.com/hc/en-us/articles/115000083932-Generate-webHooks-in-Bitbucket-Server-via-REST-API-for-Pipeline-Multibranch?page=45#updateawebhookbyid"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/webhook/{jclouds.api-version}/projects/{project}/repos/{repo}/webhook/configurations/{postWebHookId}")
    @Fallback(BitbucketFallbacks.PostWebHookOnError.class)
    @PUT
    PostWebHook update(@PathParam("project") String project,
                   @PathParam("repo") String repo,
                   @PathParam("postWebHookId") String postWebHookId,
                   @BinderParam(BindToJsonPayload.class) CreatePostWebHook postWebHook);

    @Named("postwebhook:create-postwebhook")
    @Documentation({"https://support.cloudbees.com/hc/en-us/articles/115000083932-Generate-webHooks-in-Bitbucket-Server-via-REST-API-for-Pipeline-Multibranch?page=45#createawebhook"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/webhook/{jclouds.api-version}/projects/{project}/repos/{repo}/webhook/configurations")
    @Fallback(BitbucketFallbacks.PostWebHookOnError.class)
    @POST
    PostWebHook create(@PathParam("project") String project,
                       @PathParam("repo") String repo,
                       @BinderParam(BindToJsonPayload.class) CreatePostWebHook postWebHook);

    @Named("postwebhook:delete-postwebhook")
    @Documentation({"https://support.cloudbees.com/hc/en-us/articles/115000083932-Generate-webHooks-in-Bitbucket-Server-via-REST-API-for-Pipeline-Multibranch?page=45#createawebhook"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/webhook/{jclouds.api-version}/projects/{project}/repos/{repo}/webhook/configurations/{postWebHookId}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus delete(@PathParam("project") String project,
                         @PathParam("repo") String repo,
                         @PathParam("postWebHookId") String postWebHookId);
}
