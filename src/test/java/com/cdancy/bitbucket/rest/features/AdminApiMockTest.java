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
import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "AdminApiMockTest")
public class AdminApiMockTest extends BaseBitbucketMockTest {

    private final String localContext = "test";
            
    public void testGetListUserByGroup() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/admin-list-user-by-group.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final UserPage up = baseApi.adminApi().listUsersByGroup(localContext, null, 0, 2);
            assertThat(up).isNotNull();
            assertThat(up.errors()).isEmpty();
            assertThat(up.size() == 2).isTrue();
            assertThat(up.values().get(0).slug().equals("bob123")).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of("context", localContext, "limit", 2, "start", 0);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/admin/groups/more-members", queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testGetListUserByGroupOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/admin-list-user-by-group-error.json"))
                .setResponseCode(401));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final UserPage up = baseApi.adminApi().listUsersByGroup(localContext, null, 0, 2);
            assertThat(up).isNotNull();
            assertThat(up.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("context", localContext, "limit", 2, "start", 0);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/admin/groups/more-members", queryParams);
        } finally {
            server.shutdown();
        }
    }
    
    public void testListUsers() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/admin-list-users.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final AdminApi api = baseApi.adminApi();
        try {
            final UserPage up = api.listUsers("jcitizen", 0, 2);
            assertThat(up).isNotNull();
            assertThat(up.errors()).isEmpty();
            assertThat(up.size() == 1).isTrue();
            assertThat(up.values().get(0).slug().equals("jcitizen")).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of("filter", "jcitizen", "limit", 2, "start", 0);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/admin/users", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListUsersOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/admin-list-user-by-group-error.json")).setResponseCode(401));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final AdminApi api = baseApi.adminApi();
        try {
            final UserPage up = api.listUsers("blah blah", 0, 2);
            assertThat(up).isNotNull();
            assertThat(up.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("filter", "blah%20blah", "limit", 2, "start", 0);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/admin/users", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
