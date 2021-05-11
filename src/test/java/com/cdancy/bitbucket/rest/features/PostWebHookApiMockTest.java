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

import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.postwebhooks.PostWebHook;
import com.cdancy.bitbucket.rest.domain.postwebhooks.PostWebHooks;
import com.cdancy.bitbucket.rest.options.CreatePostWebHook;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * Mock tests for the {@link PostWebHookApi} class.
 */
@Test(groups = "unit", testName = "PostWebHookApiMockTest")
public class PostWebHookApiMockTest extends BaseBitbucketMockTest {

    private final String projectKey = "PRJ";
    private final String repoKey = "my-repo";
    private final String postMethod = "POST";
    private final String getMethod = "GET";
    private final String deleteMethod = "DELETE";
    private final String putMethod = "PUT";

    private final String reposPath = "/repos/";
    private final String restApiPath = "/rest";
    private final String projectsPath = "/projects/";
    private final String webHooksEndpoint = "/webhook/";
    private final String configurations = "configurations";
    private final String postWebHookId = "1107";

    private final String postWebHookListJsonFile = "/postwebhooks-list.json";
    private final String postWebHookUpdateJsonFile = "/postwebhook-update.json";
    private final String postWebHookCreateJsonFile = "/postwebhook-create.json";
    private final String postWebHookErrorJsonFile = "/postwebhook-errors.json";
    private final String releaseBranch = "release/1.0";
    private final String userId = "userid";
    private final String newTitle = "new updated title";
    private final String newUrl = "new url";


    private final String postWebhookApiPath = restApiPath + webHooksEndpoint + BitbucketApiMetadata.API_VERSION
            + projectsPath + projectKey + reposPath + repoKey + webHooksEndpoint + configurations;

    public void testGetPostWebHooks() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(postWebHookListJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PostWebHookApi api = baseApi.postWebHookApi();
        try {
            final PostWebHooks postWebhookList = api.list(projectKey, repoKey);

            assertEquals(postWebhookList.postWebHooks().get(0).branchCreated(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).branchDeleted(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).enabled(), true);
            assertEquals(postWebhookList.postWebHooks().get(0).prCommented(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).prCreated(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).prDeclined(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).prMerged(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).prReopened(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).prRescoped(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).prUpdated(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).repoMirrorSynced(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).repoPush(), false);
            assertEquals(postWebhookList.postWebHooks().get(0).tagCreated(), true);
            assertEquals(postWebhookList.postWebHooks().get(0).title(), "test");
            assertEquals(postWebhookList.postWebHooks().get(0).url(), "test");

            assertEquals(postWebhookList.postWebHooks().get(1).branchCreated(), true);
            assertEquals(postWebhookList.postWebHooks().get(1).branchDeleted(), true);
            assertEquals(postWebhookList.postWebHooks().get(1).enabled(), false);
            assertEquals(postWebhookList.postWebHooks().get(1).prCommented(), false);
            assertEquals(postWebhookList.postWebHooks().get(1).prCreated(), true);
            assertEquals(postWebhookList.postWebHooks().get(1).prDeclined(), true);
            assertEquals(postWebhookList.postWebHooks().get(1).prMerged(), true);
            assertEquals(postWebhookList.postWebHooks().get(1).prReopened(), true);
            assertEquals(postWebhookList.postWebHooks().get(1).prRescoped(), true);
            assertEquals(postWebhookList.postWebHooks().get(1).prUpdated(), true);
            assertEquals(postWebhookList.postWebHooks().get(1).repoMirrorSynced(), false);
            assertEquals(postWebhookList.postWebHooks().get(1).repoPush(), true);
            assertEquals(postWebhookList.postWebHooks().get(1).tagCreated(), true);
            assertEquals(postWebhookList.postWebHooks().get(1).title(), "http://jenkins.example.com");
            assertEquals(postWebhookList.postWebHooks().get(1).url(), "http://jenkins.example.com/bitbucket-scmsource-hook/notify");

            assertThat(postWebhookList).isNotNull();
            assertSent(server, getMethod, postWebhookApiPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdatePostWebhook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(postWebHookUpdateJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PostWebHookApi api = baseApi.postWebHookApi();
        try {
            final CreatePostWebHook createPostWebHook = CreatePostWebHook.create(
                    true, true, releaseBranch, userId,
                    true, true, true, true, true, true,
                    true, true, true, true, true, newTitle, newUrl);
            final PostWebHook postWebHook = api.update(projectKey, repoKey, postWebHookId, createPostWebHook);
            assertThat(postWebHook).isNotNull();
            assertEquals(postWebHook.branchCreated(), true);
            assertEquals(postWebHook.branchDeleted(), true);
            assertEquals(postWebHook.enabled(), true);
            assertEquals(postWebHook.prCommented(), true);
            assertEquals(postWebHook.prCreated(), true);
            assertEquals(postWebHook.prDeclined(), true);
            assertEquals(postWebHook.prMerged(), true);
            assertEquals(postWebHook.prReopened(), true);
            assertEquals(postWebHook.prRescoped(), true);
            assertEquals(postWebHook.prUpdated(), true);
            assertEquals(postWebHook.repoMirrorSynced(), true);
            assertEquals(postWebHook.repoPush(), true);
            assertEquals(postWebHook.tagCreated(), true);
            assertEquals(postWebHook.title(), newTitle);
            assertEquals(postWebHook.url(), newUrl);
            assertEquals(postWebHook.branchesToIgnore(), releaseBranch);

            assertSent(server, putMethod, postWebhookApiPath + "/" + postWebHookId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdatePostWebhookError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(postWebHookErrorJsonFile)).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PostWebHookApi api = baseApi.postWebHookApi();
        try {
            final CreatePostWebHook createPostWebHook = CreatePostWebHook.create(
                    true, true, releaseBranch, userId,
                    true, true, true, true, true, true,
                    true, true, true, true, true, newTitle, newUrl);
            final PostWebHook postWebHook = api.update(projectKey, repoKey, postWebHookId, createPostWebHook);
            assertThat(postWebHook).isNotNull();
            assertThat(postWebHook.errors()).isNotEmpty();
            assertSent(server, putMethod, postWebhookApiPath + "/" + postWebHookId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreatePostWebhook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(postWebHookCreateJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PostWebHookApi api = baseApi.postWebHookApi();
        try {
            final CreatePostWebHook createPostWebHook = CreatePostWebHook.create(
                    true, true, releaseBranch, userId,
                    true, true, true, true, true, true,
                    true, true, true, true, true, newTitle, newUrl);
            final PostWebHook postWebHook = api.create(projectKey, repoKey, createPostWebHook);
            assertThat(postWebHook).isNotNull();
            assertEquals(postWebHook.branchCreated(), true);
            assertEquals(postWebHook.branchDeleted(), true);
            assertEquals(postWebHook.enabled(), true);
            assertEquals(postWebHook.prCommented(), true);
            assertEquals(postWebHook.prCreated(), true);
            assertEquals(postWebHook.prDeclined(), true);
            assertEquals(postWebHook.prMerged(), true);
            assertEquals(postWebHook.prReopened(), true);
            assertEquals(postWebHook.prRescoped(), true);
            assertEquals(postWebHook.prUpdated(), true);
            assertEquals(postWebHook.repoMirrorSynced(), true);
            assertEquals(postWebHook.repoPush(), true);
            assertEquals(postWebHook.tagCreated(), true);
            assertEquals(postWebHook.title(), newTitle);
            assertEquals(postWebHook.url(), newUrl);
            assertEquals(postWebHook.branchesToIgnore(), releaseBranch);

            assertSent(server, postMethod, postWebhookApiPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreatePostWebhookErrors() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(postWebHookErrorJsonFile)).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PostWebHookApi api = baseApi.postWebHookApi();
        try {
            final CreatePostWebHook createPostWebHook = CreatePostWebHook.create(
                    true, true, releaseBranch, userId,
                    true, true, true, true, true, true,
                    true, true, true, true, true, newTitle, newUrl);
            final PostWebHook postWebHook = api.create(projectKey, repoKey, createPostWebHook);
            assertThat(postWebHook).isNotNull();
            assertThat(postWebHook.errors()).isNotEmpty();
            assertSent(server, postMethod, postWebhookApiPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePostWebhook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PostWebHookApi api = baseApi.postWebHookApi();
        try {
            final RequestStatus ref = api.delete(projectKey, repoKey, postWebHookId);
            assertThat(ref).isNotNull();
            assertThat(ref.errors()).isEmpty();
            assertSent(server, deleteMethod, postWebhookApiPath + "/" + postWebHookId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeletePostWebhookErrors() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final PostWebHookApi api = baseApi.postWebHookApi();
        try {
            final RequestStatus ref = api.delete(projectKey, repoKey, postWebHookId);
            assertThat(ref).isNotNull();
            assertThat(ref.errors()).isNotEmpty();
            assertSent(server, deleteMethod, postWebhookApiPath + "/" + postWebHookId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
