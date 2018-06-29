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
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "AdminApiMockTest")
public class AdminApiMockTest extends BaseBitbucketMockTest {

    private static final String USERS_POSTFIX = "/admin/users";
    private static final String USER_TEXT = "user";
    private static final String NOTIFY_TEXT = "notify";

    private final String limitKeyword = "limit";
    private final String startKeyword = "start";
    private final String restApiPath = "/rest/api/";
    private final String getMethod = "GET";

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

            final Map<String, ?> queryParams = ImmutableMap.of("context", localContext, limitKeyword, 2, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
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

            final Map<String, ?> queryParams = ImmutableMap.of("context", localContext, limitKeyword, 2, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
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

            final Map<String, ?> queryParams = ImmutableMap.of("filter", "jcitizen", limitKeyword, 2, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + USERS_POSTFIX, queryParams);
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

            final Map<String, ?> queryParams = ImmutableMap.of("filter", "blah%20blah", limitKeyword, 2, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + USERS_POSTFIX, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateUser() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final AdminApi api = baseApi.adminApi();
        try {
            final RequestStatus status = api.createUser(USER_TEXT, "pass", "display", "email", false, NOTIFY_TEXT);
            assertThat(status.value()).isTrue();
            assertThat(status.errors()).isEmpty();

            final Map<String, String> queryParams = createUserQueryParams();
            assertSent(server, "POST", restApiPath + BitbucketApiMetadata.API_VERSION + USERS_POSTFIX, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateUserOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(400));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final AdminApi api = baseApi.adminApi();
        try {
            final RequestStatus status = api.createUser(USER_TEXT, "pass", "display", "email", false, NOTIFY_TEXT);
            assertThat(status.value()).isFalse();
            assertThat(status.errors()).isNotEmpty();

            final Map<String, String> queryParams = createUserQueryParams();
            assertSent(server, "POST", restApiPath + BitbucketApiMetadata.API_VERSION + USERS_POSTFIX, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteUser() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/admin-delete-user.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final AdminApi api = baseApi.adminApi();
        try {
            final User user = api.deleteUser(USER_TEXT);
            assertThat(user).isNotNull();
            assertThat(user.errors()).isEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("name", USER_TEXT);
            assertSent(server, "DELETE", restApiPath + BitbucketApiMetadata.API_VERSION + USERS_POSTFIX, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteUserOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/admin-delete-user-error.json")).setResponseCode(400));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final AdminApi api = baseApi.adminApi();
        try {
            final User user = api.deleteUser(USER_TEXT);
            assertThat(user).isNotNull();
            assertThat(user.name()).isNull();
            assertThat(user.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("name", USER_TEXT);
            assertSent(server, "DELETE", restApiPath + BitbucketApiMetadata.API_VERSION + USERS_POSTFIX, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    private static Map<String, String> createUserQueryParams() {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", USER_TEXT);
        queryParams.put("password", "pass");
        queryParams.put("displayName", "display");
        queryParams.put("emailAddress", "email");
        queryParams.put("addToDefaultGroup", "false");
        queryParams.put(NOTIFY_TEXT, NOTIFY_TEXT);
        return queryParams;
    }
}
