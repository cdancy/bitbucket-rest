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
import com.cdancy.bitbucket.rest.domain.build.StatusPage;
import com.cdancy.bitbucket.rest.domain.build.Summary;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.CreateBuildStatus;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import jakarta.inject.Named;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/build-status/{jclouds.api-version}")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface BuildStatusApi {

    @Named("build-status:status")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/4.14.4/bitbucket-build-rest.html#idm44911111531152"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/commits/{commitId}")
    @Fallback(BitbucketFallbacks.StatusPageOnError.class)
    @GET
    StatusPage status(@PathParam("commitId") String commitId,
                      @Nullable @QueryParam("start") Integer start,
                      @Nullable @QueryParam("limit") Integer limit);
    
    @Named("build-status:add")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/4.14.4/bitbucket-build-rest.html#idm44911111500128"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/commits/{commitId}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @POST
    RequestStatus add(@PathParam("commitId") String commitId, 
                      @BinderParam(BindToJsonPayload.class) CreateBuildStatus createBuildStatus);

    @Named("build-status:summary")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/4.14.4/bitbucket-build-rest.html#idm44911111484336"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/commits/stats/{commitId}")
    @GET
    Summary summary(@PathParam("commitId") String commitId);
}
