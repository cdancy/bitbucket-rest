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
import com.cdancy.bitbucket.rest.domain.repository.MergeConfig;
import com.cdancy.bitbucket.rest.domain.repository.MergeStrategy;
import com.cdancy.bitbucket.rest.domain.repository.PullRequestSettings;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreatePullRequestSettings;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

    public void testGetPullRequestSettings() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-settings.json")).setResponseCode(200));
        try (BitbucketApi baseApi = api(server.getUrl("/"))) {
            RepositoryApi api = baseApi.repositoryApi();

            String projectKey = "PRJ1";
            String repoKey = "test";
            PullRequestSettings settings = api.getPullRequestSettings(projectKey, repoKey);

            assertThat(settings).isNotNull();
            assertThat(settings.errors()).isEmpty();
            assertThat(settings.requiredAllApprovers()).isFalse();
            assertThat(settings.requiredAllTasksComplete()).isTrue();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/settings/pull-requests");
        } finally {
            server.shutdown();
        }
    }

    public void testGetPullRequestSettingsOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-settings-error.json")).setResponseCode(404));
        try (BitbucketApi baseApi = api(server.getUrl("/"))) {
            RepositoryApi api = baseApi.repositoryApi();

            String projectKey = "PRJ1";
            String repoKey = "test";
            PullRequestSettings settings = api.getPullRequestSettings(projectKey, repoKey);

            assertThat(settings).isNotNull();
            assertThat(settings.errors()).isNotEmpty();
            assertThat(settings.requiredAllApprovers()).isNull();
            assertThat(settings.requiredAllTasksComplete()).isNull();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/settings/pull-requests");
        } finally {
            server.shutdown();
        }
    }

    public void testUpdatePullRequestSettings() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-settings.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            MergeStrategy strategy = MergeStrategy.create(null, null, null, MergeStrategy.MergeStrategyId.ff, null);
            List<MergeStrategy> listStrategy = new ArrayList<>();
            listStrategy.add(strategy);
            MergeConfig mergeConfig = MergeConfig.create(strategy, listStrategy, MergeConfig.MergeConfigType.REPOSITORY);
            CreatePullRequestSettings pullRequestSettings = CreatePullRequestSettings.create(mergeConfig, false, false, 0, 1);
            PullRequestSettings settings = api.updatePullRequestSettings(projectKey, repoKey, pullRequestSettings);

            assertThat(settings).isNotNull();
            assertThat(settings.errors()).isEmpty();
            assertThat(settings.requiredAllApprovers()).isFalse();
            assertThat(settings.requiredAllTasksComplete()).isTrue();
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/settings/pull-requests");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdatePullRequestSettingsOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-settings-error.json")).setResponseCode(404));
        try (BitbucketApi baseApi = api(server.getUrl("/"))) {
            RepositoryApi api = baseApi.repositoryApi();

            String projectKey = "PRJ1";
            String repoKey = "test";
            MergeStrategy strategy = MergeStrategy.create(null, null, null, MergeStrategy.MergeStrategyId.ff, null);
            List<MergeStrategy> listStrategy = new ArrayList<>();
            listStrategy.add(strategy);
            MergeConfig mergeConfig = MergeConfig.create(strategy, listStrategy, MergeConfig.MergeConfigType.REPOSITORY);
            CreatePullRequestSettings pullRequestSettings = CreatePullRequestSettings.create(mergeConfig, false, false, 0, 1);
            PullRequestSettings settings = api.updatePullRequestSettings(projectKey, repoKey, pullRequestSettings);

            assertThat(settings).isNotNull();
            assertThat(settings.errors()).isNotEmpty();
            assertThat(settings.requiredAllApprovers()).isNull();
            assertThat(settings.requiredAllTasksComplete()).isNull();
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                + "/projects/" + projectKey + "/repos/" + repoKey + "/settings/pull-requests");
        } finally {
            server.shutdown();
        }
    }
}
