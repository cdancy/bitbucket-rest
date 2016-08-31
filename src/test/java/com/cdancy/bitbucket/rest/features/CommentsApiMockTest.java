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
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateComment;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

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
            assertNotNull(pr);
            assertTrue(pr.errors().size() == 0);
            assertTrue(pr.text().equals("A measured reply."));
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
            assertNotNull(pr);
            assertTrue(pr.errors().size() == 0);
            assertTrue(pr.text().equals("A measured reply."));
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
            assertNotNull(pr);
            assertTrue(pr.errors().size() == 0);
            assertTrue(pr.text().equals("A measured reply."));
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/my-repo/pull-requests/101/comments/1");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
