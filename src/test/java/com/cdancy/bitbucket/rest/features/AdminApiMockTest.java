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
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "AdminApiMockTest")
public class AdminApiMockTest extends BaseBitbucketMockTest {

    public void testGetListUserByGroup() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/admin-list-user-by-group.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        AdminApi api = baseApi.adminApi();
        try {
            UserPage up = api.listUserByGroup("test", null, 0, 2);
            assertThat(up).isNotNull();
            assertThat(up.errors()).isEmpty();
            assertThat(up.size() == 2).isTrue();
            assertThat(up.values().get(0).slug().equals("bob123")).isTrue();

            Map<String, ?> queryParams = ImmutableMap.of("context", "test", "limit", 2, "start", 0);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/admin/groups/more-members", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetListUserByGroupOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/admin-list-user-by-group-error.json")).setResponseCode(401));
        BitbucketApi baseApi = api(server.getUrl("/"));
        AdminApi api = baseApi.adminApi();
        try {
            UserPage up = api.listUserByGroup("test", null, 0, 2);
            assertThat(up).isNotNull();
            assertThat(up.errors()).isNotEmpty();

            Map<String, ?> queryParams = ImmutableMap.of("context", "test", "limit", 2, "start", 0);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/admin/groups/more-members", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
