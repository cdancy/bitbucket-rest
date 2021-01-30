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

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.commit.Commit;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Mock tests for the {@link CommitsApi} class.
 */
@Test(groups = "unit", testName = "CommitApiMockTest")
public class CommitsApiMockTest extends BaseBitbucketMockTest {

    private final String projectKey = "PRJ";
    private final String repoKey = "myrepo";
    private final String commitHash = "abcdef0123abcdef4567abcdef8987abcdef6543";

    private final String getMethod = "GET";
    private final String restApiPath = "/rest/api/";
    private final String limitKeyword = "limit";

    public void testGetCommit() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final Commit commit = baseApi.commitsApi().get(projectKey, repoKey, commitHash, null);
            assertThat(commit).isNotNull();
            assertThat(commit.errors().isEmpty()).isTrue();
            assertThat(commit.id().equalsIgnoreCase(commitHash)).isTrue();
            assertThat(commit.authorTimestamp()).isNotNull().isNotEqualTo(0);
            assertThat(commit.author()).isNotNull();
            assertThat(commit.committerTimestamp()).isNotNull().isNotEqualTo(0);
            assertThat(commit.committer()).isNotNull();

            assertSent(server, getMethod, restBasePath + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/commits/" + commitHash);
        } finally {
            server.shutdown();
        }
    }

    public void testGetCommitNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final Commit commit = baseApi.commitsApi().get(projectKey, repoKey, commitHash, null);
            assertThat(commit).isNotNull();
            assertThat(commit.errors().size() > 0).isTrue();

            assertSent(server, getMethod, restBasePath + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/commits/" + commitHash);
        } finally {
            server.shutdown();
        }
    }

    public void testGetPullRequestChanges() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/pull-request-changes.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final ChangePage changePage = baseApi.commitsApi().listChanges(projectKey, repoKey, commitHash, 12, null);
            assertThat(changePage).isNotNull();
            assertThat(changePage.errors()).isEmpty();
            assertThat(changePage.values()).hasSize(1);

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 12);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/commits/abcdef0123abcdef4567abcdef8987abcdef6543/changes", queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testGetPullRequestChangesOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/commit-error.json"))
                .setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final ChangePage changePage = baseApi.commitsApi().listChanges(projectKey, repoKey, commitHash, 1, 12);
            assertThat(changePage).isNotNull();
            assertThat(changePage.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 1, "start", 12);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/commits/abcdef0123abcdef4567abcdef8987abcdef6543/changes", queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testListCommits() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-commits.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final CommitPage pcr = baseApi.commitsApi().list(projectKey, repoKey, true, null, null, null, null, null, null, 1, null);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isEmpty();
            assertThat(pcr.values()).hasSize(1);
            assertThat(pcr.totalCount()).isEqualTo(1);

            final Map<String, ?> queryParams = ImmutableMap.of("withCounts", true, limitKeyword, 1);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/commits", queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testListCommitsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final CommitPage pcr = baseApi.commitsApi().list(projectKey, repoKey, true, null, null, null, null, null, null, 1, null);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("withCounts", true, limitKeyword, 1);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/commits", queryParams);
        } finally {
            server.shutdown();
        }
    }
}
