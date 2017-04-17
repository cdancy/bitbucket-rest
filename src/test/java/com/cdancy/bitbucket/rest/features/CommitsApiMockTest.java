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
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
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

    private String projectKey = "PRJ";
    private String repoKey = "myrepo";
    private String commitHash = "abcdef0123abcdef4567abcdef8987abcdef6543";

    public void testGetCommit() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommitsApi api = baseApi.commitsApi();
        try {

            Commit commit = api.get(projectKey, repoKey, commitHash, null);
            assertThat(commit).isNotNull();
            assertThat(commit.errors().isEmpty()).isTrue();
            assertThat(commit.id().equalsIgnoreCase(commitHash)).isTrue();

            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/commits/" + commitHash);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetCommitNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommitsApi api = baseApi.commitsApi();
        try {

            Commit commit = api.get(projectKey, repoKey, commitHash, null);
            assertThat(commit).isNotNull();
            assertThat(commit.errors().size() > 0).isTrue();

            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/commits/" + commitHash);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestChanges() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-changes.json"))
                .setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommitsApi api = baseApi.commitsApi();
        try {

            ChangePage changePage = api.listChanges(projectKey, repoKey, commitHash, null, 12);
            assertThat(changePage).isNotNull();
            assertThat(changePage.errors()).isEmpty();
            assertThat(changePage.values()).hasSize(1);

            Map<String, ?> queryParams = ImmutableMap.of("limit", 12);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/commits/abcdef0123abcdef4567abcdef8987abcdef6543/changes", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestChangesOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json"))
                .setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommitsApi api = baseApi.commitsApi();
        try {

            ChangePage changePage = api.listChanges(projectKey, repoKey, commitHash, 1, 12);
            assertThat(changePage).isNotNull();
            assertThat(changePage.errors()).isNotEmpty();

            Map<String, ?> queryParams = ImmutableMap.of("limit", 12, "start", 1);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/commits/abcdef0123abcdef4567abcdef8987abcdef6543/changes", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
