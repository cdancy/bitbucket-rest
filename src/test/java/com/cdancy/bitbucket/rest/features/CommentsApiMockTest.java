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
import com.cdancy.bitbucket.rest.domain.pullrequest.CommentPage;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
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

    public void testComment() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/comments.json"))
                .setResponseCode(201));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommentsApi api = baseApi.commentsApi();
        try {

            Comments pr = api.comment("PRJ", "my-repo", 101, "A measured reply.");
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.text()).isEqualTo("A measured reply.");
            assertThat(pr.links()).isNull();
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateComment() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/comments.json"))
                .setResponseCode(201));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommentsApi api = baseApi.commentsApi();
        try {

            Parent parent = Parent.create(1);
            Anchor anchor = Anchor.create(1, Anchor.LineType.CONTEXT, Anchor.FileType.FROM, "path/to/file", "path/to/file");
            CreateComment createComment = CreateComment.create("A measured reply.", parent, anchor);
            Comments pr = api.create("PRJ", "my-repo", 101, createComment);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.text()).isEqualTo("A measured reply.");
            assertThat(pr.links()).isNull();
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetComment() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/comments.json"))
                .setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommentsApi api = baseApi.commentsApi();
        try {
            Comments pr = api.get("PRJ", "my-repo", 101, 1);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.text()).isEqualTo("A measured reply.");
            assertThat(pr.links()).isNull();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments/1");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetCommentOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json"))
                .setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommentsApi api = baseApi.commentsApi();
        try {
            Comments pr = api.get("PRJ", "my-repo", 101, 1);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isNotEmpty();

            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments/1");
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
        CommentsApi api = baseApi.commentsApi();
        try {

            CommentPage pcr = api.fileComments("project", "repo", 101, "hej", 0, 100);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isEmpty();
            assertThat(pcr.values()).hasSize(2);

            Comments firstComment = pcr.values().get(0);
            assertThat(firstComment.anchor().path()).isEqualTo("hej");
            assertThat(firstComment.text()).isEqualTo("comment in diff");
            assertThat(firstComment.id()).isEqualTo(4);
            assertThat(firstComment.comments()).hasSize(1);
            assertThat(firstComment.comments().get(0).text()).isEqualTo("reply to comment in diff");
            assertThat(firstComment.comments().get(0).id()).isEqualTo(5);
            final JsonElement jsonElement = firstComment.properties().get("repositoryId");
            assertThat(jsonElement).isNotNull();
            assertThat(jsonElement.getAsInt()).isEqualTo(1);

            Comments secondComment = pcr.values().get(1);
            assertThat(secondComment.anchor().path()).isEqualTo("hej");
            assertThat(secondComment.text()).isEqualTo("another commet in diff");
            assertThat(secondComment.id()).isEqualTo(6);
            assertThat(secondComment.comments()).isEmpty();

            Map<String, ?> queryParams = ImmutableMap.of("path", "hej", "start", "0", "limit", "100");
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                        + "/projects/project/repos/repo/pull-requests/101/comments", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestCommentsOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json"))
                .setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommentsApi api = baseApi.commentsApi();
        try {

            CommentPage pcr = api.fileComments("project", "repo", 101, "hej", 0, 100);
            assertThat(pcr).isNotNull();
            assertThat(pcr.errors()).isNotEmpty();

            Map<String, ?> queryParams = ImmutableMap.of("path", "hej", "start", "0", "limit", "100");
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/project/repos/repo/pull-requests/101/comments", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteComment() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommentsApi api = baseApi.commentsApi();
        try {
            boolean pr = api.delete("PRJ", "my-repo", 101, 1, 1);
            assertThat(pr).isTrue();

            Map<String, ?> queryParams = ImmutableMap.of("version", 1);
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments/1", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
