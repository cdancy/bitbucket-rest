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
import com.cdancy.bitbucket.rest.domain.support.SupportZip;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import org.jclouds.rest.annotations.RequestFilters;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/troubleshooting/latest/support-zip")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface SupportApi {

    @Named("support-zip:create")
    @Documentation("https://confluence.atlassian.com/support/create-a-support-zip-using-the-rest-api-in-server-applications-947857090.html#CreateasupportzipusingtheRESTAPIinServerapplications-Generateasupportzip")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/local")
    @POST
    SupportZip createSupportZip();

    @Named("support-zip:status")
    @Documentation("https://confluence.atlassian.com/support/create-a-support-zip-using-the-rest-api-in-server-applications-947857090.html#CreateasupportzipusingtheRESTAPIinServerapplications-Checktheprogressofthetask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/status/task/{taskId}")
    @GET
    SupportZip getSupportZipStatus(@PathParam("taskId") String taskId);
}
