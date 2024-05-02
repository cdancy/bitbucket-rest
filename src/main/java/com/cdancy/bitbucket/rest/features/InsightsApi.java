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
import com.cdancy.bitbucket.rest.domain.insights.Annotation;
import com.cdancy.bitbucket.rest.domain.insights.AnnotationsResponse;
import com.cdancy.bitbucket.rest.domain.insights.InsightReport;
import com.cdancy.bitbucket.rest.domain.insights.InsightReportPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.options.CreateAnnotations;
import com.cdancy.bitbucket.rest.options.CreateInsightReport;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;

import jakarta.inject.Named;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/insights/{jclouds.api-version}")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface InsightsApi {

    @Named("insights:get-annotations")
    @Documentation( {"https://docs.atlassian.com/bitbucket-server/rest/6.4.0/bitbucket-code-insights-rest.html#idp2"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/commits/{commitId}/annotations")
    @Fallback(BitbucketFallbacks.AnnotationsResponseOnError.class)
    @GET
    AnnotationsResponse listAnnotations(@PathParam("project") String project,
                                        @PathParam("repo") String repo,
                                        @PathParam("commitId") String commitId,
                                        @Nullable @QueryParam("externalId") String externalId,
                                        @Nullable @QueryParam("path") String path,
                                        @Nullable @QueryParam("severity") String severity,
                                        @Nullable @QueryParam("type") String type);

    @Named("insights:get-reports")
    @Documentation( {"https://docs.atlassian.com/bitbucket-server/rest/6.4.0/bitbucket-code-insights-rest.html#idp4"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/commits/{commitId}/reports")
    @Fallback(BitbucketFallbacks.InsightReportPageOnError.class)
    @GET
    InsightReportPage listReports(@PathParam("project") String project,
                                  @PathParam("repo") String repo,
                                  @PathParam("commitId") String commitId,
                                  @Nullable @QueryParam("limit") int limit,
                                  @Nullable @QueryParam("start") int start);

    @Named("insights:get-report")
    @Documentation( {"https://docs.atlassian.com/bitbucket-server/rest/6.4.0/bitbucket-code-insights-rest.html#idp7"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/commits/{commitId}/reports/{key}")
    @Fallback(BitbucketFallbacks.InsightReportOnError.class)
    @GET
    InsightReport getReport(@PathParam("project") String project,
                            @PathParam("repo") String repo,
                            @PathParam("commitId") String commitId,
                            @PathParam("key") String key);

    @Named("insights:create-report")
    @Documentation( {"https://docs.atlassian.com/bitbucket-server/rest/6.4.0/bitbucket-code-insights-rest.html#idp9"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/commits/{commitId}/reports/{key}")
    @Fallback(BitbucketFallbacks.InsightReportOnError.class)
    @PUT
    InsightReport createReport(@PathParam("project") String project,
                               @PathParam("repo") String repo,
                               @PathParam("commitId") String commitId,
                               @PathParam("key") String key,
                               @BinderParam(BindToJsonPayload.class) CreateInsightReport createInsightReport);

    @Named("insights:delete-report")
    @Documentation( {"https://docs.atlassian.com/bitbucket-server/rest/6.4.0/bitbucket-code-insights-rest.html#idp8"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/commits/{commitId}/reports/{key}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deleteReport(@PathParam("project") String project,
                               @PathParam("repo") String repo,
                               @PathParam("commitId") String commitId,
                               @PathParam("key") String key);

    @Named("insights:delete-annotation")
    @Documentation( {"https://docs.atlassian.com/bitbucket-server/rest/6.4.0/bitbucket-code-insights-rest.html#idp11"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/commits/{commitId}/reports/{key}/annotations")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus deleteAnnotation(@PathParam("project") String project,
                                   @PathParam("repo") String repo,
                                   @PathParam("commitId") String commitId,
                                   @PathParam("key") String key,
                                   @Nullable @QueryParam("externalId") String externalId);

    @Named("insights:create-annotations")
    @Documentation( {"https://docs.atlassian.com/bitbucket-server/rest/6.4.0/bitbucket-code-insights-rest.html#idp12"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/commits/{commitId}/reports/{key}/annotations")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @POST
    RequestStatus createAnnotations(@PathParam("project") String project,
                                    @PathParam("repo") String repo,
                                    @PathParam("commitId") String commitId,
                                    @PathParam("key") String key,
                                    @BinderParam(BindToJsonPayload.class) CreateAnnotations createAnnotations);

    @Named("insights:get-annotations")
    @Documentation( {"https://docs.atlassian.com/bitbucket-server/rest/6.4.0/bitbucket-code-insights-rest.html#idp13"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/commits/{commitId}/reports/{key}/annotations")
    @Fallback(BitbucketFallbacks.AnnotationsResponseOnError.class)
    @GET
    AnnotationsResponse getAnnotationsByReport(@PathParam("project") String project,
                                               @PathParam("repo") String repo,
                                               @PathParam("commitId") String commitId,
                                               @PathParam("key") String key);

    @Named("insights:create-annotation")
    @Documentation( {"https://docs.atlassian.com/bitbucket-server/rest/6.4.0/bitbucket-code-insights-rest.html#idp13"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{project}/repos/{repo}/commits/{commitId}/reports/{key}/annotations/{externalId}")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @PUT
    RequestStatus createAnnotation(@PathParam("project") String project,
                                   @PathParam("repo") String repo,
                                   @PathParam("commitId") String commitId,
                                   @PathParam("key") String key,
                                   @PathParam("externalId") String externalId,
                                   @BinderParam(BindToJsonPayload.class) Annotation annotation);
}
