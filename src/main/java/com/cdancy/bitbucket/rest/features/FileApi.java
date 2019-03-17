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
import com.cdancy.bitbucket.rest.domain.commit.Commit;
import com.cdancy.bitbucket.rest.domain.file.FilesPage;
import com.cdancy.bitbucket.rest.domain.file.LastModified;
import com.cdancy.bitbucket.rest.domain.file.LinePage;
import com.cdancy.bitbucket.rest.domain.file.RawContent;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.filters.ScrubNullFromPathFilter;
import com.cdancy.bitbucket.rest.parsers.RawContentParser;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PartParam;
import org.jclouds.rest.annotations.RequestFilters;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.jclouds.rest.annotations.ResponseParser;

@RequestFilters(BitbucketAuthenticationFilter.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface FileApi {

    @Named("file:raw-content")
    @Documentation({"https://jira.atlassian.com/browse/BSERV-4036"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/projects/{project}/repos/{repo}/raw/{filePath}")
    @Fallback(BitbucketFallbacks.RawContentOnError.class)
    @ResponseParser(RawContentParser.class)
    @GET
    RawContent raw(@PathParam("project") String project,
                @PathParam("repo") String repo,
                @PathParam("filePath") String filePath,
                @Nullable @QueryParam("at") String commitHash);
    
    @Named("file:list-lines")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.1.0/bitbucket-rest.html#idm45588158357840"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/rest/api/{jclouds.api-version}/projects/{project}/repos/{repo}/browse/{filePath}")
    @Fallback(BitbucketFallbacks.LinePageOnError.class)
    @GET
    LinePage listLines(@PathParam("project") String project,
                           @PathParam("repo") String repo,
                           @PathParam("filePath") String filePath,
                           @Nullable @QueryParam("at") String commitHash,
                           @Nullable @QueryParam("type") Boolean type,
                           @Nullable @QueryParam("blame") Boolean blame,
                           @Nullable @QueryParam("noContent") Boolean noContent,
                           @Nullable @QueryParam("start") Integer start,
                           @Nullable @QueryParam("limit") Integer limit);

    @Named("file:update-content")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/6.0.0/bitbucket-rest.html#idp185"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/rest/api/{jclouds.api-version}/projects/{project}/repos/{repo}/browse/{filePath}")
    @Fallback(BitbucketFallbacks.CommitOnError.class)
    @PUT
    Commit updateContent(@PathParam("project") String project,
                         @PathParam("repo") String repo,
                         @PathParam("filePath") String filePath,
                         @FormParam("branch") String branch,
                         @PartParam(name = "content") String content,
                         @Nullable @FormParam("message") String message,
                         @Nullable @FormParam("sourceCommitId") String sourceCommitId,
                         @Nullable @FormParam("sourceBranch") String sourceBranch);

    @Named("file:list-files")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/5.1.0/bitbucket-rest.html#idm45588159557712"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/rest/api/{jclouds.api-version}/projects/{project}/repos/{repo}/files")
    @Fallback(BitbucketFallbacks.FilesPageOnError.class)
    @GET
    FilesPage listFiles(@PathParam("project") String project,
                           @PathParam("repo") String repo,
                           @Nullable @QueryParam("at") String commitIdOrRef,
                           @Nullable @QueryParam("start") Integer start,
                           @Nullable @QueryParam("limit") Integer limit);

    @Named("file:last-modified")
    @RequestFilters(ScrubNullFromPathFilter.class)
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/6.0.0/bitbucket-rest.html#idp233"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/rest/api/{jclouds.api-version}/projects/{project}/repos/{repo}/last-modified/{path}")
    @Fallback(BitbucketFallbacks.LastModifiedOnError.class)
    @GET
    LastModified lastModified(@PathParam("project") String project,
            @PathParam("repo") String repo,
            @Nullable @PathParam("path") String path,
            @QueryParam("at") String commitIdOrRef);
}
