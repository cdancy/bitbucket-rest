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

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.system.Version;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link SystemApi} class.
 */
@Test(groups = "unit", testName = "SystemApiMockTest")
public class SystemApiMockTest extends BaseBitbucketMockTest {

    private final String versionRegex = "^\\d+\\.\\d+\\.\\d+$";

    public void testGetVersion() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/version.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final Version version = baseApi.systemApi().version();
            assertThat(version).isNotNull();
            assertThat(version.version().matches(versionRegex)).isTrue();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/application-properties");
        } finally {
            server.shutdown();
        }
    }
}
