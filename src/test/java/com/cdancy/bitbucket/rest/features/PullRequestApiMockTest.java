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

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.domain.pullrequest.CommentPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.MergeStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.MinimalRepository;
import com.cdancy.bitbucket.rest.domain.pullrequest.ProjectKey;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequestPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.Reference;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreatePullRequest;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link PullRequestApi} class.
 */
@Test(groups = "unit", testName = "PullRequestApiMockTest")
public class PullRequestApiMockTest extends BaseBitbucketMockTest {

    final String project = "PRJ";
    final String repo = "my-repo";

    public void testCreatePullRequest() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request.json")).setResponseCode(201));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {

            ProjectKey proj1 = ProjectKey.create("PRJ");
            ProjectKey proj2 = ProjectKey.create("PRJ");
            MinimalRepository repository1 = MinimalRepository.create("my-repo", null, proj1);
            MinimalRepository repository2 = MinimalRepository.create("my-repo", null, proj2);

            Reference fromRef = Reference.create("refs/heads/feature-ABC-123", repository1);
            Reference toRef = Reference.create("refs/heads/master", repository2);
            CreatePullRequest cpr = CreatePullRequest.create("Talking Nerdy", "Some description", fromRef, toRef, null, null);
            PullRequest pr = api.create(repository2.project().key(), repository2.slug(), cpr);

            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase("PRJ");
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase("my-repo");
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.links()).isNotNull();
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequest() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            PullRequest pr = api.get(project, repo, 101);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase(project);
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase(repo);
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.links()).isNotNull();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPullRequest() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-page.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            PullRequestPage pr = api.list(project, repo, null, null, null, null, null, null, null, 10);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.values()).isNotEmpty();
            Map<String, ?> queryParams = ImmutableMap.of("limit", 10);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPullRequestNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-page-error.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            PullRequestPage pr = api.list(project, repo, null, null, null, null, null, null, null, 10);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isNotEmpty();
            Map<String, ?> queryParams = ImmutableMap.of("limit", 10);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeclinePullRequest() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-decline.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            PullRequest pr = api.decline(project, repo, 101, 1);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase("PRJ");
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase("my-repo");
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.state()).isEqualToIgnoringCase("DECLINED");
            assertThat(pr.open()).isFalse();
            assertThat(pr.links()).isNotNull();

            Map<String, ?> queryParams = ImmutableMap.of("version", 1);
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/decline", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testReopenPullRequest() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            PullRequest pr = api.reopen(project, repo, 101, 1);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase("PRJ");
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase("my-repo");
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.state()).isEqualToIgnoringCase("OPEN");
            assertThat(pr.open()).isTrue();
            assertThat(pr.links()).isNotNull();

            Map<String, ?> queryParams = ImmutableMap.of("version", 1);
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/reopen", queryParams);

        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCanMergePullRequest() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-can-merge-succeed.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            MergeStatus ms = api.canMerge(project, repo, 101);
            assertThat(ms).isNotNull();
            assertThat(ms.errors()).isEmpty();
            assertThat(ms.canMerge()).isTrue();
            assertThat(ms.vetoes()).isEmpty();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/merge");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCanMergePullRequestFail() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-can-merge-fail.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            MergeStatus ms = api.canMerge(project, repo, 101);
            assertThat(ms).isNotNull();
            assertThat(ms.errors()).isEmpty();
            assertThat(ms.canMerge()).isFalse();
            assertThat(ms.vetoes()).hasSize(1);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/merge");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testMergePullRequest() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-merge.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            PullRequest pr = api.merge(project, repo, 101, 1);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase("PRJ");
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase("my-repo");
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.state()).isEqualToIgnoringCase("MERGED");
            assertThat(pr.open()).isFalse();
            assertThat(pr.links()).isNotNull();

            Map<String, ?> queryParams = ImmutableMap.of("version", 1);
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/merge", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-not-exist.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            PullRequest pr = api.get(project, repo, 101);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isNotEmpty();
            assertThat(pr.errors().get(0).exceptionName()).endsWith("NoSuchPullRequestException");
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101");
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
        PullRequestApi api = baseApi.pullRequestApi();
        try {

            ChangePage pcr = api.changes(project, repo, 101, true, 12, null);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isEmpty();
            assertThat(pcr.values()).hasSize(1);

            Map<String, ?> queryParams = ImmutableMap.of("withComments", true, "limit", 12);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/changes", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestComments() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-comments.json"))
                .setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {

            CommentPage pcr = api.comments(project, repo, 101, "hej");
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isEmpty();
            assertThat(pcr.values()).hasSize(2);

            Comments firstComment = pcr.values().get(0);
            assertThat(firstComment.text()).isEqualTo("comment in diff");
            assertThat(firstComment.id()).isEqualTo(4);
            assertThat(firstComment.comments()).hasSize(1);
            assertThat(firstComment.comments().get(0).text()).isEqualTo("reply to comment in diff");
            assertThat(firstComment.comments().get(0).id()).isEqualTo(5);

            Comments secondComment = pcr.values().get(1);
            assertThat(secondComment.text()).isEqualTo("another commet in diff");
            assertThat(secondComment.id()).isEqualTo(6);
            assertThat(secondComment.comments()).isEmpty();

            Map<String, ?> queryParams = ImmutableMap.of("path", "hej");
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestCommits() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-commits.json"))
                .setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {

            CommitPage pcr = api.commits(project, repo, 101, true, 1, null);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isEmpty();
            assertThat(pcr.values()).hasSize(1);
            assertThat(pcr.totalCount()).isEqualTo(1);

            Map<String, ?> queryParams = ImmutableMap.of("withCounts", true, "limit", 1);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/commits", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
