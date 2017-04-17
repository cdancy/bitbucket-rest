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

import com.cdancy.bitbucket.rest.domain.activities.ActivitiesPage;
import com.cdancy.bitbucket.rest.domain.participants.Participants;
import com.cdancy.bitbucket.rest.domain.participants.ParticipantsPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.domain.pullrequest.MergeStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.MinimalRepository;
import com.cdancy.bitbucket.rest.domain.pullrequest.ProjectKey;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequestPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.Reference;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.options.CreateParticipants;
import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreatePullRequest;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.util.concurrent.atomic.AtomicInteger;

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

            Reference fromRef = Reference.create("refs/heads/feature-ABC-123", repository1, "feature-ABC-123");
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
    
    public void testMergePullRequestNeedsRetry() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/merge-failed-retry.json")).setResponseCode(409));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            
            AtomicInteger retries = new AtomicInteger(5);
            
            PullRequest pr = api.merge(project, repo, 101, 1);
            while (retries.get() > 0 && pr.errors().size() > 0 && pr.errors().get(0).message().contains("Please retry the merge")) {
                
                System.out.println("Bitbucket is under load. Waiting for some time period and then retrying");
                Thread.sleep(500);
                retries.decrementAndGet();
                
                if (retries.get() == 0) {
                    server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-merge.json")).setResponseCode(200));
                } else {
                    server.enqueue(new MockResponse().setBody(payloadFromResource("/merge-failed-retry.json")).setResponseCode(409));
                }

                pr = api.merge(project, repo, 101, 1);
            } 

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

    public void testGetPullRequestChangesOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json"))
                .setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {

            ChangePage pcr = api.changes(project, repo, 101, true, 12, null);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isNotEmpty();

            Map<String, ?> queryParams = ImmutableMap.of("withComments", true, "limit", 12);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/changes", queryParams);
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

    public void testGetPullRequestCommitsOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json"))
                .setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {

            CommitPage pcr = api.commits(project, repo, 101, true, 1, null);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isNotEmpty();

            Map<String, ?> queryParams = ImmutableMap.of("withCounts", true, "limit", 1);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/commits", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestActivities() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-activities.json"))
                .setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {

            ActivitiesPage activities = api.listActivities(project, repo, 1, 5, 0);
            assertThat(activities).isNotNull();
            assertThat(activities.errors()).isEmpty();
            assertThat(activities.values()).hasSize(2);
            assertThat(activities.values().get(1).id() == 29733L).isTrue();

            Map<String, ?> queryParams = ImmutableMap.of("start", "0", "limit", 5);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/1/activities", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestActivitiesOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-activities-error.json"))
                .setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {

            ActivitiesPage activities = api.listActivities(project, repo, 1, 5, 0);
            assertThat(activities).isNotNull();
            assertThat(activities.errors()).isNotEmpty();

            Map<String, ?> queryParams = ImmutableMap.of("start", "0", "limit", 5);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/1/activities", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestParticipants() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-participants.json"))
                .setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {

            ParticipantsPage participants = api.listParticipants(project, repo, 1, 5, 0);
            assertThat(participants).isNotNull();
            assertThat(participants.errors()).isEmpty();
            assertThat(participants.values()).hasSize(1);
            assertThat(participants.values().get(0).approved()).isFalse();

            Map<String, ?> queryParams = ImmutableMap.of("start", "0", "limit", 5);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/1/participants", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestParticipantsOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-participants-error.json"))
                .setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {

            ParticipantsPage participants = api.listParticipants(project, repo, 1, 5, 0);
            assertThat(participants).isNotNull();
            assertThat(participants.errors()).isNotEmpty();

            Map<String, ?> queryParams = ImmutableMap.of("start", "0", "limit", 5);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/1/participants", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testPullRequestAssignPaticipants() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/participants.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            Long pullRequestId = 839L;
            User user = User.create("bob", "bob@acme.ic", 123, "bob", true, "bob", "asd");
            CreateParticipants participants = CreateParticipants.create(user, null, Participants.Role.REVIEWER,
                    false, Participants.Status.UNAPPROVED);
            Participants success = api.assignParticipant(projectKey, repoKey, pullRequestId, participants);
            assertThat(success).isNotNull();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/pull-requests/"
                    + pullRequestId + "/participants");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testPullRequestAssignPaticipantsOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/participants-error.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            Long pullRequestId = 839L;
            User user = User.create("bob", "bob@acme.ic", 123, "bob", true, "bob", "asd");
            CreateParticipants participants = CreateParticipants.create(user, null, Participants.Role.REVIEWER,
                    false, Participants.Status.UNAPPROVED);
            Participants success = api.assignParticipant(projectKey, repoKey, pullRequestId, participants);
            assertThat(success).isNotNull();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/pull-requests/"
                    + pullRequestId + "/participants");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePullRequestPaticipants() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            Long pullRequestId = 839L;
            String userSlug = "bbdfgf";
            boolean success = api.deleteParticipant(projectKey, repoKey, pullRequestId, userSlug);
            assertThat(success).isTrue();
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/pull-requests/"
                    + pullRequestId + "/participants/" + userSlug);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePullRequestPaticipantsOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        PullRequestApi api = baseApi.pullRequestApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            Long pullRequestId = 839L;
            String userSlug = "bbdfgf";
            boolean success = api.deleteParticipant(projectKey, repoKey, pullRequestId, userSlug);
            assertThat(success).isFalse();
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/pull-requests/"
                    + pullRequestId + "/participants/" + userSlug);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
