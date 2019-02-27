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
import com.cdancy.bitbucket.rest.domain.sync.SyncState;
import com.cdancy.bitbucket.rest.domain.sync.SyncStatus;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.SyncOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/sync/{jclouds.api-version}/projects")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface SyncApi {

    @Named("sync:enable")
    @Documentation({"https://docs.atlassian.com/DAC/rest/stash/3.7.2/stash-repository-ref-sync-rest.html"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}")
    @Payload("%7B \"enabled\": \"{enabled}\" %7D")
    @Fallback(BitbucketFallbacks.SyncStatusOnError.class)
    @POST
    SyncStatus enable(@PathParam("project") String project,
                      @PathParam("repo") String repo,
                      @PayloadParam("enabled") boolean enabled);

    @Named("sync:status")
    @Documentation({"https://docs.atlassian.com/DAC/rest/stash/3.7.2/stash-repository-ref-sync-rest.html"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}")
    @Fallback(BitbucketFallbacks.SyncStatusOnError.class)
    @GET
    SyncStatus status(@PathParam("project") String project,
                      @PathParam("repo") String repo,
                      @Nullable @QueryParam("at") String at);

    @Named("sync:synchronize")
    @Documentation({"https://docs.atlassian.com/DAC/rest/stash/3.7.2/stash-repository-ref-sync-rest.html"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/synchronize")
    @Fallback(BitbucketFallbacks.SyncStateOnError.class)
    @POST
    SyncState synchronize(@PathParam("project") String project,
                          @PathParam("repo") String repo,
                          @BinderParam(BindToJsonPayload.class) SyncOptions syncOptions);
}
