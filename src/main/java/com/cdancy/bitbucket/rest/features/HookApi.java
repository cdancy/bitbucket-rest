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
import com.cdancy.bitbucket.rest.binders.BindHookSettingsToPayload;
import com.cdancy.bitbucket.rest.domain.repository.Hook;
import com.cdancy.bitbucket.rest.domain.repository.HookPage;
import com.cdancy.bitbucket.rest.domain.repository.HookSettings;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.parsers.HookSettingsParser;
import com.google.inject.name.Named;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.jclouds.rest.annotations.ResponseParser;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/api/{jclouds.api-version}/projects")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface HookApi {

    @Named("hook:list-hooks")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.1/bitbucket-rest.html#idm45993794419936"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/settings/hooks")
    @Fallback(BitbucketFallbacks.HookPageOnError.class)
    @GET
    HookPage list(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @Nullable @QueryParam("start") Integer start,
            @Nullable @QueryParam("limit") Integer limit);

    @Named("hook:get-hook")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.1/bitbucket-rest.html#idm45993794409760"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/settings/hooks/{hookKey}")
    @Fallback(BitbucketFallbacks.HookOnError.class)
    @GET
    Hook get(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("hookKey") String hookKey);

    @Named("hook:update-hook-settings")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/5.0.1/bitbucket-rest.html#idm45993794444512"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/settings/hooks/{hookKey}/settings")
    @Fallback(BitbucketFallbacks.HookSettingsOnError.class)
    @ResponseParser(HookSettingsParser.class)
    @PUT
    HookSettings update(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("hookKey") String hookKey,
            @BinderParam(BindHookSettingsToPayload.class) HookSettings hookSettings);
    
    @Named("hook:get-hook-settings")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/5.0.1/bitbucket-rest.html#idm45993794444512"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/settings/hooks/{hookKey}/settings")
    @Fallback(BitbucketFallbacks.HookSettingsOnError.class)
    @ResponseParser(HookSettingsParser.class)
    @GET
    HookSettings settings(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("hookKey") String hookKey);

    @Named("hook:enable-hook")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.1/bitbucket-rest.html#idm45993794409760"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/settings/hooks/{hookKey}/enabled")
    @Fallback(BitbucketFallbacks.HookOnError.class)
    @PUT
    Hook enable(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("hookKey") String hookKey);

    @Named("hook:disable-hook")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.0.1/bitbucket-rest.html#idm45993794409760"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/settings/hooks/{hookKey}/enabled")
    @Fallback(BitbucketFallbacks.HookOnError.class)
    @DELETE
    Hook disable(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("hookKey") String hookKey);
}
