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
import com.cdancy.bitbucket.rest.domain.sync.Enabled;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "SyncApiMockTest")
public class SyncApiMockTest extends BaseBitbucketMockTest {

    private final String restApiPath = "/rest/sync/";

    private final String projectKey = "PRJ";
    private final String repoKey = "my-repo";
    private final String syncPath = "/projects/" + projectKey + "/repos/" + repoKey;

    public void testEnabled() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/sync-enabled.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final SyncApi api = baseApi.syncApi();
        try {
            final Enabled status = api.enable(projectKey, repoKey, true);
            assertThat(status.available()).isTrue();
            assertThat(status.enabled()).isTrue();
            assertThat(status.divergedRefs()).isNotEmpty();
            assertThat(status.divergedRefs().get(0).state()).isEqualTo("DIVERGED");
            assertThat(status.errors()).isEmpty();

            assertSent(server, "POST", restApiPath + BitbucketApiMetadata.API_VERSION + syncPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDisabled() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final SyncApi api = baseApi.syncApi();
        try {
            final Enabled status = api.enable(projectKey, repoKey, true);
            assertThat(status.available()).isTrue();
            assertThat(status.enabled()).isFalse();
            assertThat(status.divergedRefs()).isEmpty();
            assertThat(status.errors()).isEmpty();

            assertSent(server, "POST", restApiPath + BitbucketApiMetadata.API_VERSION + syncPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testEnabledOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(400));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final SyncApi api = baseApi.syncApi();
        try {
            final Enabled status = api.enable(projectKey, repoKey, true);
            assertThat(status.available()).isFalse();
            assertThat(status.enabled()).isFalse();
            assertThat(status.divergedRefs()).isEmpty();
            assertThat(status.errors()).isNotEmpty();

            assertSent(server, "POST", restApiPath + BitbucketApiMetadata.API_VERSION + syncPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
