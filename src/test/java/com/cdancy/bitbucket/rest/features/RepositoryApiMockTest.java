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
import com.cdancy.bitbucket.rest.domain.category.RepositoryCategoriesPage;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.labels.LabelsPage;
import com.cdancy.bitbucket.rest.domain.repository.MergeConfig;
import com.cdancy.bitbucket.rest.domain.repository.MergeStrategy;
import com.cdancy.bitbucket.rest.domain.repository.PermissionsPage;
import com.cdancy.bitbucket.rest.domain.repository.PullRequestSettings;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreatePullRequestSettings;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mock tests for the {@link RepositoryApi} class.
 */
@Test(groups = "unit", testName = "RepositoryApiMockTest")
public class RepositoryApiMockTest extends BaseBitbucketMockTest {

    private final String projectKey = "PRJ";
    private final String repoKey = "my-repo";
    private final String getMethod = "GET";
    private final String postMethod = "POST";
    private final String deleteMethod = "DELETE";
    private final String putMethod = "PUT";

    private final String restApiPath = "/rest/api/";
    private final String projectsPath = "/projects/";
    private final String permissionsPath = "/permissions/";
    private final String usersPath = permissionsPath + "users";
    private final String groupsPath = permissionsPath + "groups";
    private final String labelsPath = "/labels";
    private final String settingsPath = "/settings/";
    private final String pullRequestsPath = settingsPath + "pull-requests";
    private final String reposPath = "/repos/";
    private final String reposEndpoint = "/repos";

    private final String projectKeyword = "projectname";
    private final String limitKeyword = "limit";
    private final String startKeyword = "start";
    private final String nameKeyword = "name";
    private final String permissionKeyword = "permission";
    private final String oneTwoThreeKeyword = "123";
    private final String testOneTwoThreeKeyword = "test" + oneTwoThreeKeyword;

    public void testCreateRepository() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository.json")).setResponseCode(201));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final CreateRepository createRepository = CreateRepository.create(repoKey, null, true);
            final Repository repository = api.create(projectKey, createRepository);
            assertThat(repository).isNotNull();
            assertThat(repository.errors()).isEmpty();
            assertThat(repository.slug()).isEqualToIgnoringCase(repoKey);
            assertThat(repository.name()).isEqualToIgnoringCase(repoKey);
            assertThat(repository.links()).isNotNull();
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION + projectsPath + projectKey + reposEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateRepositoryWithIllegalName() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-illegal-name.json")).setResponseCode(400));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final CreateRepository createRepository = CreateRepository.create("!_myrepo", null, true);
            final Repository repository = api.create(projectKey, createRepository);
            assertThat(repository).isNotNull();
            assertThat(repository.errors()).isNotEmpty();
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION + projectsPath + projectKey + reposEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetRepository() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final Repository repository = api.get(projectKey, repoKey);
            assertThat(repository).isNotNull();
            assertThat(repository.errors()).isEmpty();
            assertThat(repository.slug()).isEqualToIgnoringCase(repoKey);
            assertThat(repository.name()).isEqualToIgnoringCase(repoKey);
            assertThat(repository.links()).isNotNull();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION + projectsPath + projectKey + reposPath + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetRepositoryNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {
            final String nonExistentRepoKey = "notexist";
            final Repository repository = api.get(projectKey, nonExistentRepoKey);
            assertThat(repository).isNotNull();
            assertThat(repository.errors()).isNotEmpty();
            assertSent(server, getMethod, restApiPath
                    + BitbucketApiMetadata.API_VERSION
                    + projectsPath
                    + projectKey
                    + reposPath
                    + nonExistentRepoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testForkRepository() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-fork.json")).setResponseCode(201));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String forkName = "hello-world";
            final Repository repository = api.fork(projectKey, repoKey, projectKey, forkName);
            assertThat(repository).isNotNull();
            assertThat(repository.errors()).isEmpty();
            assertThat(repository.slug()).isEqualToIgnoringCase(forkName);
            assertThat(repository.origin()).isNotNull();
            assertSent(server, postMethod, restApiPath
                    + BitbucketApiMetadata.API_VERSION
                    + projectsPath
                    + projectKey
                    + reposEndpoint
                    + "/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testForkRepositoryWithErrors() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(400));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String forkName = "hello-world";
            final String nonExistentRepo = UUID.randomUUID().toString();
            final Repository repository = api.fork(projectKey, repoKey, nonExistentRepo, forkName);
            assertThat(repository).isNotNull();
            assertThat(repository.errors()).isNotEmpty();
            assertSent(server, postMethod, restApiPath
                    + BitbucketApiMetadata.API_VERSION
                    + projectsPath
                    + projectKey
                    + reposEndpoint
                    + "/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteRepository() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(202));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final RequestStatus success = api.delete(projectKey, repoKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION + projectsPath + projectKey + reposPath + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteRepositoryNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String nonExistentRepoKey = "notexist";
            final RequestStatus success = api.delete(projectKey, nonExistentRepoKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, deleteMethod, restApiPath
                    + BitbucketApiMetadata.API_VERSION
                    + projectsPath
                    + projectKey
                    + reposPath
                    + nonExistentRepoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetRepositoryList() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-page-full.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();

            final RepositoryPage repositoryPage = api.list(projectKey, null, null);

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION + projectsPath + projectKey + reposEndpoint);
            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();

            final int size = repositoryPage.size();
            final int limit = repositoryPage.limit();

            assertThat(size).isLessThanOrEqualTo(limit);
            assertThat(repositoryPage.start()).isEqualTo(0);
            assertThat(repositoryPage.isLastPage()).isTrue();
            assertThat(repositoryPage.values()).hasSize(size);
            assertThat(repositoryPage.values()).hasOnlyElementsOfType(Repository.class);
        } finally {
            server.shutdown();
        }
    }

    public void testGetRepositoryListWithLimit() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-page-truncated.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();

            final int start = 0;
            final int limit = 2;
            final RepositoryPage repositoryPage = api.list(projectKey, start, limit);

            final Map<String, ?> queryParams = ImmutableMap.of(startKeyword, start, limitKeyword, limit);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION + projectsPath + projectKey + reposEndpoint, queryParams);
            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();

            final int size = repositoryPage.size();

            assertThat(size).isEqualTo(limit);
            assertThat(repositoryPage.start()).isEqualTo(start);
            assertThat(repositoryPage.limit()).isEqualTo(limit);
            assertThat(repositoryPage.isLastPage()).isFalse();
            assertThat(repositoryPage.nextPageStart()).isEqualTo(size);
            assertThat(repositoryPage.values()).hasSize(size);
            assertThat(repositoryPage.values()).hasOnlyElementsOfType(Repository.class);
        } finally {
            server.shutdown();
        }
    }

    public void testGetRepositoryListNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();

            final String nonExistentProjectKey = "non-existent";
            final RepositoryPage repositoryPage = api.list(nonExistentProjectKey, null, null);

            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isNotEmpty();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION + projectsPath + nonExistentProjectKey + reposEndpoint);
        } finally {
            server.shutdown();
        }
    }

    public void testListAllRepositories() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-page-full.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();

            final RepositoryPage repositoryPage = api.listAll(null, null, null, null, null, null);

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + reposEndpoint);
            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();

            final int size = repositoryPage.size();
            final int limit = repositoryPage.limit();

            assertThat(size).isLessThanOrEqualTo(limit);
            assertThat(repositoryPage.start()).isEqualTo(0);
            assertThat(repositoryPage.isLastPage()).isTrue();
            assertThat(repositoryPage.values()).hasSize(size);
            assertThat(repositoryPage.values()).hasOnlyElementsOfType(Repository.class);
        } finally {
            server.shutdown();
        }
    }

    public void testListAllRepositoriesWithLimit() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(
                new MockResponse().setBody(payloadFromResource("/repository-page-truncated.json"))
                        .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();

            final int start = 0;
            final int limit = 2;
            final RepositoryPage repositoryPage = api.listAll(null, null, null, null, start, limit);

            final Map<String, ?> queryParams =
                    ImmutableMap.of(startKeyword, start, limitKeyword, limit);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + reposEndpoint, queryParams);
            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();

            final int size = repositoryPage.size();

            assertThat(size).isEqualTo(limit);
            assertThat(repositoryPage.start()).isEqualTo(start);
            assertThat(repositoryPage.limit()).isEqualTo(limit);
            assertThat(repositoryPage.isLastPage()).isFalse();
            assertThat(repositoryPage.nextPageStart()).isEqualTo(size);
            assertThat(repositoryPage.values()).hasSize(size);
            assertThat(repositoryPage.values()).hasOnlyElementsOfType(Repository.class);
        } finally {
            server.shutdown();
        }
    }

    public void testListAllRepositoriesByProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-page-single.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();

            final RepositoryPage repositoryPage = api.listAll(projectKey, null, null, null, null, null);

            final Map<String, ?> queryParams = ImmutableMap.of(projectKeyword, projectKey);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + reposEndpoint, queryParams);
            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();

            assertThat(repositoryPage.size()).isEqualTo(1);
            assertThat(repositoryPage.start()).isEqualTo(0);
            assertThat(repositoryPage.isLastPage()).isTrue();
            assertThat(repositoryPage.values()).isNotEmpty();
        } finally {
            server.shutdown();
        }
    }


    public void testListAllRepositoriesByRepository() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-page-single.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();

            final RepositoryPage repositoryPage = api.listAll(null, repoKey, null, null, null, null);

            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, repoKey);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + reposEndpoint, queryParams);
            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();

            assertThat(repositoryPage.size()).isEqualTo(1);
            assertThat(repositoryPage.start()).isEqualTo(0);
            assertThat(repositoryPage.isLastPage()).isTrue();
            assertThat(repositoryPage.values()).isNotEmpty();
        } finally {
            server.shutdown();
        }
    }

    public void testListAllRepositoriesByProjectNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-page-empty.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();

            final RepositoryPage repositoryPage = api.listAll(projectKey, null, null, null, null, null);

            final Map<String, ?> queryParams = ImmutableMap.of(projectKeyword, projectKey);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + reposEndpoint, queryParams);
            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();

            assertThat(repositoryPage.size()).isEqualTo(0);
            assertThat(repositoryPage.start()).isEqualTo(0);
            assertThat(repositoryPage.isLastPage()).isTrue();
            assertThat(repositoryPage.values()).isEmpty();
        } finally {
            server.shutdown();
        }
    }

    public void testListAllRepositoriesByRepositoryNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-page-empty.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();

            final RepositoryPage repositoryPage = api.listAll(null, repoKey, null, null, null, null);

            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, repoKey);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + reposEndpoint, queryParams);
            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();

            assertThat(repositoryPage.size()).isEqualTo(0);
            assertThat(repositoryPage.start()).isEqualTo(0);
            assertThat(repositoryPage.isLastPage()).isTrue();
            assertThat(repositoryPage.values()).isEmpty();
        } finally {
            server.shutdown();
        }
    }

    public void testGetPullRequestSettings() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-settings.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();
            final PullRequestSettings settings = api.getPullRequestSettings(projectKey, repoKey);

            assertThat(settings).isNotNull();
            assertThat(settings.errors()).isEmpty();
            assertThat(settings.requiredAllApprovers()).isFalse();
            assertThat(settings.requiredAllTasksComplete()).isTrue();
            assertThat(settings.unapproveOnUpdate()).isTrue();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath);
        } finally {
            server.shutdown();
        }
    }

    public void testCreatePermissionByUser() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final RequestStatus success = api.createPermissionsByUser(projectKey, repoKey, testOneTwoThreeKeyword, oneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, oneTwoThreeKeyword, permissionKeyword, testOneTwoThreeKeyword);
            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPermissionByUser() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-permission-users.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final PermissionsPage permissionsPage = api.listPermissionsByUser(projectKey, repoKey, 0, 100);
            assertThat(permissionsPage).isNotNull();
            assertThat(permissionsPage.errors()).isEmpty();
            assertThat(permissionsPage.size() == 1).isTrue();
            assertThat(permissionsPage.values().get(0).group() == null).isTrue();
            assertThat(permissionsPage.values().get(0).user().name().equals("test")).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestSettingsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-settings-error.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();
            final PullRequestSettings settings = api.getPullRequestSettings(projectKey, repoKey);

            assertThat(settings).isNotNull();
            assertThat(settings.errors()).isNotEmpty();
            assertThat(settings.requiredAllApprovers()).isNull();
            assertThat(settings.requiredAllTasksComplete()).isNull();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath);
        } finally {
            server.shutdown();
        }
    }

    public void testCreatePermissionByGroup() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final RequestStatus success = api.createPermissionsByGroup(projectKey, repoKey, testOneTwoThreeKeyword, oneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, oneTwoThreeKeyword, permissionKeyword, testOneTwoThreeKeyword);
            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPermissionByGroup() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-permission-group.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final PermissionsPage permissionsPage = api.listPermissionsByGroup(projectKey, repoKey, 0, 100);
            assertThat(permissionsPage).isNotNull();
            assertThat(permissionsPage.errors()).isEmpty();
            assertThat(permissionsPage.size() == 1).isTrue();
            assertThat(permissionsPage.values().get(0).user() == null).isTrue();
            assertThat(permissionsPage.values().get(0).group().name().equals("test12345")).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdatePullRequestSettings() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-settings.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final MergeStrategy strategy = MergeStrategy.create(null, null, null, MergeStrategy.MergeStrategyId.FF, null);
            final List<MergeStrategy> listStrategy = new ArrayList<>();
            listStrategy.add(strategy);
            final MergeConfig mergeConfig = MergeConfig.create(strategy, listStrategy, MergeConfig.MergeConfigType.REPOSITORY);
            final CreatePullRequestSettings pullRequestSettings = CreatePullRequestSettings.create(mergeConfig, false, false, 0, 1, true);
            final PullRequestSettings settings = api.updatePullRequestSettings(projectKey, repoKey, pullRequestSettings);

            assertThat(settings).isNotNull();
            assertThat(settings.errors()).isEmpty();
            assertThat(settings.requiredAllApprovers()).isFalse();
            assertThat(settings.requiredAllTasksComplete()).isTrue();
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreatePermissionByUserOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final RequestStatus success = api.createPermissionsByUser(projectKey, repoKey, testOneTwoThreeKeyword, oneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, oneTwoThreeKeyword, permissionKeyword, testOneTwoThreeKeyword);
            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdatePullRequestSettingsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-settings-error.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            final RepositoryApi api = baseApi.repositoryApi();

            final MergeStrategy strategy = MergeStrategy.create(null, null, null, MergeStrategy.MergeStrategyId.FF, null);
            final List<MergeStrategy> listStrategy = new ArrayList<>();
            listStrategy.add(strategy);
            final MergeConfig mergeConfig = MergeConfig.create(strategy, listStrategy, MergeConfig.MergeConfigType.REPOSITORY);
            final CreatePullRequestSettings pullRequestSettings = CreatePullRequestSettings.create(mergeConfig, false, false, 0, 1, true);
            final PullRequestSettings settings = api.updatePullRequestSettings(projectKey, repoKey, pullRequestSettings);

            assertThat(settings).isNotNull();
            assertThat(settings.errors()).isNotEmpty();
            assertThat(settings.requiredAllApprovers()).isNull();
            assertThat(settings.requiredAllTasksComplete()).isNull();
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath);
        } finally {
            server.shutdown();
        }
    }

    public void testListPermissionByUserOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-permission-users-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final PermissionsPage permissionsPage = api.listPermissionsByUser(projectKey, repoKey, 0, 100);
            assertThat(permissionsPage).isNotNull();
            assertThat(permissionsPage.values()).isEmpty();
            assertThat(permissionsPage.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreatePermissionByGroupOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final RequestStatus success = api.createPermissionsByGroup(projectKey, repoKey, testOneTwoThreeKeyword, oneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, oneTwoThreeKeyword, permissionKeyword, testOneTwoThreeKeyword);
            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePermissionByUser() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final RequestStatus success = api.deletePermissionsByUser(projectKey, repoKey, testOneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, testOneTwoThreeKeyword);
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePermissionByGroup() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final RequestStatus success = api.deletePermissionsByGroup(projectKey, repoKey, testOneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, testOneTwoThreeKeyword);
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePermissionByUserOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final RequestStatus success = api.deletePermissionsByUser(projectKey, repoKey, testOneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, testOneTwoThreeKeyword);
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + usersPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePermissionByGroupOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final RequestStatus success = api.deletePermissionsByGroup(projectKey, repoKey, testOneTwoThreeKeyword);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(nameKeyword, testOneTwoThreeKeyword);
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPermissionByGroupOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-permission-group-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final PermissionsPage permissionsPage = api.listPermissionsByGroup(projectKey, repoKey, 0, 100);
            assertThat(permissionsPage).isNotNull();
            assertThat(permissionsPage.values()).isEmpty();
            assertThat(permissionsPage.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + groupsPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetLabels() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-labels.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {
            final LabelsPage labelsPage = api.getLabels(projectKey, repoKey);
            assertThat(labelsPage).isNotNull();
            assertThat(labelsPage.errors()).isEmpty();
            assertThat(labelsPage.values()).isNotEmpty();
            assertSent(server,
                getMethod,
                restBasePath + BitbucketApiMetadata.API_VERSION + projectsPath + projectKey + reposPath + repoKey + labelsPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListCategories() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-categories.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {
            final RepositoryCategoriesPage repositoryCategoriesPage = api.listCategories(projectKey, repoKey);
            assertThat(repositoryCategoriesPage).isNotNull();
            assertThat(repositoryCategoriesPage.result()).isNotNull();
            assertThat(repositoryCategoriesPage.result().categories()).hasSize(2);
            assertThat(repositoryCategoriesPage.result().projectKey()).isEqualTo(projectKey);
            assertThat(repositoryCategoriesPage.result().repositorySlug()).isEqualTo(repoKey);
            assertThat(repositoryCategoriesPage.result().categories().get(0).title()).isEqualTo("cat1");
            assertThat(repositoryCategoriesPage.result().categories().get(1).title()).isEqualTo("cat2");
            assertSent(server,
                getMethod,
                "/rest/categories/latest/project/" + projectKey + "/repository/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
