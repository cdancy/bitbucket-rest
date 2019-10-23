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

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.insights.Annotation;
import com.cdancy.bitbucket.rest.domain.insights.AnnotationsResponse;
import com.cdancy.bitbucket.rest.domain.insights.InsightReport;
import com.cdancy.bitbucket.rest.domain.insights.InsightReportData;
import com.cdancy.bitbucket.rest.domain.insights.InsightReportPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.options.CreateAnnotations;
import com.cdancy.bitbucket.rest.options.CreateInsightReport;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "InsightsApiLiveTest")
public class InsightsApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private String projectKey;
    private String repoKey;
    private User user;
    private String commitHash;
    private String reportKey = TestUtilities.randomStringLettersOnly();

    private CreateInsightReport createInsightReport;
    private CreateAnnotations createAnnotations;
    private Annotation annotation;
    private String annotationId;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        projectKey = generatedTestContents.project.key();
        repoKey = generatedTestContents.repository.name();
        user = TestUtilities.getDefaultUser(this.bitbucketAuthentication, this.api);
        final Branch branch = api.branchApi().getDefault(projectKey, repoKey);
        assertThat(branch).isNotNull();
        assertThat(branch.errors().isEmpty()).isTrue();
        commitHash = branch.latestCommit();

        createInsightReport = CreateInsightReport.create(TestUtilities.randomString(),
                                                         String.format("https://%s", TestUtilities.randomString()),
                                                         String.format("https://%s", TestUtilities.randomString()),
                                                         CreateInsightReport.RESULT.PASS,
                                                         TestUtilities.randomStringLettersOnly(),
                                                         TestUtilities.randomString(),
                                                         Collections.singletonList(
                                                             InsightReportData.create(TestUtilities.randomStringLettersOnly(),
                                                                                      InsightReportData.DataType.TEXT,
                                                                                      TestUtilities.randomString())
                                                         ));

        annotationId = TestUtilities.randomStringLettersOnly();
        createAnnotations = CreateAnnotations.create(Collections.singletonList(
            Annotation.create(reportKey,
                              annotationId,
                              0,
                              String.format("https://%s", TestUtilities.randomString()),
                              TestUtilities.randomStringLettersOnly(),
                              TestUtilities.randomStringLettersOnly(),
                              Annotation.AnnotationSeverity.LOW,
                              Annotation.AnnotationType.BUG
            )
        ));

        annotation = Annotation.create(reportKey,
                                       TestUtilities.randomStringLettersOnly(),
                                       0,
                                       String.format("https://%s", TestUtilities.randomString()),
                                       TestUtilities.randomStringLettersOnly(),
                                       TestUtilities.randomStringLettersOnly(),
                                       Annotation.AnnotationSeverity.LOW,
                                       Annotation.AnnotationType.BUG
        );
    }

    @Test
    public void testCreateReport() {
        final InsightReport report = api().createReport(projectKey, repoKey, commitHash, reportKey, createInsightReport);
        assertThat(report).isNotNull();
        assertThat(report.errors().isEmpty()).isTrue();
        assertThat(reportKey.equalsIgnoreCase(report.key())).isTrue();
    }

    @Test(dependsOnMethods = "testCreateReport")
    public void testGetReport() {
        final InsightReport report = api().getReport(projectKey, repoKey, commitHash, reportKey);
        assertThat(report).isNotNull();
        assertThat(report.errors().isEmpty()).isTrue();
        assertThat(reportKey.equalsIgnoreCase(report.key())).isTrue();
    }

    @Test(dependsOnMethods = "testCreateReport")
    public void testListReport() {
        final InsightReportPage page = api().listReports(projectKey, repoKey, commitHash, 100, 0);
        assertThat(page).isNotNull();
        assertThat(page.errors().isEmpty()).isTrue();
        assertThat(page.size()).isGreaterThan(0);
        final List<InsightReport> insightReports = page.values();
        assertThat(insightReports).isNotEmpty();
        assertThat(insightReports.stream().anyMatch(r -> r.key().equals(reportKey))).isTrue();
    }

    @Test
    public void testDeleteReportNonExistent() {
        final RequestStatus success = api().deleteReport(projectKey, repoKey, TestUtilities.randomStringLettersOnly(), TestUtilities.randomStringLettersOnly());
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @Test
    public void testGetReportNonExistent() {
        final InsightReport report = api().getReport(projectKey, repoKey, commitHash, TestUtilities.randomStringLettersOnly());
        assertThat(report).isNotNull();
        assertThat(report.errors().isEmpty()).isFalse();
    }

    @Test(dependsOnMethods = "testCreateReport")
    public void testCreateAnnotations() {
        final RequestStatus success = api().createAnnotations(projectKey, repoKey, commitHash, reportKey, createAnnotations);
        assertThat(success).isNotNull();
        assertThat(success.errors().isEmpty()).isTrue();
    }

    @Test(dependsOnMethods = "testCreateReport")
    public void testCreateAnnotation() {
        final RequestStatus success = api().createAnnotation(projectKey, repoKey, commitHash, reportKey, TestUtilities.randomStringLettersOnly(), annotation);
        assertThat(success).isNotNull();
        assertThat(success.errors().isEmpty()).isTrue();
    }

    @Test(dependsOnMethods = "testCreateAnnotations")
    public void testGetAnnotationsByReport() {
        final AnnotationsResponse annotations = api().getAnnotationsByReport(projectKey, repoKey, commitHash, reportKey);
        assertThat(annotations).isNotNull();
        assertThat(annotations.errors().isEmpty()).isTrue();
        assertThat(annotations.totalCount()).isGreaterThan(0);
        assertThat(annotations.annotations().stream().anyMatch(a -> a.externalId().equals(annotationId))).isTrue();
    }

    @Test(dependsOnMethods = "testCreateAnnotations")
    public void testListAnnotation() {
        final AnnotationsResponse annotations = api().listAnnotations(projectKey, repoKey, commitHash, annotationId, null, null, null);
        assertThat(annotations).isNotNull();
        assertThat(annotations.errors().isEmpty()).isTrue();
        assertThat(annotations.totalCount()).isGreaterThan(0);
        assertThat(annotations.annotations().stream().anyMatch(a -> a.externalId().equals(annotationId))).isTrue();
    }

    @Test
    public void testDeleteAnnotationNonExistent() {
        final RequestStatus success = api().deleteAnnotation(projectKey, TestUtilities.randomStringLettersOnly(), commitHash, TestUtilities.randomStringLettersOnly(), TestUtilities.randomStringLettersOnly());
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private InsightsApi api() {
        return api.insightsApi();
    }
}
