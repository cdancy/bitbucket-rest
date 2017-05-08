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

import com.cdancy.bitbucket.rest.domain.repository.PermissionsPage;
import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link RepositoryApi} class.
 */
@Test(groups = "unit", testName = "RepositoryApiMockTest")
public class RepositoryApiMockTest extends BaseBitbucketMockTest {

    public void testCreateRepository() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository.json")).setResponseCode(201));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            CreateRepository createRepository = CreateRepository.create(repoKey, true);
            Repository repository = api.create(projectKey, createRepository);
            assertThat(repository).isNotNull();
            assertThat(repository.errors()).isEmpty();
            assertThat(repository.slug()).isEqualToIgnoringCase(repoKey);
            assertThat(repository.name()).isEqualToIgnoringCase(repoKey);
            assertThat(repository.links()).isNotNull();
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateRepositoryWithIllegalName() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-illegal-name.json")).setResponseCode(400));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "!_myrepo";
            CreateRepository createRepository = CreateRepository.create(repoKey, true);
            Repository repository = api.create(projectKey, createRepository);
            assertThat(repository).isNotNull();
            assertThat(repository.errors()).isNotEmpty();
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetRepository() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            Repository repository = api.get(projectKey, repoKey);
            assertThat(repository).isNotNull();
            assertThat(repository.errors()).isEmpty();
            assertThat(repository.slug()).isEqualToIgnoringCase(repoKey);
            assertThat(repository.name()).isEqualToIgnoringCase(repoKey);
            assertThat(repository.links()).isNotNull();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetRepositoryNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "notexist";
            Repository repository = api.get(projectKey, repoKey);
            assertThat(repository).isNotNull();
            assertThat(repository.errors()).isNotEmpty();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteRepository() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(202));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            boolean success = api.delete(projectKey, repoKey);
            assertThat(success).isTrue();
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteRepositoryNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "notexist";
            boolean success = api.delete(projectKey, repoKey);
            assertThat(success).isTrue();
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetRepositoryList() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-page-full.json")).setResponseCode(200));
        try (BitbucketApi baseApi = api(server.getUrl("/"))) {
            RepositoryApi api = baseApi.repositoryApi();

            String projectKey = "PRJ1";
            RepositoryPage repositoryPage = api.list(projectKey, null, null);

            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos");

            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();

            int size = repositoryPage.size();
            int limit = repositoryPage.limit();

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
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-page-truncated.json")).setResponseCode(200));
        try (BitbucketApi baseApi = api(server.getUrl("/"))) {
            RepositoryApi api = baseApi.repositoryApi();

            String projectKey = "PRJ1";
            int start = 0;
            int limit = 2;
            RepositoryPage repositoryPage = api.list(projectKey, start, limit);

            Map<String, ?> queryParams = ImmutableMap.of("start", start, "limit", limit);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos", queryParams);

            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();

            int size = repositoryPage.size();

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
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        try (BitbucketApi baseApi = api(server.getUrl("/"))) {
            RepositoryApi api = baseApi.repositoryApi();

            String projectKey = "non-existent";
            RepositoryPage repositoryPage = api.list(projectKey, null, null);

            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isNotEmpty();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos");
        } finally {
            server.shutdown();
        }
    }

    public void testListPermissionGroup() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-permission-group.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ1";
            String repoKey = "1234";
            PermissionsPage permissionsPage = api.listPermissionsGroup(projectKey, repoKey, 0, 100);
            assertThat(permissionsPage).isNotNull();
            assertThat(permissionsPage.errors()).isEmpty();
            assertThat(permissionsPage.size() == 1).isTrue();
            assertThat(permissionsPage.values().get(0).user() == null).isTrue();
            assertThat(permissionsPage.values().get(0).group().name().equals("test12345")).isTrue();

            Map<String, ?> queryParams = ImmutableMap.of("limit", 100, "start", 0);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/permissions/groups", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPermissionGroupOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-permission-group-error.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ1";
            String repoKey = "1234";
            PermissionsPage permissionsPage = api.listPermissionsGroup(projectKey, repoKey, 0, 100);
            assertThat(permissionsPage).isNotNull();
            assertThat(permissionsPage.values()).isEmpty();
            assertThat(permissionsPage.errors()).isNotEmpty();

            Map<String, ?> queryParams = ImmutableMap.of("limit", 100, "start", 0);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                   + "/projects/" + projectKey + "/repos/" + repoKey + "/permissions/groups", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
