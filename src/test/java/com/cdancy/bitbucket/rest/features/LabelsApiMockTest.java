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
import com.cdancy.bitbucket.rest.domain.labels.LabelsPage;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "LabelsApiMockTest")
public class LabelsApiMockTest extends BaseBitbucketMockTest {

    public void testListAllLabels() throws IOException {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/labels-list.json")).setResponseCode(201));

        try (final BitbucketApi baseApi = api(server.url("/").url())) {
            final LabelsPage labelsPage = baseApi.labelsApi().list(null);
            assertThat(labelsPage).isNotNull();
            assertThat(labelsPage.errors().isEmpty()).isTrue();
            assertThat(labelsPage.values().size() > 0).isTrue();

            assertThat(labelsPage.values().get(0).name()).isEqualTo("labelName");

        } finally {
            server.shutdown();
        }
    }

    public void testListAllLabelError() throws IOException {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(401));

        try (final BitbucketApi baseApi = api(server.url("/").url())) {
            final LabelsPage labelsPage = baseApi.labelsApi().list(null);
            assertThat(labelsPage).isNotNull();
            assertThat(labelsPage.values().size() > 0).isFalse();

            assertThat(labelsPage.errors()).hasSizeGreaterThan(0);

        } finally {
            server.shutdown();
        }
    }
}
