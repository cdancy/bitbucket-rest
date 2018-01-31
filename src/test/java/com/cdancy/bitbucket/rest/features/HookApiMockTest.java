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
import com.cdancy.bitbucket.rest.domain.repository.Hook;
import com.cdancy.bitbucket.rest.domain.repository.HookPage;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.repository.HookSettings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mock tests for the {@link HookApi} class.
 */
@Test(groups = "unit", testName = "HookApiMockTest")
public class HookApiMockTest extends BaseBitbucketMockTest {

    private final String projectKey = "PRJ";
    private final String repoKey = "my-repo";
    private final String getMethod = "GET";
    private final String deleteMethod = "DELETE";
    private final String putMethod = "PUT";

    private final String restApiPath = "/rest/api/";    
    private final String projectsPath = "/projects/";
    private final String settingsPath = "/settings/";
    private final String hooksPath = settingsPath + "hooks";
    private final String reposPath = "/repos/";
    private final String enabledEndpoint = "/enabled";
    private final String settingsEndpoint = "/settings";

    private final String qwertyKeyword = "qwerty";
    private final String limitKeyword = "limit";
    private final String startKeyword = "start";

    final String testKey = "string-value";
    final String testValue = "this is an arbitrary string";

    private final String hookErrorJsonFile = "/repository-hook-error.json";

    public void testListHook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-hooks.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final HookPage hookPage = api.listHooks(projectKey, repoKey, 0, 100);
            assertThat(hookPage).isNotNull();
            assertThat(hookPage.values()).isNotEmpty();
            assertThat(hookPage.errors()).isEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListHookOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final HookPage hookPage = api.listHooks(projectKey, repoKey, 0, 100);
            assertThat(hookPage).isNotNull();
            assertThat(hookPage.values()).isEmpty();
            assertThat(hookPage.errors()).isNotEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 100, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetHook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-hook.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String hookKey = qwertyKeyword;
            final Hook hookPage = api.getHook(projectKey, repoKey, hookKey);
            assertThat(hookPage).isNotNull();
            assertThat(hookPage.enabled()).isFalse();
            assertThat(hookPage.errors()).isEmpty();

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetHookOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(hookErrorJsonFile)).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String hookKey = qwertyKeyword;
            final Hook hookPage = api.getHook(projectKey, repoKey, hookKey);
            assertThat(hookPage).isNotNull();
            assertThat(hookPage.enabled()).isFalse();
            assertThat(hookPage.errors()).isNotEmpty();

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testEnableHook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-hook.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String hookKey = qwertyKeyword;
            final Hook hookPage = api.enableHook(projectKey, repoKey, hookKey);
            assertThat(hookPage).isNotNull();
            assertThat(hookPage.enabled()).isFalse();
            assertThat(hookPage.errors()).isEmpty();

            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey + enabledEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testEnableHookOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(hookErrorJsonFile)).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String hookKey = qwertyKeyword;
            final Hook hookPage = api.enableHook(projectKey, repoKey, hookKey);
            assertThat(hookPage).isNotNull();
            assertThat(hookPage.enabled()).isFalse();
            assertThat(hookPage.errors()).isNotEmpty();

            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey + enabledEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDisableHook() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-hook.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String hookKey = qwertyKeyword;
            final Hook hookPage = api.disableHook(projectKey, repoKey, hookKey);
            assertThat(hookPage).isNotNull();
            assertThat(hookPage.enabled()).isFalse();
            assertThat(hookPage.errors()).isEmpty();

            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey + enabledEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDisableHookOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(hookErrorJsonFile)).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String hookKey = qwertyKeyword;
            final Hook hookPage = api.disableHook(projectKey, repoKey, hookKey);
            assertThat(hookPage).isNotNull();
            assertThat(hookPage.enabled()).isFalse();
            assertThat(hookPage.errors()).isNotEmpty();

            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey + enabledEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetHookSettings() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/hook-settings.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String hookKey = qwertyKeyword;
            final HookSettings hookSettings = api.getHookSettings(projectKey, repoKey, hookKey);
            assertThat(hookSettings).isNotNull();
            assertThat(hookSettings.errors()).isEmpty();
            final String possibleValue = hookSettings
                    .settings()
                    .getAsJsonObject()
                    .get(testKey)
                    .getAsString();
            assertThat(possibleValue).isEqualTo(testValue);

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey + settingsEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetHookSettingsNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String hookKey = qwertyKeyword;
            final HookSettings hookSettings = api.getHookSettings(projectKey, repoKey, hookKey);
            assertThat(hookSettings).isNotNull();
            assertThat(hookSettings.settings().getAsJsonObject().entrySet()).isEmpty();
            assertThat(hookSettings.errors()).isEmpty();

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey + settingsEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetHookSettiingsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource(hookErrorJsonFile)).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final String hookKey = qwertyKeyword;
            final HookSettings hookSettings = api.getHookSettings(projectKey, repoKey, hookKey);
            assertThat(hookSettings).isNotNull();
            assertThat(hookSettings.errors()).isNotEmpty();

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey + settingsEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateHookSettings() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/hook-settings.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final LinkedTreeMap settings = new LinkedTreeMap();
            settings.put(testKey, testValue);
            final HookSettings updateHook = HookSettings.of(settings);
            
            final String hookKey = qwertyKeyword;
            final HookSettings hookSettings = api.updateHookSettings(projectKey, repoKey, hookKey, updateHook);
            assertThat(hookSettings).isNotNull();
            assertThat(hookSettings.settings().getAsJsonObject().entrySet()).isNotEmpty();
            assertThat(hookSettings.errors()).isEmpty();
            final String possibleValue = hookSettings
                    .settings()
                    .getAsJsonObject()
                    .get(testKey)
                    .getAsString();
            assertThat(possibleValue).isEqualTo(testValue);

            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey + settingsEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateHookSettingsNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final RepositoryApi api = baseApi.repositoryApi();
        try {

            final LinkedTreeMap settings = new LinkedTreeMap();
            settings.put(testKey, testValue);
            final HookSettings updateHook = HookSettings.of(settings);

            final String hookKey = TestUtilities.randomStringLettersOnly();
            final HookSettings hookSettings = api.updateHookSettings(projectKey,
                    repoKey,
                    hookKey,
                    updateHook);
            assertThat(hookSettings).isNotNull();
            assertThat(hookSettings.settings().getAsJsonObject().entrySet()).isEmpty();
            assertThat(hookSettings.errors()).isEmpty();

            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + hooksPath + "/" + hookKey + settingsEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
