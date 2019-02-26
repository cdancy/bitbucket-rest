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
import com.cdancy.bitbucket.rest.domain.common.Reference;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.options.CreateParticipants;
import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.activities.Activities;
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.comment.Task;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
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

    private static final String BOB_EMAIL_ADDRESS = "bob@acme.ic";
    private static final String USER_TYPE = "asd";
    private static final String PARTICIPANTS_PATH = "/participants/";

    private final String projectKey = "PRJ";
    private final String repoKey = "my-repo";
    private final String restApiPath = "/rest/api/";
    private final String getMethod = "GET";
    private final String postMethod = "POST";
    private final String deleteMethod = "DELETE";

    private final String limitKeyword = "limit";
    private final String versionKeyword = "version";
    private final String startKeyword = "start";
    private final String projectsPath = "/projects/";
    private final String pullRequestsPath = "/pull-requests/";
    private final String specificPullRequestPath = "/projects/PRJ/repos/my-repo/pull-requests/101";
    private final String specificPullRequestMergePath = specificPullRequestPath + "/merge";
    private final String bobKeyword = "bob";
    private final String reposPath = "/repos/";
    private final String pullRequestFile = "/pull-request.json";

    public void testCreatePullRequest() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(pullRequestFile)).setResponseCode(201));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final ProjectKey proj1 = ProjectKey.create(projectKey);
            final ProjectKey proj2 = ProjectKey.create(projectKey);
            final MinimalRepository repository1 = MinimalRepository.create(repoKey, null, proj1);
            final MinimalRepository repository2 = MinimalRepository.create(repoKey, null, proj2);

            final String commitId = "930228bb501e07c2653771858320873d94518e33";
            final Reference fromRef = Reference.create("refs/heads/feature-ABC-123", repository1, null, null, "feature-ABC-123", commitId);
            final Reference toRef = Reference.create("refs/heads/master", repository2);
            final CreatePullRequest cpr = CreatePullRequest.create("Talking Nerdy", "Some description", fromRef, toRef, null, null);
            final PullRequest pr = api.create(repository2.project().key(), repository2.slug(), cpr);

            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase(projectKey);
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase(repoKey);
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.links()).isNotNull();
            assertThat(pr.fromRef().latestCommit().equals(commitId));
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequest() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(pullRequestFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final PullRequest pr = api.get(projectKey, repoKey, 101);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase(projectKey);
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase(repoKey);
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.links()).isNotNull();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + specificPullRequestPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPullRequest() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-page.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final PullRequestPage pr = api.list(projectKey, repoKey, null, null, null, null, null, null, null, 10);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.values()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 10);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListPullRequestNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-page-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final PullRequestPage pr = api.list(projectKey, repoKey, null, null, null, null, null, null, null, 10);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 10);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeclinePullRequest() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-decline.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final PullRequest pr = api.decline(projectKey, repoKey, 101, 1);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase(projectKey);
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase(repoKey);
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.state()).isEqualToIgnoringCase("DECLINED");
            assertThat(pr.open()).isFalse();
            assertThat(pr.links()).isNotNull();

            final Map<String, ?> queryParams = ImmutableMap.of(versionKeyword, 1);
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/decline", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testReopenPullRequest() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(pullRequestFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final PullRequest pr = api.reopen(projectKey, repoKey, 101, 1);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase(projectKey);
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase(repoKey);
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.state()).isEqualToIgnoringCase("OPEN");
            assertThat(pr.open()).isTrue();
            assertThat(pr.links()).isNotNull();

            final Map<String, ?> queryParams = ImmutableMap.of(versionKeyword, 1);
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/reopen", queryParams);

        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCanMergePullRequest() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-can-merge-succeed.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final MergeStatus ms = api.canMerge(projectKey, repoKey, 101);
            assertThat(ms).isNotNull();
            assertThat(ms.errors()).isEmpty();
            assertThat(ms.canMerge()).isTrue();
            assertThat(ms.vetoes()).isEmpty();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + specificPullRequestMergePath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCanMergePullRequestFail() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-can-merge-fail.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final MergeStatus ms = api.canMerge(projectKey, repoKey, 101);
            assertThat(ms).isNotNull();
            assertThat(ms.errors()).isEmpty();
            assertThat(ms.canMerge()).isFalse();
            assertThat(ms.vetoes()).hasSize(1);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + specificPullRequestMergePath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testMergePullRequest() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-merge.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final PullRequest pr = api.merge(projectKey, repoKey, 101, 1);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase(projectKey);
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase(repoKey);
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.state()).isEqualToIgnoringCase("MERGED");
            assertThat(pr.open()).isFalse();
            assertThat(pr.links()).isNotNull();

            final Map<String, ?> queryParams = ImmutableMap.of(versionKeyword, 1);
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + specificPullRequestMergePath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testMergePullRequestNeedsRetry() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/merge-failed-retry.json")).setResponseCode(409));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final AtomicInteger retries = new AtomicInteger(5);

            PullRequest pr = api.merge(projectKey, repoKey, 101, 1);
            while (retries.get() > 0 && pr.errors().size() > 0 && pr.errors().get(0).message().contains("Please retry the merge")) {

                System.out.println("Bitbucket is under load. Waiting for some time period and then retrying");
                Thread.sleep(500);
                retries.decrementAndGet();

                if (retries.get() == 0) {
                    server.enqueue(new MockResponse() //NOPMD
                            .setBody(payloadFromResource("/pull-request-merge.json"))
                            .setResponseCode(200));
                } else {
                    server.enqueue(new MockResponse() //NOPMD
                            .setBody(payloadFromResource("/merge-failed-retry.json"))
                            .setResponseCode(409));
                }

                pr = api.merge(projectKey, repoKey, 101, 1);
            }

            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.fromRef().repository().project().key()).isEqualToIgnoringCase(projectKey);
            assertThat(pr.fromRef().repository().slug()).isEqualToIgnoringCase(repoKey);
            assertThat(pr.id()).isEqualTo(101);
            assertThat(pr.state()).isEqualToIgnoringCase("MERGED");
            assertThat(pr.open()).isFalse();
            assertThat(pr.links()).isNotNull();

            final Map<String, ?> queryParams = ImmutableMap.of(versionKeyword, 1);
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + specificPullRequestMergePath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-not-exist.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final PullRequest pr = api.get(projectKey, repoKey, 101);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isNotEmpty();
            assertThat(pr.errors().get(0).exceptionName()).endsWith("NoSuchPullRequestException");
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + specificPullRequestPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestChanges() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-changes.json"))
                .setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final ChangePage pcr = api.changes(projectKey, repoKey, 101, true, 12, null);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isEmpty();
            assertThat(pcr.values()).hasSize(1);

            final Map<String, ?> queryParams = ImmutableMap.of("withComments", true, limitKeyword, 12);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/changes", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestChangesOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json"))
                .setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final ChangePage pcr = api.changes(projectKey, repoKey, 101, true, 12, null);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("withComments", true, limitKeyword, 12);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/changes", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestCommits() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-commits.json"))
                .setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final CommitPage pcr = api.commits(projectKey, repoKey, 101, true, 1, null);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isEmpty();
            assertThat(pcr.values()).hasSize(1);
            assertThat(pcr.totalCount()).isEqualTo(1);

            final Map<String, ?> queryParams = ImmutableMap.of("withCounts", true, limitKeyword, 1);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/commits", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestCommitsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json"))
                .setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final CommitPage pcr = api.commits(projectKey, repoKey, 101, true, 1, null);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("withCounts", true, limitKeyword, 1);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/commits", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestActivities() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-activities.json"))
                .setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final ActivitiesPage activities = api.listActivities(projectKey, repoKey, 1, 5, 0);
            assertThat(activities).isNotNull();
            assertThat(activities.errors()).isEmpty();
            assertThat(activities.values()).hasSize(3);
            assertThat(activities.values().get(1).id() == 29733L).isTrue();
            Activities foundActivities = null;
            for (final Activities act : activities.values()) {
                if (act.commentAction() != null && act.commentAction().equals("ADDED") && act.comment() != null) {
                    foundActivities = act;
                }
            }
            assertThat(foundActivities).isNotNull();
            final Comments comments = foundActivities.comment();
            assertThat(comments.permittedOperations()).isNotNull();
            assertThat(comments.permittedOperations().deletable()).isTrue();
            assertThat(comments.permittedOperations().transitionable()).isFalse();
            assertThat(comments.tasks().size()).isEqualTo(1);
            final Task task = comments.tasks().get(0);
            assertThat(task.anchor().type()).isEqualTo("COMMENT");
            assertThat(task.state()).isEqualTo("OPEN");
            assertThat(task.anchor().properties().keySet().contains("likedBy"));
            assertThat(task.anchor().properties().keySet().contains("repositoryId"));

            final Map<String, ?> queryParams = ImmutableMap.of(startKeyword, "0", limitKeyword, 5);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/1/activities", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestActivitiesOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-activities-error.json"))
                .setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final ActivitiesPage activities = api.listActivities(projectKey, repoKey, 1, 5, 0);
            assertThat(activities).isNotNull();
            assertThat(activities.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(startKeyword, "0", limitKeyword, 5);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/1/activities", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestParticipants() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-participants.json"))
                .setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final ParticipantsPage participants = api.listParticipants(projectKey, repoKey, 1, 5, 0);
            assertThat(participants).isNotNull();
            assertThat(participants.errors()).isEmpty();
            assertThat(participants.values()).hasSize(1);
            assertThat(participants.values().get(0).approved()).isFalse();

            final Map<String, ?> queryParams = ImmutableMap.of(startKeyword, "0", limitKeyword, 5);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/1/participants", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestParticipantsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-participants-error.json"))
                .setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final ParticipantsPage participants = api.listParticipants(projectKey, repoKey, 1, 5, 0);
            assertThat(participants).isNotNull();
            assertThat(participants.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(startKeyword, "0", limitKeyword, 5);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/1/participants", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testPullRequestAssignPaticipants() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/participants.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final Long pullRequestId = 839L;
            final User user = User.create(bobKeyword, BOB_EMAIL_ADDRESS, 123, bobKeyword, true, bobKeyword, USER_TYPE);
            final CreateParticipants participants = CreateParticipants.create(user, null, Participants.Role.REVIEWER,
                    false, Participants.Status.UNAPPROVED);
            final Participants success = api.assignParticipant(projectKey, repoKey, pullRequestId, participants);
            assertThat(success).isNotNull();
            assertThat(success.errors()).isEmpty();
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath
                    + pullRequestId + "/participants");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testPullRequestAssignPaticipantsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/participants-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final Long pullRequestId = 839L;
            final User user = User.create(bobKeyword, BOB_EMAIL_ADDRESS, 123, bobKeyword, true, bobKeyword, USER_TYPE);
            final CreateParticipants participants = CreateParticipants.create(user, null, Participants.Role.REVIEWER,
                    false, Participants.Status.UNAPPROVED);
            final Participants success = api.assignParticipant(projectKey, repoKey, pullRequestId, participants);
            assertThat(success).isNotNull();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath
                    + pullRequestId + "/participants");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePullRequestPaticipants() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final Long pullRequestId = 839L;
            final String userSlug = "bbdfgf";
            final RequestStatus success = api.deleteParticipant(projectKey, repoKey, pullRequestId, userSlug);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "DELETE", restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath
                    + pullRequestId + PARTICIPANTS_PATH + userSlug);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePullRequestPaticipantsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {

            final Long pullRequestId = 839L;
            final String userSlug = "bbdfgf";
            final RequestStatus success = api.deleteParticipant(projectKey, repoKey, pullRequestId, userSlug);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, "DELETE", restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath
                    + pullRequestId + PARTICIPANTS_PATH + userSlug);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testAddPullRequestParticipants() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/participants.json")).setResponseCode(201));

        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final Long pullRequestId = 839L;
            final User user = User.create(bobKeyword, BOB_EMAIL_ADDRESS, 123, bobKeyword, true, bobKeyword, USER_TYPE);
            final CreateParticipants participants = CreateParticipants.create(user, null, Participants.Role.REVIEWER,
                    false, Participants.Status.APPROVED);
            final Participants resultParticipants = api.addParticipant(projectKey, repoKey, pullRequestId, bobKeyword, participants);
            assertThat(resultParticipants).isNotNull();
            assertThat(resultParticipants.errors()).isEmpty();
            assertSent(server, "PUT", restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath
                    + pullRequestId + PARTICIPANTS_PATH + bobKeyword);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testAddPullRequestParticipantsOnError() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setResponseCode(401));

        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final Long pullRequestId = 839L;
            final User user = User.create(bobKeyword, BOB_EMAIL_ADDRESS, 123, bobKeyword, true, bobKeyword, USER_TYPE);
            final CreateParticipants participants = CreateParticipants.create(user, null, Participants.Role.REVIEWER,
                    false, Participants.Status.APPROVED);
            final Participants resultParticipants = api.addParticipant(projectKey, repoKey, pullRequestId, bobKeyword, participants);
            assertThat(resultParticipants).isNotNull();
            assertThat(resultParticipants.errors()).isNotEmpty();
            assertSent(server, "PUT", restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath
                    + pullRequestId + PARTICIPANTS_PATH + bobKeyword);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteMergedPullRequest() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-delete-merged.json")).setResponseCode(409));

        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final Long pullRequestId = 839L;
            final Long prVersion = 1L;
            final RequestStatus success = api.delete(projectKey, repoKey, pullRequestId, prVersion);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath
                    + pullRequestId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteBadVersionOfPullRequest() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-delete-invalid-version.json")).setResponseCode(409));

        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final Long pullRequestId = 839L;
            final Long badPrVersion = 1L;
            final RequestStatus success = api.delete(projectKey, repoKey, pullRequestId, badPrVersion);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath
                    + pullRequestId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteNonExistentPullRequest() throws Exception {
        final MockWebServer server = mockWebServer();
        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-delete-non-existent.json")).setResponseCode(404));

        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final Long pullRequestId = 999L;
            final Long prVersion = 1L;
            final RequestStatus success = api.delete(projectKey, repoKey, pullRequestId, prVersion);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath
                    + pullRequestId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePullRequest() throws Exception {
        final MockWebServer server = mockWebServer();
        //server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-delete.json")).setResponseCode(204));
        server.enqueue(new MockResponse().setResponseCode(204));

        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PullRequestApi api = baseApi.pullRequestApi();
        try {
            final Long pullRequestId = 999L;
            final Long prVersion = 1L;
            final RequestStatus success = api.delete(projectKey, repoKey, pullRequestId, prVersion);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + pullRequestsPath
                    + pullRequestId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

}
