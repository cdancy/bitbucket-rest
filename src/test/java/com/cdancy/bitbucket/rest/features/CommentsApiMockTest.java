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
import com.cdancy.bitbucket.rest.domain.comment.Anchor;
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.comment.Parent;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.CommentPage;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateComment;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mock tests for the {@link CommentsApi} class.
 */
@Test(groups = "unit", testName = "CommentsApiMockTest")
public class CommentsApiMockTest extends BaseBitbucketMockTest {

    private final String projectKey = "PRJ";
    private final String repoKey = "my-repo";
    private final String restApiPath = "/rest/api/";
    private final String getMethod = "GET";
    private final String hejKeyword = "hej";
    private final String measuredReplyKeyword = "A measured reply.";

    public void testComment() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/comments.json")).setResponseCode(201));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final Comments pr = baseApi.commentsApi().comment(projectKey, repoKey, 101, measuredReplyKeyword);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.text()).isEqualTo(measuredReplyKeyword);
            assertThat(pr.links()).isNull();
            assertSent(server, "POST", restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments");
        } finally {
            server.shutdown();
        }
    }

    public void testCreateComment() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/comments.json")).setResponseCode(201));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final Parent parent = Parent.create(1);
            final Anchor anchor = Anchor.create(1, Anchor.LineType.CONTEXT, Anchor.FileType.FROM, "path/to/file", "path/to/file");
            final CreateComment createComment = CreateComment.create(measuredReplyKeyword, parent, anchor, null, null);
            final Comments pr = baseApi.commentsApi().create(projectKey, repoKey, 101, createComment);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.text()).isEqualTo(measuredReplyKeyword);
            assertThat(pr.links()).isNull();
            assertSent(server, "POST", restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments");
        } finally {
            server.shutdown();
        }
    }

    public void testGetComment() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/comments.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final Comments pr = baseApi.commentsApi().get(projectKey, repoKey, 101, 1);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.text()).isEqualTo(measuredReplyKeyword);
            assertThat(pr.links()).isNull();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments/1");
        } finally {
            server.shutdown();
        }
    }

    public void testGetCommentOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final Comments pr = baseApi.commentsApi().get(projectKey, repoKey, 101, 1);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isNotEmpty();

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments/1");
        } finally {
            server.shutdown();
        }
    }

    public void testGetPullRequestComments() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-comments.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final CommentPage pcr = baseApi.commentsApi().fileComments("project", "repo", 101, hejKeyword, null, null,
                    null, null, 0, 100);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isEmpty();
            assertThat(pcr.values()).hasSize(2);

            final Comments firstComment = pcr.values().get(0);
            assertThat(firstComment.anchor().path()).isEqualTo(hejKeyword);
            assertThat(firstComment.text()).isEqualTo("comment in diff");
            assertThat(firstComment.id()).isEqualTo(4);
            assertThat(firstComment.comments()).hasSize(1);
            assertThat(firstComment.comments().get(0).text()).isEqualTo("reply to comment in diff");
            assertThat(firstComment.comments().get(0).id()).isEqualTo(5);
            final JsonElement jsonElement = firstComment.properties().get("repositoryId");
            assertThat(jsonElement).isNotNull();
            assertThat(jsonElement.getAsInt()).isEqualTo(1);

            final Comments secondComment = pcr.values().get(1);
            assertThat(secondComment.anchor().path()).isEqualTo(hejKeyword);
            assertThat(secondComment.text()).isEqualTo("another commet in diff");
            assertThat(secondComment.id()).isEqualTo(6);
            assertThat(secondComment.comments()).isEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("path", hejKeyword, "start", "0", "limit", "100");
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                        + "/projects/project/repos/repo/pull-requests/101/comments", queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testGetPullRequestCommentsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final CommentPage pcr = baseApi.commentsApi().fileComments("project", "repo", 101, hejKeyword, null, null,
                    null, null, 0, 100);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("path", hejKeyword, "start", "0", "limit", "100");
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/project/repos/repo/pull-requests/101/comments", queryParams);
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteComment() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final RequestStatus success = baseApi.commentsApi().delete(projectKey, repoKey, 101, 1, 1);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of("version", 1);
            assertSent(server, "DELETE", restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments/1", queryParams);
        } finally {
            server.shutdown();
        }
    }
}
