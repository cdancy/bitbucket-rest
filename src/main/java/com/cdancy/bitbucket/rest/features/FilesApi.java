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
import com.cdancy.bitbucket.rest.domain.file.FilesPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.RequestFilters;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.jclouds.rest.annotations.Fallback;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
public interface FilesApi {
    
    @Named("file:list-files")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.1.0/bitbucket-rest.html#idm45588159557712"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/rest/api/{jclouds.api-version}/projects/{project}/repos/{repo}/files")
    @Fallback(BitbucketFallbacks.FilesPageOnError.class)
    @GET
    FilesPage listLines(@PathParam("project") String project,
                           @PathParam("repo") String repo,
                           @Nullable @QueryParam("at") String commitHash,
                           @Nullable @QueryParam("start") Integer start,
                           @Nullable @QueryParam("limit") Integer limit);
}
