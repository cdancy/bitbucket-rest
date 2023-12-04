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
import com.cdancy.bitbucket.rest.domain.comment.Task;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.CreateTask;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/api/1.0/tasks")
public interface TasksApi {

    @Named("tasks:create")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45319070694864"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Fallback(BitbucketFallbacks.TaskOnError.class)
    @POST
    @Deprecated
    Task create(@BinderParam(BindToJsonPayload.class) CreateTask createTask);

    @Named("tasks:update")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/5.16.0/bitbucket-rest.html#idm8288367088"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{taskId}")
    @Fallback(BitbucketFallbacks.TaskOnError.class)
    @Payload("%7B \"state\": \"{state}\" %7D")
    @PUT
    @Deprecated
    Task update(@PathParam("taskId") int taskId, @PayloadParam("state") String state);


    @Named("tasks:get")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45701777641664"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{taskId}")
    @Fallback(BitbucketFallbacks.TaskOnError.class)
    @GET
    @Deprecated
    Task get(@PathParam("taskId") int taskId);

    @Named("tasks:delete")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html#idm45701777664960"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{taskId}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    @Deprecated
    RequestStatus delete(@PathParam("taskId") int taskId);
}
