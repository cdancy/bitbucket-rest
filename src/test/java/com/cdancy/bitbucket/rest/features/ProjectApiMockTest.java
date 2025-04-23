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
import static org.assertj.core.api.Assertions.assertWith;

import java.util.Map;

import com.cdancy.bitbucket.rest.domain.project.ProjectPermissions;
import com.cdancy.bitbucket.rest.domain.project.ProjectPermissions.PermissionsType;
import com.cdancy.bitbucket.rest.domain.project.ProjectPermissionsPage;
import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.project.ProjectPage;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link ProjectApi} class.
 */
@Test(groups = "unit", testName = "ProjectApiMockTest")
public class ProjectApiMockTest extends BaseBitbucketMockTest {

    private final String localMethod = "GET";
    private final String localPath = "/projects";

    private final String projectKey = "PRJ";
    private final String getMethod = "GET";
    private final String deleteMethod = "DELETE";
    private final String putMethod = "PUT";

    private final String restApiPath = "/rest/api/";
    private final String projectsPath = "/projects/";
    private final String permissionsPath = "/permissions/";
    private final String usersPath = permissionsPath + "users";
    private final String groupsPath = permissionsPath + "groups";

    private final String limitKeyword = "limit";
    private final String startKeyword = "start";
    private final String nameKeyword = "name";
    private final String permissionKeyword = "permission";
    private final String oneTwoThreeKeyword = "123";
    private final String testOneTwoThreeKeyword = "test" + oneTwoThreeKeyword;

    public void testCreateProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project.json"))
                .setResponseCode(201));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final String projectKey = "HELLO";
            final CreateProject createProject = CreateProject.create(projectKey, null, null, null);
            final Project project = baseApi.projectApi().create(createProject);

            assertThat(project).isNotNull();
            assertThat(project.errors()).isEmpty();
            assertThat(project.key()).isEqualToIgnoringCase(projectKey);
            assertThat(project.name()).isEqualToIgnoringCase(projectKey);
            assertThat(project.links()).isNotNull();
            assertSent(server, "POST", restBasePath + BitbucketApiMetadata.API_VERSION + localPath);
        } finally {
            server.shutdown();
        }
    }

    public void testCreateProjectWithIllegalName() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-create-fail.json"))
                .setResponseCode(400));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final String projectKey = "9999";
            final CreateProject createProject = CreateProject.create(projectKey, null, null, null);
            final Project project = baseApi.projectApi().create(createProject);

            assertThat(project).isNotNull();
            assertThat(project.errors()).isNotEmpty();
            assertSent(server, "POST", restBasePath + BitbucketApiMetadata.API_VERSION + localPath);
        } finally {
            server.shutdown();
        }
    }

    public void testGetProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final String projectKey = "HELLO";
            final Project project = baseApi.projectApi().get(projectKey);

            assertThat(project).isNotNull();
            assertThat(project.errors()).isEmpty();
            assertThat(project.key()).isEqualToIgnoringCase(projectKey);
            assertThat(project.links()).isNotNull();
            assertSent(server, localMethod, restBasePath + BitbucketApiMetadata.API_VERSION + localPath + "/" + projectKey);
        } finally {
            server.shutdown();
        }
    }

    public void testGetProjectNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-not-exist.json"))
                .setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final String projectKey = "HelloWorld";
            final Project project = baseApi.projectApi().get(projectKey);

            assertThat(project).isNotNull();
            assertThat(project.errors()).isNotEmpty();
            assertSent(server, localMethod, restBasePath + BitbucketApiMetadata.API_VERSION + localPath + "/" + projectKey);
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final String projectKey = "HELLO";
            final RequestStatus success = baseApi.projectApi().delete(projectKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey);
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteProjectNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-not-exist.json"))
                .setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final String projectKey = "NOTEXIST";
            final RequestStatus success = baseApi.projectApi().delete(projectKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey);
        } finally {
            server.shutdown();
        }
    }

    public void testGetProjectList() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-page-full.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final ProjectPage projectPage = baseApi.projectApi().list(null, null, null, null);

            assertThat(projectPage).isNotNull();
            assertThat(projectPage.errors()).isEmpty();

            assertThat(projectPage.size()).isLessThanOrEqualTo(projectPage.limit());
            assertThat(projectPage.start()).isEqualTo(0);
            assertThat(projectPage.isLastPage()).isTrue();

            assertThat(projectPage.values()).hasSize(projectPage.size());
            assertThat(projectPage.values()).hasOnlyElementsOfType(Project.class);
            assertSent(server, localMethod, restBasePath + BitbucketApiMetadata.API_VERSION + localPath);
        } finally {
            server.shutdown();
        }
    }

    public void testGetProjectListWithLimit() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-page-truncated.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final int start = 0;
            final int limit = 2;
            final ProjectPage projectPage = baseApi.projectApi().list(null, null, start, limit);

            assertThat(projectPage).isNotNull();
            assertThat(projectPage.errors()).isEmpty();

            final int size = projectPage.size();

            assertThat(size).isEqualTo(limit);
            assertThat(projectPage.start()).isEqualTo(start);
            assertThat(projectPage.limit()).isEqualTo(limit);
            assertThat(projectPage.isLastPage()).isFalse();
            assertThat(projectPage.nextPageStart()).isEqualTo(size);

            assertThat(projectPage.values()).hasSize(size);
            assertThat(projectPage.values()).hasOnlyElementsOfType(Project.class);

            final Map<String, ?> queryParams = ImmutableMap.of("start", start, "limit", limit);
            assertSent(server, localMethod, restBasePath + BitbucketApiMetadata.API_VERSION + localPath, queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testGetProjectListNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-not-exist.json"))
                .setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final ProjectPage projectPage = baseApi.projectApi().list(null, null, null, null);

            assertThat(projectPage).isNotNull();
            assertThat(projectPage.errors()).isNotEmpty();
            assertSent(server, localMethod, restBasePath + BitbucketApiMetadata.API_VERSION + localPath);
        } finally {
            server.shutdown();
        }
    }

    public void testCreatePermissionByUser() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final RequestStatus success = api.createPermissionsByUser(projectKey, testOneTwoThreeKeyword, oneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, oneTwoThreeKeyword, permissionKeyword, testOneTwoThreeKeyword);
            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPermissionByUser() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project-permission-users.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final ProjectPermissionsPage projectPermissionsPage = api.listPermissionsByUser(projectKey, 0, 100);
            assertThat(projectPermissionsPage).isNotNull();
            assertThat(projectPermissionsPage.errors()).isEmpty();
            assertThat(projectPermissionsPage.size() == 1).isTrue();
            assertThat(projectPermissionsPage.values().get(0).group() == null).isTrue();
            assertThat(projectPermissionsPage.values().get(0).user().name().equals("test")).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                        + projectsPath + projectKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreatePermissionByGroup() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final RequestStatus success = api.createPermissionsByGroup(projectKey, testOneTwoThreeKeyword, oneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, oneTwoThreeKeyword, permissionKeyword, testOneTwoThreeKeyword);
            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPermissionByGroup() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project-permission-group.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final ProjectPermissionsPage projectPermissionsPage = api.listPermissionsByGroup(projectKey, 0, 100);
            assertThat(projectPermissionsPage).isNotNull();
            assertThat(projectPermissionsPage.errors()).isEmpty();
            assertWith(projectPermissionsPage.values(), projectPermissions -> {
                assertThat(projectPermissions).hasSize(2);
                assertThat(projectPermissions.get(0).user()).isNull();
                assertThat(projectPermissions.get(0).group().name()).isEqualTo("test12345");
                assertThat(projectPermissions.get(1).permission()).isEqualTo(PermissionsType.REPO_CREATE);
            });

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreatePermissionByUserOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final RequestStatus success = api.createPermissionsByUser(projectKey, testOneTwoThreeKeyword, oneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, oneTwoThreeKeyword, permissionKeyword, testOneTwoThreeKeyword);
            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPermissionByUserOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project-permission-users-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final ProjectPermissionsPage projectPermissionsPage = api.listPermissionsByUser(projectKey, 0, 100);
            assertThat(projectPermissionsPage).isNotNull();
            assertThat(projectPermissionsPage.values()).isEmpty();
            assertThat(projectPermissionsPage.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreatePermissionByGroupOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final RequestStatus success = api.createPermissionsByGroup(projectKey, testOneTwoThreeKeyword, oneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, oneTwoThreeKeyword, permissionKeyword, testOneTwoThreeKeyword);
            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePermissionByUser() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final RequestStatus success = api.deletePermissionsByUser(projectKey, testOneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, testOneTwoThreeKeyword);
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePermissionByGroup() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final RequestStatus success = api.deletePermissionsByGroup(projectKey, testOneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, testOneTwoThreeKeyword);
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePermissionByUserOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final RequestStatus success = api.deletePermissionsByUser(projectKey, testOneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, testOneTwoThreeKeyword);
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePermissionByGroupOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final RequestStatus success = api.deletePermissionsByGroup(projectKey, testOneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, testOneTwoThreeKeyword);
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPermissionByGroupOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project-permission-group-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final ProjectApi api = baseApi.projectApi();
        try {

            final ProjectPermissionsPage projectPermissionsPage = api.listPermissionsByGroup(projectKey, 0, 100);
            assertThat(projectPermissionsPage).isNotNull();
            assertThat(projectPermissionsPage.values()).isEmpty();
            assertThat(projectPermissionsPage.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

}
