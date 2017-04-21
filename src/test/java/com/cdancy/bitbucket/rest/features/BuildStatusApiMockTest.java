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
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
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

    public void testGetStatus() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/build-status.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BuildStatusApi api = baseApi.buildStatusApiApi();
        try {
            StatusPage statusPage = api.status("306bcf274566f2e89f75ae6f7faf10beff38382012", 0, 100);
            assertThat(statusPage).isNotNull();
            assertThat(statusPage.errors()).isEmpty();
            assertThat(statusPage.size() == 2).isTrue();
            assertThat(statusPage.values().get(0).state().equals(Status.StatusState.FAILED)).isTrue();

            Map<String, ?> queryParams = ImmutableMap.of("limit", 100, "start", 0);
            assertSent(server, "GET", "/rest/build-status/" + BitbucketApiMetadata.API_VERSION
                    + "/commits/306bcf274566f2e89f75ae6f7faf10beff38382012", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetStatusOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/build-status-error.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BuildStatusApi api = baseApi.buildStatusApiApi();
        try {
            StatusPage statusPage = api.status("306bcf274566f2e89f75ae6f7faf10beff38382012", 0, 100);
            assertThat(statusPage).isNotNull();
            assertThat(statusPage.values()).isEmpty();
            assertThat(statusPage.errors().size() == 1).isTrue();

            Map<String, ?> queryParams = ImmutableMap.of("limit", 100, "start", 0);
            assertSent(server, "GET", "/rest/build-status/" + BitbucketApiMetadata.API_VERSION
                    + "/commits/306bcf274566f2e89f75ae6f7faf10beff38382012", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetSummary() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/build-summary.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BuildStatusApi api = baseApi.buildStatusApiApi();
        try {
            Summary summary = api.summary("306bcf274566f2e89f75ae6f7faf10beff38382012");
            assertThat(summary).isNotNull();
            assertThat(summary.failed() == 1).isTrue();
            assertThat(summary.inProgress() == 2).isTrue();
            assertThat(summary.successful() == 3).isTrue();

            assertSent(server, "GET", "/rest/build-status/" + BitbucketApiMetadata.API_VERSION
                    + "/commits/stats/306bcf274566f2e89f75ae6f7faf10beff38382012");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
