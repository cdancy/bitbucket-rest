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

import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.insights.Annotation;
import com.cdancy.bitbucket.rest.domain.insights.AnnotationsResponse;
import com.cdancy.bitbucket.rest.domain.insights.InsightReport;
import com.cdancy.bitbucket.rest.domain.insights.InsightReportData;
import com.cdancy.bitbucket.rest.domain.insights.InsightReportPage;
import com.cdancy.bitbucket.rest.options.CreateAnnotations;
import com.cdancy.bitbucket.rest.options.CreateInsightReport;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "InsightApiMockTest")
public class InsightsApiMockTest extends BaseBitbucketMockTest {
    private final String projectKey = "PRJ";
    private final String repoKey = "my-repo";
    private final String commitHash = "abcdef0123abcdef4567abcdef8987abcdef6543";

    private final String getMethod = "GET";
    private final String deleteMethod = "DELETE";
    private final String putMethod = "PUT";

    private final String restApiPath = "/rest/insights/";

    private final String projectsKeyword = "/projects/";
    private final String repoKeyword = "/repos/";
    private final String commitKeyword = "/commits/";
    private final String annotationsKeyword = "/annotations";
    private final String reportsKeyword = "/reports";


    private final String qwertyKeyword = "qwerty";
    private final String limitKeyword = "limit";
    private final String startKeyword = "start";
    private final String annotationsJsonFile = "/annotations.json";
    private final String mockPath = ".gitignore";

    public void testListReports() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/insight-report-page.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final InsightsApi api = baseApi.insightsApi();
        try {

            final InsightReportPage insightReportPage = api.listReports(projectKey, repoKey, commitHash, 100, 0);
            assertThat(insightReportPage).isNotNull();
            assertThat(insightReportPage.values()).isNotEmpty();
            assertThat(insightReportPage.errors()).isEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);

            assertSent(server,
                       getMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey + repoKeyword + repoKey + commitKeyword + commitHash + reportsKeyword, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListReportsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final InsightsApi api = baseApi.insightsApi();
        try {

            final InsightReportPage insightReportPage = api.listReports(projectKey, repoKey, commitHash, 100, 0);
            assertThat(insightReportPage).isNotNull();
            assertThat(insightReportPage.values()).isEmpty();
            assertThat(insightReportPage.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server,
                       getMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey + repoKeyword + repoKey + commitKeyword + commitHash + reportsKeyword, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetReport() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/insight-report.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final InsightsApi api = baseApi.insightsApi();
        try {

            final String reportKey = qwertyKeyword;
            final InsightReport report = api.getReport(projectKey, repoKey, commitHash, reportKey);
            assertThat(report).isNotNull();
            assertThat(report.key()).isEqualTo(reportKey);

            assertSent(server,
                       getMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey + repoKeyword + repoKey + commitKeyword + commitHash + reportsKeyword + "/" + reportKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetReportOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/insight-report-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final InsightsApi api = baseApi.insightsApi();
        try {

            final String reportKey = qwertyKeyword;
            final InsightReport report = api.getReport(projectKey, repoKey, commitHash, reportKey);
            assertThat(report).isNotNull();
            assertThat(report.errors()).isNotEmpty();

            assertSent(server,
                       getMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey + repoKeyword + repoKey + commitKeyword + commitHash + reportsKeyword + "/" + reportKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateReport() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/insight-report.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final InsightsApi api = baseApi.insightsApi();
        try {

            final String reportKey = qwertyKeyword;
            final InsightReportData reportData = InsightReportData.createPercentage("Code Coverage", (byte) 15);
            final CreateInsightReport createInsightReport = CreateInsightReport.create("details",
                                                                                       "http://example.com",
                                                                                       "http://example.com/logourl",
                                                                                       CreateInsightReport.RESULT.PASS,
                                                                                       "reportTitle",
                                                                                       "Bitbucket-rest",
                                                                                       Collections.singletonList(reportData));
            final InsightReport report = api.createReport(projectKey, repoKey, commitHash, reportKey, createInsightReport);
            assertThat(report).isNotNull();
            assertThat(report.key()).isEqualTo(reportKey);

            assertSent(server,
                       putMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey + repoKeyword + repoKey + commitKeyword + commitHash + reportsKeyword + "/" + reportKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteReport() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final String reportKey = qwertyKeyword;
            final RequestStatus success = baseApi.insightsApi().deleteReport(projectKey, repoKey, commitHash, reportKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();

            assertSent(server,
                       deleteMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey + repoKeyword + repoKey + commitKeyword + commitHash + reportsKeyword + "/" + reportKey);
        } finally {
            server.shutdown();
        }
    }

    public void testGetAnnotations() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(annotationsJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final InsightsApi api = baseApi.insightsApi();
        try {

            final String reportKey = qwertyKeyword;
            final AnnotationsResponse annotations = api.getAnnotationsByReport(projectKey, repoKey, commitHash, reportKey);
            assertThat(annotations).isNotNull();
            assertThat(annotations.totalCount()).isEqualTo(3);

            assertSent(server,
                       getMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey
                           + repoKeyword + repoKey
                           + commitKeyword + commitHash
                           + reportsKeyword + "/" + reportKey
                           + annotationsKeyword);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetAnnotationsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final InsightsApi api = baseApi.insightsApi();
        try {

            final String reportKey = qwertyKeyword;
            final AnnotationsResponse annotationsResponse = api.getAnnotationsByReport(projectKey, repoKey, commitHash, reportKey);
            assertThat(annotationsResponse).isNotNull();
            assertThat(annotationsResponse.errors()).isNotEmpty();

            assertSent(server,
                       getMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey
                           + repoKeyword + repoKey
                           + commitKeyword + commitHash
                           + reportsKeyword + "/" + reportKey
                           + annotationsKeyword);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListAnnotations() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(annotationsJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final InsightsApi api = baseApi.insightsApi();
        try {
            final AnnotationsResponse annotations = api.listAnnotations(projectKey, repoKey, commitHash, null, null, null, null);
            assertThat(annotations).isNotNull();
            assertThat(annotations.totalCount()).isEqualTo(3);

            assertSent(server,
                       getMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey
                           + repoKeyword + repoKey
                           + commitKeyword + commitHash
                           + annotationsKeyword);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListAnnotationsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final InsightsApi api = baseApi.insightsApi();
        try {

            final AnnotationsResponse annotations = api.listAnnotations(projectKey, repoKey, commitHash, null, null, null, null);
            assertThat(annotations).isNotNull();
            assertThat(annotations.errors()).isNotEmpty();

            assertSent(server,
                       getMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey
                           + repoKeyword + repoKey
                           + commitKeyword + commitHash
                           + annotationsKeyword);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateAnnotation() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(annotationsJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.url("/").url());
        final InsightsApi api = baseApi.insightsApi();
        try {
            final String reportKey = qwertyKeyword;
            final Annotation annotation = Annotation.create(reportKey,
                                                            null,
                                                            3,
                                                            "",
                                                            "",
                                                            mockPath,
                                                            Annotation.AnnotationSeverity.LOW,
                                                            Annotation.AnnotationType.BUG);
            final RequestStatus requestStatus = api.createAnnotation(projectKey, repoKey, commitHash, reportKey, qwertyKeyword, annotation);
            assertThat(requestStatus).isNotNull();
            assertThat(requestStatus.value()).isTrue();

            assertSent(server,
                       putMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey
                           + repoKeyword + repoKey
                           + commitKeyword + commitHash
                           + reportsKeyword + "/" + reportKey
                           + annotationsKeyword + "/" + qwertyKeyword);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateAnnotationOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/insight-report-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.url("/").url());
        final InsightsApi api = baseApi.insightsApi();
        try {
            final String reportKey = qwertyKeyword;
            final Annotation annotation = Annotation.create(reportKey,
                                                            null,
                                                            3,
                                                            "",
                                                            "",
                                                            mockPath,
                                                            Annotation.AnnotationSeverity.LOW,
                                                            Annotation.AnnotationType.BUG);
            final RequestStatus requestStatus = api.createAnnotation(projectKey, repoKey, commitHash, reportKey, qwertyKeyword, annotation);
            assertThat(requestStatus).isNotNull();
            assertThat(requestStatus.errors()).isNotEmpty();

            assertSent(server,
                       putMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey
                           + repoKeyword + repoKey
                           + commitKeyword + commitHash
                           + reportsKeyword + "/" + reportKey
                           + annotationsKeyword + "/" + qwertyKeyword);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateAnnotations() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(annotationsJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.url("/").url());
        final InsightsApi api = baseApi.insightsApi();
        try {
            final String reportKey = qwertyKeyword;
            final Annotation annotation = Annotation.create(reportKey,
                                                            null,
                                                            3,
                                                            "",
                                                            "",
                                                            mockPath,
                                                            Annotation.AnnotationSeverity.LOW,
                                                            Annotation.AnnotationType.BUG);
            final CreateAnnotations createAnnotations = CreateAnnotations.create(Collections.singletonList(annotation));
            final RequestStatus requestStatus = api.createAnnotations(projectKey, repoKey, commitHash, reportKey, createAnnotations);
            assertThat(requestStatus).isNotNull();
            assertThat(requestStatus.value()).isTrue();

            assertSent(server,
                       postMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey
                           + repoKeyword + repoKey
                           + commitKeyword + commitHash
                           + reportsKeyword + "/" + reportKey
                           + annotationsKeyword);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateAnnotationsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/insight-report-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.url("/").url());
        final InsightsApi api = baseApi.insightsApi();
        try {
            final String reportKey = qwertyKeyword;
            final Annotation annotation = Annotation.create(reportKey,
                                                            null,
                                                            3,
                                                            "",
                                                            "",
                                                            mockPath,
                                                            Annotation.AnnotationSeverity.LOW,
                                                            Annotation.AnnotationType.BUG);
            final CreateAnnotations createAnnotations = CreateAnnotations.create(Collections.singletonList(annotation));
            final RequestStatus requestStatus = api.createAnnotations(projectKey, repoKey, commitHash, reportKey, createAnnotations);
            assertThat(requestStatus).isNotNull();
            assertThat(requestStatus.errors()).isNotEmpty();

            assertSent(server,
                       postMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey
                           + repoKeyword + repoKey
                           + commitKeyword + commitHash
                           + reportsKeyword + "/" + reportKey
                           + annotationsKeyword);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteAnnotation() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.url("/").url())) {

            final String reportKey = qwertyKeyword;
            final RequestStatus success = baseApi.insightsApi().deleteAnnotation(projectKey, repoKey, commitHash, reportKey, null);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();

            assertSent(server,
                       deleteMethod,
                       restApiPath + BitbucketApiMetadata.API_VERSION
                           + projectsKeyword + projectKey
                           + repoKeyword + repoKey
                           + commitKeyword + commitHash
                           + reportsKeyword + "/" + reportKey
                           + annotationsKeyword);
        } finally {
            server.shutdown();
        }
    }
}
