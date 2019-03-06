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
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.options.CreateWebHook;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import com.cdancy.bitbucket.rest.domain.repository.WebHook;
import com.cdancy.bitbucket.rest.domain.repository.WebHook.EventType;
import com.cdancy.bitbucket.rest.domain.repository.WebHookConfiguration;
import com.cdancy.bitbucket.rest.domain.repository.WebHookPage;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mock tests for the {@link WebHookApi} class.
 */
@Test(groups = "unit", testName = "WebHookApiMockTest")
public class WebHookApiMockTest extends BaseBitbucketMockTest {

    private final String projectKey = "PRJ";
    private final String repoKey = "my-repo";
    private final String postMethod = "POST";
    private final String getMethod = "GET";
    private final String deleteMethod = "DELETE";
    private final String putMethod = "PUT";

    private final String reposPath = "/repos/";
    private final String restApiPath = "/rest/api/";
    private final String projectsPath = "/projects/";
    private final String webHooksEndpoint = "/webhooks";

    private final String webHooksApiPath = restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + webHooksEndpoint;

    private final String webHookName = "Webhook Name";
    private final String webHookKey = "10";
    private final String webHookUrl = "http://example.com";
    private final WebHookConfiguration webHookConfiguration = WebHookConfiguration.create("password");

    private final String webHookErrorJsonFile = "/repository-webhook-errors.json";
    private final String webHookJsonFile = "/repository-webhook.json";
    private final String webHookPageJsonFile = "/repository-webhook-page.json";
    private final String limitKeyword = "limit";
    private final String startKeyword = "start";



    public void testCreateWebHook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(webHookJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final WebHookApi api = baseApi.webHookApi();
        final List<WebHook.EventType> events = new ArrayList<WebHook.EventType>();
        events.add(EventType.REPO_CHANGED);
        events.add(EventType.REPO_MODIFIED);
        try {
            final CreateWebHook createWebHook = CreateWebHook.create(
                    webHookName, events, webHookUrl,true, webHookConfiguration);
            final WebHook webHook = api.create(projectKey, repoKey, createWebHook);
            assertThat(webHook).isNotNull();
            assertThat(webHook.errors()).isEmpty();
            assertThat(webHook.events().equals(events)).isTrue();
            assertSent(server, postMethod, webHooksApiPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }

    }

    public void testCreateWebHookOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(webHookErrorJsonFile)).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final WebHookApi api = baseApi.webHookApi();
        final List<WebHook.EventType> events = new ArrayList<WebHook.EventType>();
        try {
            final CreateWebHook createWebHook = CreateWebHook.create(
                    webHookName, events, webHookUrl,true, webHookConfiguration);
            final WebHook webHook = api.create(projectKey, repoKey, createWebHook);
            assertThat(webHook).isNotNull();
            assertThat(webHook.errors()).isNotEmpty();
            assertThat(webHook.configuration()).isNull();
            assertSent(server, "POST", webHooksApiPath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetWebHook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(webHookJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final WebHookApi api = baseApi.webHookApi();
        try {
            final WebHook webHook = api.get(projectKey, repoKey, webHookKey);

            assertThat(webHook).isNotNull();
            assertThat(webHook.errors()).isEmpty();
            assertSent(server, getMethod, webHooksApiPath + "/" + webHookKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetWebHookOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(webHookErrorJsonFile)).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final WebHookApi api = baseApi.webHookApi();
        try {
            final WebHook webHook = api.get(projectKey, repoKey, webHookKey);
            assertThat(webHook).isNotNull();
            assertThat(webHook.errors()).isNotEmpty();
            assertThat(webHook.active()).isFalse();
            assertSent(server, getMethod, webHooksApiPath + "/" + webHookKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListWebHooks() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(webHookPageJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final WebHookApi api = baseApi.webHookApi();

        try {
            final WebHookPage webHookPage = api.list(projectKey, repoKey, 0, 100);
            assertThat(webHookPage).isNotNull();
            assertThat(webHookPage.values()).isNotEmpty();
            assertThat(webHookPage.errors()).isEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, webHooksApiPath, queryParams);
            assertThat(webHookPage.values().size() > 0).isEqualTo(true);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListWebHooksOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(webHookErrorJsonFile)).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final WebHookApi api = baseApi.webHookApi();

        try {
            final WebHookPage webHookPage = api.list(projectKey, repoKey, 0, 100);
            assertThat(webHookPage).isNotNull();
            assertThat(webHookPage.values()).isEmpty();
            assertThat(webHookPage.errors()).isNotEmpty();
            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, webHooksApiPath, queryParams);
            assertThat(webHookPage.values().size() > 0).isEqualTo(false);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateWebHook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(webHookJsonFile)).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final WebHookApi api = baseApi.webHookApi();
        final List<WebHook.EventType> events = new ArrayList<WebHook.EventType>();

        try {
            events.add(EventType.REPO_CHANGED);
            events.add(EventType.REPO_MODIFIED);
            final CreateWebHook createWebHook = CreateWebHook.create(
                    webHookName, events, webHookUrl,true, webHookConfiguration);
            final WebHook webHook = api.update(projectKey, repoKey, webHookKey, createWebHook);
            assertThat(webHook.events()).isEqualTo(events);
            assertThat(webHook).isNotNull();
            assertThat(webHook.errors()).isEmpty();
            assertSent(server, putMethod, webHooksApiPath + "/" + webHookKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateWebHookOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(webHookErrorJsonFile)).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final WebHookApi api = baseApi.webHookApi();
        final List<WebHook.EventType> events = new ArrayList<WebHook.EventType>();

        try {
            final CreateWebHook createWebHook = CreateWebHook.create(
                    webHookName, events, webHookUrl,true, webHookConfiguration);
            final WebHook webHook = api.update(projectKey, repoKey, webHookKey, createWebHook);
            assertThat(webHook).isNotNull();
            assertThat(webHook.errors()).isNotEmpty();
            assertSent(server, putMethod, webHooksApiPath + "/" + webHookKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }

    }

    public void testDeleteWebHook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final WebHookApi api = baseApi.webHookApi();
        try {
            final RequestStatus ref = api.delete(projectKey, repoKey, webHookKey);
            assertThat(ref).isNotNull();
            assertThat(ref.errors()).isEmpty();
            assertSent(server, deleteMethod, webHooksApiPath + "/" + webHookKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteWebHookOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final WebHookApi api = baseApi.webHookApi();
        try {
            final RequestStatus ref = api.delete(projectKey, repoKey, webHookKey);
            assertThat(ref).isNotNull();
            assertThat(ref.errors()).isNotEmpty();
            assertSent(server, deleteMethod, webHooksApiPath + "/" + webHookKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
