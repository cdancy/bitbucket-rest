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
import com.cdancy.bitbucket.rest.domain.common.Reference;
import com.cdancy.bitbucket.rest.domain.sync.SyncStatus;
import com.cdancy.bitbucket.rest.options.SyncOptions;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "SyncApiMockTest")
public class SyncApiMockTest extends BaseBitbucketMockTest {

    private final String restApiPath = "/rest/sync/";
    private final String projectKey = "PRJ";
    private final String repoKey = "my-repo";
    private final String syncPath = "/projects/" + projectKey + "/repos/" + repoKey;
    private final String refsHeadsMaster = "refs/heads/master";

    public void testEnabled() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/sync-enabled.json")).setResponseCode(200));

        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {

            final SyncStatus status = baseApi.syncApi().enable(projectKey, repoKey, true);
            assertThat(status.available()).isTrue();
            assertThat(status.enabled()).isTrue();
            assertThat(status.divergedRefs()).isNotEmpty();
            assertThat(status.divergedRefs().get(0).state()).isEqualTo("DIVERGED");
            assertThat(status.errors()).isEmpty();

            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION + syncPath);
        } finally {
            server.shutdown();
        }
    }

    public void testDisabled() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setResponseCode(204));

        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {

            final SyncStatus status = baseApi.syncApi().enable(projectKey, repoKey, true);
            assertThat(status.available()).isTrue();
            assertThat(status.enabled()).isFalse();
            assertThat(status.divergedRefs()).isEmpty();
            assertThat(status.errors()).isEmpty();

            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION + syncPath);
        } finally {
            server.shutdown();
        }
    }

    public void testEnabledOnError() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(400));

        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {

            final SyncStatus status = baseApi.syncApi().enable(projectKey, repoKey, true);
            assertThat(status.available()).isFalse();
            assertThat(status.enabled()).isFalse();
            assertThat(status.divergedRefs()).isEmpty();
            assertThat(status.errors()).isNotEmpty();

            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION + syncPath);
        } finally {
            server.shutdown();
        }
    }

    public void testStatus() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/sync-enabled.json")).setResponseCode(200));

        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {

            final SyncStatus status = baseApi.syncApi().status(projectKey, repoKey, null);
            assertThat(status.available()).isTrue();
            assertThat(status.enabled()).isTrue();
            assertThat(status.divergedRefs()).isNotEmpty();
            assertThat(status.divergedRefs().get(0).state()).isEqualTo("DIVERGED");
            assertThat(status.errors()).isEmpty();

            assertSent(server, "GET", restApiPath + BitbucketApiMetadata.API_VERSION + syncPath);
        } finally {
            server.shutdown();
        }
    }

    public void testStatusAt() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/sync-enabled.json")).setResponseCode(200));

        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {

            final SyncStatus status = baseApi.syncApi().status(projectKey, repoKey, "somereference");
            assertThat(status.available()).isTrue();
            assertThat(status.enabled()).isTrue();
            assertThat(status.divergedRefs()).isNotEmpty();
            assertThat(status.divergedRefs().get(0).state()).isEqualTo("DIVERGED");
            assertThat(status.errors()).isEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("at", "somereference");
            assertSent(server, "GET", restApiPath + BitbucketApiMetadata.API_VERSION + syncPath, queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testStatusOnError() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(400));

        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {

            final SyncStatus status = baseApi.syncApi().status(projectKey, repoKey, null);
            assertThat(status.available()).isFalse();
            assertThat(status.enabled()).isFalse();
            assertThat(status.divergedRefs()).isEmpty();
            assertThat(status.errors()).isNotEmpty();

            assertSent(server, "GET", restApiPath + BitbucketApiMetadata.API_VERSION + syncPath);
        } finally {
            server.shutdown();
        }
    }

    public void testSynchronize() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/synchronize.json")).setResponseCode(200));

        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {

            final SyncOptions options = SyncOptions.create(refsHeadsMaster, SyncOptions.ACTION.MERGE, "hello world");
            final Reference ref = baseApi.syncApi().synchronize(projectKey, repoKey, options);
            assertThat(ref.id()).isEqualTo(refsHeadsMaster);
            assertThat(ref.state()).isEqualTo("AHEAD");
            assertThat(ref.errors()).isEmpty();

            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION + syncPath + "/synchronize");
        } finally {
            server.shutdown();
        }
    }

    public void testSynchronizeInSync() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/synchronize.json")).setResponseCode(204));

        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {

            final SyncOptions options = SyncOptions.create(refsHeadsMaster, SyncOptions.ACTION.MERGE, "merge message");
            final Reference ref = baseApi.syncApi().synchronize(projectKey, repoKey, options);
            assertThat(ref.id()).isEqualTo(refsHeadsMaster);
            assertThat(ref.state()).isEqualTo("SYNCED");
            assertThat(ref.errors()).isEmpty();

            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION + syncPath + "/synchronize");
        } finally {
            server.shutdown();
        }
    }

    public void testSynchronizeOnError() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(400));

        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {

            final SyncOptions options = SyncOptions.create(refsHeadsMaster, SyncOptions.ACTION.MERGE, "hello world");
            final Reference ref = baseApi.syncApi().synchronize(projectKey, repoKey, options);
            assertThat(ref.id()).isEqualTo(refsHeadsMaster);
            assertThat(ref.state()).isNull();
            assertThat(ref.errors()).isNotEmpty();

            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION + syncPath + "/synchronize");
        } finally {
            server.shutdown();
        }
    }
}
