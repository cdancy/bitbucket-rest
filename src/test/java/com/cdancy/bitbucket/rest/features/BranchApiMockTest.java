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
import com.google.common.collect.Lists;
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

    private final String projectKey = "PRJ";
    private final String repoKey = "myrepo";
    private final String testKeyword = "test";
    private final String localRestPath = "/rest/branch-utils/";
    private final String localBranchesPath = "/branches";
    private final String localInfoPath = localBranchesPath + "/info";
    private final String localProjectsPath = "/projects/";
    private final String branchPermissionsPath = "/rest/branch-permissions/2.0";
    private final String branchModelPath = "/branchmodel/configuration";
    private final String localReposPath = "/repos/";
    private final String localGetMethod = "GET";
    private final String localDeleteMethod = "DELETE";
    private final String localLimit = "limit";
    private final String commitId = "123456789HelloWorld";

    public void testCreateBranch() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final String branchName = "dev-branch";
            final String commitHash = "8d351a10fb428c0c1239530256e21cf24f136e73";

            final CreateBranch createBranch = CreateBranch.create(branchName, commitHash, null);
            final Branch branch = baseApi.branchApi().create(projectKey, repoKey, createBranch);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().isEmpty()).isTrue();
            assertThat(branch.id().endsWith(branchName)).isTrue();
            assertThat(commitHash.equalsIgnoreCase(branch.latestChangeset())).isTrue();
            assertSent(server, "POST", localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + localBranchesPath);
        } finally {
            server.shutdown();
        }
    }

    public void testListBranches() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-list.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchPage branch = baseApi.branchApi().list(projectKey, repoKey, null, null, null, null, null, 1);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().isEmpty()).isTrue();
            assertThat(branch.values().size() > 0).isTrue();
            assertThat("hello-world".equals(branch.values().get(0).displayId())).isTrue();
            assertThat(branch.values().get(0).metadata()).isNotNull();

            final String jiraIssuesKey = "com.atlassian.bitbucket.server.bitbucket-jira:branch-list-jira-issues";
            final String commitInfoKey = "com.atlassian.bitbucket.server.bitbucket-branch:latest-commit-metadata";
            final String buildStatusKey = "com.atlassian.bitbucket.server.bitbucket-build:build-status-metadata";
            assertThat(branch.values().get(0).metadata().containsKey(jiraIssuesKey)).isNotNull();
            assertThat(branch.values().get(0).metadata().containsKey(commitInfoKey)).isNotNull();
            assertThat(branch.values().get(0).metadata().containsKey(buildStatusKey)).isNotNull();

            final JsonObject buildStatusMetadata = ((JsonElement)branch.values().get(0).metadata().get(buildStatusKey)).getAsJsonObject();
            final int success = buildStatusMetadata.get("successful").getAsInt();
            assertThat(success).isEqualTo(1);

            final Map<String, ?> queryParams = ImmutableMap.of(localLimit, 1);
            assertSent(server, localGetMethod, restBasePath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + localBranchesPath, queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testListBranchesNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-list-error.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchPage branch = baseApi.branchApi().list(projectKey, repoKey, null, null, null, null, null, 1);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().size() > 0).isTrue();
            final List<Veto> vetoes = branch.errors().get(0).vetoes();
            assertThat(vetoes.size() > 0).isTrue();
            assertThat(vetoes.get(0).summaryMessage()).isEqualTo("some short message");
            assertThat(vetoes.get(0).detailedMessage()).isEqualTo("some detailed message");
            final Map<String, ?> queryParams = ImmutableMap.of("limit", 1);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + "/branches", queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testGetBranchModel() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-model.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchModel branchModel = baseApi.branchApi().model(projectKey, repoKey);
            assertThat(branchModel).isNotNull();
            assertThat(branchModel.errors().isEmpty()).isTrue();
            assertThat(branchModel.types().size() > 0).isTrue();
            assertSent(server, localGetMethod, localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + "/branchmodel");
        } finally {
            server.shutdown();
        }
    }

    public void testGetBranchModelOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-list-error.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchModel branchModel = baseApi.branchApi().model(projectKey, repoKey);
            assertThat(branchModel).isNotNull();
            assertThat(branchModel.errors()).isNotEmpty();
            assertSent(server, localGetMethod, localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + "/branchmodel");
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteBranch() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final RequestStatus success = baseApi.branchApi().delete(projectKey, repoKey, "refs/heads/some-branch-name");
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, localDeleteMethod, localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + "/branches");
        } finally {
            server.shutdown();
        }
    }

    public void testGetDefaultBranch() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-default.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final Branch branch = baseApi.branchApi().getDefault(projectKey, repoKey);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().isEmpty()).isTrue();
            assertThat(branch.id()).isNotNull();
            assertSent(server, localGetMethod, restBasePath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + localBranchesPath + "/default");
        } finally {
            server.shutdown();
        }
    }

    public void testGetDefaultBranchEmpty() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody("null").setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final Branch branch = baseApi.branchApi().getDefault(projectKey, repoKey);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().isEmpty()).isTrue();
            assertThat(branch.id()).isNull();
            assertSent(server, localGetMethod, restBasePath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + localBranchesPath + "/default");
        } finally {
            server.shutdown();
        }
    }

    public void testUpdateDefaultBranch() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final RequestStatus success = baseApi.branchApi().updateDefault(projectKey, repoKey, "refs/heads/my-new-default-branch");
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "PUT", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + "/branches/default");
        } finally {
            server.shutdown();
        }
    }

    public void testListBranchePermissions() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-permission-list.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchRestrictionPage branch = baseApi.branchApi().listBranchRestriction(projectKey, repoKey, null, 1);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().isEmpty()).isTrue();
            assertThat(branch.values().size() > 0).isTrue();
            assertThat(839L == branch.values().get(0).id()).isTrue();
            assertThat(2 == branch.values().get(0).accessKeys().size()).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of(localLimit, 1);
            assertSent(server, localGetMethod, branchPermissionsPath
                    + localProjectsPath + projectKey + localReposPath + repoKey + "/restrictions", queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testListBranchesPermissionsNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-permission-list-error.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchRestrictionPage branch = baseApi.branchApi().listBranchRestriction(projectKey, repoKey, null, 1);
            assertThat(branch).isNotNull();
            assertThat(branch.errors().size() > 0).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of(localLimit, 1);
            assertSent(server, localGetMethod, branchPermissionsPath
                    + localProjectsPath + projectKey + localReposPath + repoKey + "/restrictions", queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testUpdateBranchesPermissions() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final List<String> groupPermission = new ArrayList<>();
            groupPermission.add("Test12354");
            final List<Long> listAccessKey = new ArrayList<>();
            listAccessKey.add(123L);
            final List<BranchRestriction> listBranchPermission = new ArrayList<>();
            listBranchPermission.add(BranchRestriction.createWithId(839L, BranchRestrictionEnumType.FAST_FORWARD_ONLY,
                    Matcher.create(Matcher.MatcherId.RELEASE, true), new ArrayList<User>(), groupPermission,
                    listAccessKey));

            final RequestStatus success = baseApi.branchApi().createBranchRestriction(projectKey, repoKey, listBranchPermission);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "POST", branchPermissionsPath
                    + localProjectsPath + projectKey + localReposPath + repoKey + "/restrictions");
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteBranchesPermissions() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final Long idToDelete = 839L;
            final RequestStatus success = baseApi.branchApi().deleteBranchRestriction(projectKey, repoKey, idToDelete);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, localDeleteMethod, branchPermissionsPath
                    + localProjectsPath + projectKey + localReposPath + repoKey + "/restrictions/" + idToDelete);
        } finally {
            server.shutdown();
        }
    }

    public void testGetBranchModelConfiguration() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-model-configuration.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchModelConfiguration configuration = baseApi.branchApi().getModelConfiguration(projectKey, repoKey);
            assertThat(configuration).isNotNull();
            assertThat(configuration.errors().isEmpty()).isTrue();
            assertThat(configuration.types().size() > 0).isTrue();
            assertThat(configuration.development().refId().equals("refs/heads/master")).isTrue();
            assertThat(configuration.production()).isNull();
            assertSent(server, localGetMethod, localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + branchModelPath);
        } finally {
            server.shutdown();
        }
    }

    public void testGetBranchModelConfigurationOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-model-configuration-error.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchModelConfiguration configuration = baseApi.branchApi().getModelConfiguration(projectKey, repoKey);
            assertThat(configuration).isNotNull();
            assertThat(configuration.errors()).isNotEmpty();
            assertThat(configuration.production()).isNull();
            assertThat(configuration.development()).isNull();
            assertSent(server, localGetMethod, localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + branchModelPath);
        } finally {
            server.shutdown();
        }
    }

    public void testUpdateBranchModelConfiguration() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-model-configuration.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchConfiguration branchConfiguration = BranchConfiguration.create(testKeyword, false);
            final List<Type> types = Lists.newArrayList(Type.create(Type.TypeId.BUGFIX, testKeyword, testKeyword, true));

            final CreateBranchModelConfiguration bcm = CreateBranchModelConfiguration.create(branchConfiguration, null, types);

            final BranchModelConfiguration configuration = baseApi.branchApi().updateModelConfiguration(projectKey, repoKey, bcm);
            assertThat(configuration).isNotNull();
            assertThat(configuration.errors().isEmpty()).isTrue();
            assertThat(configuration.types().size() > 0).isTrue();
            assertSent(server, "PUT", localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + branchModelPath);
        } finally {
            server.shutdown();
        }
    }

    public void testUpdateBranchModelConfigurationOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-model-configuration-error.json")).setResponseCode(400));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchConfiguration branchConfiguration = BranchConfiguration.create(testKeyword, false);
            final List<Type> types = Lists.newArrayList(Type.create(Type.TypeId.BUGFIX, testKeyword, testKeyword, true));

            final CreateBranchModelConfiguration bcm = CreateBranchModelConfiguration.create(branchConfiguration, null, types);

            final BranchModelConfiguration configuration = baseApi.branchApi().updateModelConfiguration(projectKey, repoKey, bcm);
            assertThat(configuration).isNotNull();
            assertThat(configuration.errors()).isNotEmpty();
            assertThat(configuration.production()).isNull();
            assertThat(configuration.development()).isNull();
            assertSent(server, "PUT", localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + branchModelPath);
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteBranchModelConfiguration() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final RequestStatus success = baseApi.branchApi().deleteModelConfiguration(projectKey, repoKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, localDeleteMethod, localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + branchModelPath);
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteBranchModelConfigurationOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final RequestStatus success = baseApi.branchApi().deleteModelConfiguration(projectKey, repoKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, localDeleteMethod, localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + branchModelPath);
        } finally {
            server.shutdown();
        }
    }

    public void testGetBranchInfo() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-list.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchPage branchPage = baseApi.branchApi().info(projectKey, repoKey, commitId);
            assertThat(branchPage).isNotNull();
            assertThat(branchPage.errors().isEmpty()).isTrue();
            assertThat(branchPage.size() > 0).isTrue();
            assertSent(server, localGetMethod, localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + localInfoPath + "/" + commitId);
        } finally {
            server.shutdown();
        }
    }

    public void testGetBranchInfoOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/branch-list-error.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final BranchPage branchPage = baseApi.branchApi().info(projectKey, repoKey, commitId);
            assertThat(branchPage).isNotNull();
            assertThat(branchPage.errors()).isNotEmpty();
            assertSent(server, localGetMethod, localRestPath + BitbucketApiMetadata.API_VERSION
                    + localProjectsPath + projectKey + localReposPath + repoKey + localInfoPath + "/" + commitId);
        } finally {
            server.shutdown();
        }
    }
}
