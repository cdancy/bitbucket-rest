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

import com.cdancy.bitbucket.rest.options.CreateAccessKey;
import com.cdancy.bitbucket.rest.options.CreateKey;
import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.sshkey.AccessKey.PermissionType;
import com.cdancy.bitbucket.rest.domain.sshkey.AccessKeyPage;
import com.cdancy.bitbucket.rest.domain.sshkey.AccessKey;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mock tests for the {@link KeysApi} class.
 */
@Test(groups = "unit", testName = "KeysApiMockTest")
public class KeysApiMockTest extends BaseBitbucketMockTest {

    private final String projectKey = "MY-PROJECT";
    private final String repoKey = "my-repo";
    private final int keyId = 1;
    private final String getMethod = "GET";
    private final String deleteMethod = "DELETE";

    private final String restApiPath = "/rest/keys/";
    private final String projectsPath = "/projects/";
    private final String reposPath = "/repos/";
    private final String sshEndpoint = "/ssh";

    private final String limitKeyword = "limit";
    private final String startKeyword = "start";
    private final String keyLabel = "abc";

    public void testListKeysByRepository() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-list-by-repository.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKeyPage accessKeyPage = api.listByRepo(projectKey, repoKey, 0, 25);
            assertThat(accessKeyPage).isNotNull();
            assertThat(accessKeyPage.errors()).isEmpty();

            assertThat(accessKeyPage.size() == 1).isTrue();
            assertThat(accessKeyPage.values().get(0).key().label().equals(keyLabel)).isTrue();
            assertThat(accessKeyPage.values().get(0).repository().name().equals(repoKey)).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 25, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + sshEndpoint, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListEmptyKeysByRepository() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-list-empty.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKeyPage accessKeyPage = api.listByRepo(projectKey, repoKey, 0, 25);
            assertThat(accessKeyPage).isNotNull();
            assertThat(accessKeyPage.values()).isEmpty();
            assertThat(accessKeyPage.errors()).isEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 25, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + sshEndpoint, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateForRepo() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-get-for-repository.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKey accessKey = api.createForProject(projectKey, CreateAccessKey.create(CreateKey.create("ssh-rsa abc"),
                    PermissionType.REPO_READ));
            assertThat(accessKey).isNotNull();
            assertThat(accessKey.errors()).isEmpty();
            assertThat(accessKey.key().label().equals(keyLabel)).isTrue();
            assertThat(accessKey.repository().name().equals(repoKey)).isTrue();

            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + sshEndpoint, "{\"key\": {\"text\": \"ssh-rsa abc\"},\"permission\": \"REPO_READ\"}");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateForRepoWrongKey() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-create-wrong-key.json")).setResponseCode(400));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKey accessKey = api.createForProject(projectKey, CreateAccessKey.create(CreateKey.create("ssh-rsa WRONG"),
                    PermissionType.REPO_READ));
            assertThat(accessKey).isNotNull();
            assertThat(accessKey.errors()).isNotEmpty();

            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + sshEndpoint,
                    "{\"key\": {\"text\": \"ssh-rsa WRONG\"},\"permission\": \"REPO_READ\"}");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetForRepo() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-get-for-repository.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKey accessKey = api.getForRepo(projectKey, repoKey, keyId);
            assertThat(accessKey).isNotNull();
            assertThat(accessKey.errors()).isEmpty();

            assertThat(accessKey.key().label().equals(keyLabel)).isTrue();
            assertThat(accessKey.repository().name().equals(repoKey)).isTrue();

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + sshEndpoint + "/" + keyId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetForRepoByWrongId() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-get-wrong-id.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKey accessKey = api.getForRepo(projectKey, repoKey, keyId);

            assertThat(accessKey).isNotNull();
            assertThat(accessKey.errors()).isNotEmpty();

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + sshEndpoint + "/" + keyId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteFromRepo() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final RequestStatus success = api.deleteFromRepo(projectKey, repoKey, keyId);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();

            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + sshEndpoint + "/" + keyId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteFromRepoByWrongId() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final long nonExistentKey = 2L;

            final RequestStatus success = api.deleteFromRepo(projectKey, repoKey, nonExistentKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();

            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + sshEndpoint + "/" + nonExistentKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListKeysByProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-list-by-project.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKeyPage accessKeyPage = api.listByProject(projectKey, 0, 25);
            assertThat(accessKeyPage).isNotNull();
            assertThat(accessKeyPage.errors()).isEmpty();

            assertThat(accessKeyPage.size() == 1).isTrue();
            assertThat(accessKeyPage.values().get(0).key().label().equals(keyLabel)).isTrue();
            assertThat(accessKeyPage.values().get(0).project().name().equals(projectKey)).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 25, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + sshEndpoint, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListEmptyKeysByProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-list-empty.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKeyPage accessKeyPage = api.listByProject(projectKey, 0, 25);
            assertThat(accessKeyPage).isNotNull();
            assertThat(accessKeyPage.values()).isEmpty();
            assertThat(accessKeyPage.errors()).isEmpty();

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 25, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + sshEndpoint, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateForProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-get-for-project.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKey accessKey = api.createForProject(projectKey, CreateAccessKey.create(CreateKey.create("ssh-rsa abc"),
                    PermissionType.PROJECT_READ));
            assertThat(accessKey).isNotNull();
            assertThat(accessKey.errors()).isEmpty();
            assertThat(accessKey.key().label().equals(keyLabel)).isTrue();
            assertThat(accessKey.project().name().equals(projectKey)).isTrue();

            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + sshEndpoint, "{\"key\": {\"text\": \"ssh-rsa abc\"},\"permission\": \"PROJECT_READ\"}");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateForProjectWrongKey() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-create-wrong-key.json")).setResponseCode(400));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKey accessKey = api.createForProject(projectKey, CreateAccessKey.create(CreateKey.create("ssh-rsa WRONG"),
                    PermissionType.PROJECT_READ));
            assertThat(accessKey).isNotNull();
            assertThat(accessKey.errors()).isNotEmpty();

            assertSent(server, postMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + sshEndpoint, "{\"key\": {\"text\": \"ssh-rsa WRONG\"},\"permission\": \"PROJECT_READ\"}");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetForProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-get-for-project.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKey accessKey = api.getForProject(projectKey, keyId);
            assertThat(accessKey).isNotNull();

            assertThat(accessKey.key().label().equals(keyLabel)).isTrue();
            assertThat(accessKey.project().name().equals(projectKey)).isTrue();
            assertThat(accessKey.errors()).isEmpty();

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + sshEndpoint + "/" + keyId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetForProjectByWrongId() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/accesskeys-get-wrong-id.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final AccessKey accessKey = api.getForProject(projectKey, keyId);
            assertThat(accessKey).isNotNull();
            assertThat(accessKey.errors()).isNotEmpty();

            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + sshEndpoint + "/" + keyId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteFromProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final RequestStatus success = api.deleteFromProject(projectKey, keyId);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();

            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + sshEndpoint + "/" + keyId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteFromProjectByWrongId() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final KeysApi api = baseApi.keysApi();
        try {
            final long nonExistentKey = 2L;
            final RequestStatus success = api.deleteFromProject(projectKey, nonExistentKey);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();

            assertSent(server, deleteMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + sshEndpoint + "/" + nonExistentKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

}
