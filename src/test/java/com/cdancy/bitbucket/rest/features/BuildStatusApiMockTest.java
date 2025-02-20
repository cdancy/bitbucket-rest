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

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.build.Status;
import com.cdancy.bitbucket.rest.domain.build.StatusPage;
import com.cdancy.bitbucket.rest.domain.build.Summary;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateBuildStatus;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mock tests for the {@link BuildStatusApi} class.
 */
@Test(groups = "unit", testName = "BuildStatusApiMockTest")
public class BuildStatusApiMockTest extends BaseBitbucketMockTest {

    private final String restBuildStatusPath = "/rest/build-status/";
    private final String commitHash = "306bcf274566f2e89f75ae6f7faf10beff38382012";
    private final String commitPath = "/commits/" + commitHash;
    
    public void testGetStatus() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/build-status.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final StatusPage statusPage = baseApi.buildStatusApi().status(commitHash, 0, 100);
            assertThat(statusPage).isNotNull();
            assertThat(statusPage.errors()).isEmpty();
            assertThat(statusPage.size() == 2).isTrue();
            assertThat(statusPage.values().get(0).state().equals(Status.StatusState.FAILED)).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of("limit", 100, "start", 0);
            assertSent(server, "GET", restBuildStatusPath + BitbucketApiMetadata.API_VERSION
                    + commitPath, queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testGetStatusOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/build-status-error.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final StatusPage statusPage = baseApi.buildStatusApi().status(commitHash, 0, 100);
            assertThat(statusPage).isNotNull();
            assertThat(statusPage.values()).isEmpty();
            assertThat(statusPage.errors().size() == 1).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of("limit", 100, "start", 0);
            assertSent(server, "GET", restBuildStatusPath + BitbucketApiMetadata.API_VERSION
                    + commitPath, queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testGetSummary() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/build-summary.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final Summary summary = baseApi.buildStatusApi().summary(commitHash);
            assertThat(summary).isNotNull();
            assertThat(summary.cancelled() == 1).isTrue();
            assertThat(summary.failed() == 2).isTrue();
            assertThat(summary.inProgress() == 3).isTrue();
            assertThat(summary.successful() == 4).isTrue();
            assertThat(summary.unknown() == 5).isTrue();

            assertSent(server, "GET", restBuildStatusPath + BitbucketApiMetadata.API_VERSION
                    + "/commits/stats/306bcf274566f2e89f75ae6f7faf10beff38382012");
        } finally {
            server.shutdown();
        }
    }
    
    public void testAddBuildStatus() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/build-status-post.json")).setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final CreateBuildStatus cbs = CreateBuildStatus.create(CreateBuildStatus.STATE.SUCCESSFUL, 
                                    "REPO-MASTER", 
                                    "REPO-MASTER-42", 
                                    "https://bamboo.example.com/browse/REPO-MASTER-42", 
                                    "Changes by John Doe");
            final RequestStatus success = baseApi.buildStatusApi().add(commitHash, cbs);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            
            assertSent(server, "POST", restBuildStatusPath + BitbucketApiMetadata.API_VERSION
                    + commitPath);
        } finally {
            server.shutdown();
        }
    }
    
    public void testAddBuildStatusOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final CreateBuildStatus cbs = CreateBuildStatus.create(CreateBuildStatus.STATE.SUCCESSFUL, 
                                    "REPO-MASTER", 
                                    "REPO-MASTER-42", 
                                    "https://bamboo.example.com/browse/REPO-MASTER-42", 
                                    "Changes by John Doe");
            final RequestStatus success = baseApi.buildStatusApi().add(commitHash, cbs);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            
            assertSent(server, "POST", restBuildStatusPath + BitbucketApiMetadata.API_VERSION
                    + commitPath);
        } finally {
            server.shutdown();
        }
    }
}
