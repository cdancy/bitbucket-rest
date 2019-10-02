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

import java.util.Map;

import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "UserApiMockTest")
public class UserApiMockTest extends BaseBitbucketMockTest {

    private final String restApiPath = "/rest/api/";
    private final String getMethod = "GET";
    private final String admin = "ADMIN";
    private final String group = "stash-users";


    public void testUserListUsers() throws Exception {
    
    
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/user-list-users.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final UserPage up = baseApi.userApi().users("jcitizen", group, admin, null,
                    null, null, null, null, null, 0, 2);
            assertThat(up).isNotNull();
            assertThat(up.errors()).isEmpty();
            assertThat(up.size() == 1).isTrue();
            assertThat(up.values().get(0).slug().equals("jcitizen")).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of("filter", "jcitizen",
                    "group", group, "permission", admin,"start",0,"limit",2);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/users", queryParams);
        } finally {
            server.shutdown();
        }
    }
    
    public void testUserListUsersOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/user-list-users-error.json")).setResponseCode(401));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        try {
            final UserPage up = baseApi.userApi().users("blah blah", group, admin, null,
                    null, null, null, null, null, 0, 2);            
            assertThat(up).isNotNull();
            assertThat(up.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("filter", "blah%20blah",
                    "group", group, "permission", admin,"start",0,"limit",2);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/users", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    



}
