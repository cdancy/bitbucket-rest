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
import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/api/{jclouds.api-version}/users")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface UserApi {

    @Named("user:get-user")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/6.0.0/bitbucket-rest.html#idp362"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("")
    @Fallback(BitbucketFallbacks.UserPageOnError.class)
    @GET
    UserPage users(@Nullable @QueryParam("filter") String filter,
                   @Nullable @QueryParam("group") String group,
                   @Nullable @QueryParam("permission") String globalRole,
                   @Nullable @QueryParam("permission.1") String projectRole,
                   @Nullable @QueryParam("permission.1.projectKey") String projProjectKey,
                   @Nullable @QueryParam("permission.1.projectId") String projProjectId,
                   @Nullable @QueryParam("permission.2") String repoRole,
                   @Nullable @QueryParam("permission.2.projectKey") String repoProjectKey,
                   @Nullable @QueryParam("permission.3.projectId") String repoPrrojectId,
                   @Nullable @QueryParam("start") Integer start,
                   @Nullable @QueryParam("limit") Integer limit);

    
}
