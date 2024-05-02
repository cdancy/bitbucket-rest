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
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import jakarta.inject.Named;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;


@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/api/{jclouds.api-version}/admin")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface AdminApi {

    @Named("admin:list-user-by-group")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/4.14.4/bitbucket-rest.html#idm46478323815824"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/groups/more-members")
    @Fallback(BitbucketFallbacks.UserPageOnError.class)
    @GET
    UserPage listUsersByGroup(@QueryParam("context") String context,
                              @Nullable @QueryParam("filter") String filter,
                              @Nullable @QueryParam("start") Integer start,
                              @Nullable @QueryParam("limit") Integer limit);

    @Named("admin:list-users")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45588158982432"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/users")
    @Fallback(BitbucketFallbacks.UserPageOnError.class)
    @GET
    UserPage listUsers(@Nullable @QueryParam("filter") String filter,
                       @Nullable @QueryParam("start") Integer start,
                       @Nullable @QueryParam("limit") Integer limit);

    @Named("admin:create-user")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm46358291368432"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/users")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @POST
    RequestStatus createUser(@QueryParam("name") String name,
                             @QueryParam("password") String password,
                             @QueryParam("displayName") String displayName,
                             @QueryParam("emailAddress") String emailAddress,
                             @Nullable @QueryParam("addToDefaultGroup") Boolean addToDefaultGroup,
                             @Nullable @QueryParam("notify") String notify);

    @Named("admin:delete-user")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm46358291356736"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/users")
    @Fallback(BitbucketFallbacks.UserOnError.class)
    @DELETE
    User deleteUser(@QueryParam("name") String name);
}
