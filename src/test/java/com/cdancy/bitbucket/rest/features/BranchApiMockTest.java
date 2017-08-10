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
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.BranchConfiguration;
import com.cdancy.bitbucket.rest.domain.branch.BranchModel;
import com.cdancy.bitbucket.rest.domain.branch.BranchModelConfiguration;
import com.cdancy.bitbucket.rest.domain.branch.BranchPage;
import com.cdancy.bitbucket.rest.domain.branch.BranchRestriction;
import com.cdancy.bitbucket.rest.domain.branch.BranchRestrictionEnumType;
import com.cdancy.bitbucket.rest.domain.branch.BranchRestrictionPage;
import com.cdancy.bitbucket.rest.domain.branch.Matcher;
import com.cdancy.bitbucket.rest.domain.branch.Type;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.domain.common.Veto;
import com.cdancy.bitbucket.rest.options.CreateBranch;
import com.cdancy.bitbucket.rest.options.CreateBranchModelConfiguration;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mock tests for the {@link BranchApi} class.
 */
@Test(groups = "unit", testName = "BranchApiMockTest")
public class BranchApiMockTest extends BaseBitbucketMockTest {

    public void testCreateBranch() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            String branchName = "dev-branch";
            String commitHash = "8d351a10fb428c0c1239530256e21cf24f136e73";

            CreateBranch createBranch = CreateBranch.create(branchName, commitHash, null);
            Branch branch = api.create(projectKey, repoKey, createBranch);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().isEmpty()).isTrue();
            assertThat(branch.id().endsWith(branchName)).isTrue();
            assertThat(commitHash.equalsIgnoreCase(branch.latestChangeset())).isTrue();
            assertSent(server, "POST", "/rest/branch-utils/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branches");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListBranches() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-list.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";

            BranchPage branch = api.list(projectKey, repoKey, null, null, null, null, null, 1);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().isEmpty()).isTrue();
            assertThat(branch.values().size() > 0).isTrue();
            assertThat("hello-world".equals(branch.values().get(0).displayId())).isTrue();
            assertThat(branch.values().get(0).metadata()).isNotNull();

            String jiraIssuesKey = "com.atlassian.bitbucket.server.bitbucket-jira:branch-list-jira-issues";
            String commitInfoKey = "com.atlassian.bitbucket.server.bitbucket-branch:latest-commit-metadata";
            String buildStatusKey = "com.atlassian.bitbucket.server.bitbucket-build:build-status-metadata";
            assertThat(branch.values().get(0).metadata().containsKey(jiraIssuesKey)).isNotNull();
            assertThat(branch.values().get(0).metadata().containsKey(commitInfoKey)).isNotNull();
            assertThat(branch.values().get(0).metadata().containsKey(buildStatusKey)).isNotNull();

            JsonObject buildStatusMetadata = ((JsonElement)branch.values().get(0).metadata().get(buildStatusKey)).getAsJsonObject();
            int success = buildStatusMetadata.get("successful").getAsInt();
            assertThat(success).isEqualTo(1);

            Map<String, ?> queryParams = ImmutableMap.of("limit", 1);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branches", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListBranchesNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-list-error.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "hello";
            String repoKey = "world";

            final BranchPage branch = api.list(projectKey, repoKey, null, null, null, null, null, 1);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().size() > 0).isTrue();
            final List<Veto> vetoes = branch.errors().get(0).vetoes();
            assertThat(vetoes.size() > 0).isTrue();
            assertThat(vetoes.get(0).summaryMessage()).isEqualTo("some short message");
            assertThat(vetoes.get(0).detailedMessage()).isEqualTo("some detailed message");
            Map<String, ?> queryParams = ImmutableMap.of("limit", 1);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branches", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetBranchModel() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-model.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            BranchModel branchModel = api.model(projectKey, repoKey);
            assertThat(branchModel).isNotNull();
            assertThat(branchModel.errors().isEmpty()).isTrue();
            assertThat(branchModel.types().size() > 0).isTrue();
            assertSent(server, "GET", "/rest/branch-utils/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branchmodel");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetBranchModelOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-list-error.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            BranchModel branchModel = api.model(projectKey, repoKey);
            assertThat(branchModel).isNotNull();
            assertThat(branchModel.errors()).isNotEmpty();
            assertSent(server, "GET", "/rest/branch-utils/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branchmodel");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteBranch() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            final RequestStatus success = api.delete(projectKey, repoKey, "refs/heads/some-branch-name");
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "DELETE", "/rest/branch-utils/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branches");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetDefaultBranch() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-default.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";

            Branch branch = api.getDefault(projectKey, repoKey);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().isEmpty()).isTrue();
            assertThat(branch.id()).isNotNull();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branches/default");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateDefaultBranch() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";

            final RequestStatus success = api.updateDefault(projectKey, repoKey, "refs/heads/my-new-default-branch");
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "PUT", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branches/default");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListBranchePermissions() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-permission-list.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";

            BranchRestrictionPage branch = api.listBranchRestriction(projectKey, repoKey, null, 1);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().isEmpty()).isTrue();
            assertThat(branch.values().size() > 0).isTrue();
            assertThat(839L == branch.values().get(0).id()).isTrue();
            assertThat(2 == branch.values().get(0).accessKeys().size()).isTrue();

            Map<String, ?> queryParams = ImmutableMap.of("limit", 1);
            assertSent(server, "GET", "/rest/branch-permissions/2.0"
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/restrictions", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListBranchesPermissionsNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-permission-list-error.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "hello";
            String repoKey = "world";

            BranchRestrictionPage branch = api.listBranchRestriction(projectKey, repoKey, null, 1);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().size() > 0).isTrue();

            Map<String, ?> queryParams = ImmutableMap.of("limit", 1);
            assertSent(server, "GET", "/rest/branch-permissions/2.0"
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/restrictions", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateBranchesPermissions() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {

            List<String> groupPermission = new ArrayList<>();
            groupPermission.add("Test12354");
            List<Long> listAccessKey = new ArrayList<>();
            listAccessKey.add(123L);
            List<BranchRestriction> listBranchPermission = new ArrayList<>();
            listBranchPermission.add(BranchRestriction.createWithId(839L, BranchRestrictionEnumType.FAST_FORWARD_ONLY,
                    Matcher.create(Matcher.MatcherId.RELEASE, true), new ArrayList<User>(), groupPermission,
                    listAccessKey));

            String projectKey = "PRJ";
            String repoKey = "myrepo";
            final RequestStatus success = api.createBranchRestriction(projectKey, repoKey, listBranchPermission);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "POST", "/rest/branch-permissions/2.0"
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/restrictions");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteBranchesPermissions() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            Long idToDelete = 839L;
            final RequestStatus success = api.deleteBranchRestriction(projectKey, repoKey, idToDelete);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "DELETE", "/rest/branch-permissions/2.0"
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/restrictions/" + idToDelete);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetBranchModelConfiguration() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-model-configuration.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            BranchModelConfiguration configuration = api.getModelConfiguration(projectKey, repoKey);
            assertThat(configuration).isNotNull();
            assertThat(configuration.errors().isEmpty()).isTrue();
            assertThat(configuration.types().size() > 0).isTrue();
            assertThat(configuration.development().refId().equals("refs/heads/master")).isTrue();
            assertThat(configuration.production()).isNull();
            assertSent(server, "GET", "/rest/branch-utils/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branchmodel/configuration");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetBranchModelConfigurationOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-model-configuration-error.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            BranchModelConfiguration configuration = api.getModelConfiguration(projectKey, repoKey);
            assertThat(configuration).isNotNull();
            assertThat(configuration.errors()).isNotEmpty();
            assertThat(configuration.production()).isNull();
            assertThat(configuration.development()).isNull();
            assertSent(server, "GET", "/rest/branch-utils/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branchmodel/configuration");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateBranchModelConfiguration() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-model-configuration.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";

            BranchConfiguration branchConfiguration = BranchConfiguration.create("test", false);
            List<Type> types = new ArrayList<>();
            types.add(Type.create(Type.TypeId.BUGFIX, "test", "test", true));

            CreateBranchModelConfiguration branchModelConfiguration = CreateBranchModelConfiguration.create(branchConfiguration, null, types);

            BranchModelConfiguration configuration = api.updateModelConfiguration(projectKey, repoKey, branchModelConfiguration);
            assertThat(configuration).isNotNull();
            assertThat(configuration.errors().isEmpty()).isTrue();
            assertThat(configuration.types().size() > 0).isTrue();
            assertSent(server, "PUT", "/rest/branch-utils/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branchmodel/configuration");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateBranchModelConfigurationOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-model-configuration-error.json")).setResponseCode(400));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";

            BranchConfiguration branchConfiguration = BranchConfiguration.create("test", false);
            List<Type> types = new ArrayList<>();
            types.add(Type.create(Type.TypeId.BUGFIX, "test", "test", true));

            CreateBranchModelConfiguration branchModelConfiguration = CreateBranchModelConfiguration.create(branchConfiguration, null, types);

            BranchModelConfiguration configuration = api.updateModelConfiguration(projectKey, repoKey, branchModelConfiguration);
            assertThat(configuration).isNotNull();
            assertThat(configuration.errors()).isNotEmpty();
            assertThat(configuration.production()).isNull();
            assertThat(configuration.development()).isNull();
            assertSent(server, "PUT", "/rest/branch-utils/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branchmodel/configuration");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteBranchModelConfiguration() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            final RequestStatus success = api.deleteModelConfiguration(projectKey, repoKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "DELETE", "/rest/branch-utils/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branchmodel/configuration");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteBranchModelConfigurationOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        BranchApi api = baseApi.branchApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            final RequestStatus success = api.deleteModelConfiguration(projectKey, repoKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, "DELETE", "/rest/branch-utils/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/branchmodel/configuration");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
