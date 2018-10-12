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

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.tags.Tag;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.tags.TagPage;
import com.cdancy.bitbucket.rest.options.CreateTag;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.util.Map;
import org.testng.annotations.Test;

/**
 * Mock tests for the {@link TagApi} class.
 */
@Test(groups = "unit", testName = "TagApiMockTest")
public class TagApiMockTest extends BaseBitbucketMockTest {

    private final String getMethod = "GET";
    private final String deleteMethod = "DELETE";
    private final String restApiPath = "/rest/api/";
    private final String restGitPath = "/rest/git/";
    private final String reposPath = "/repos/";
    private final String tagsPath = "/tags/";
    private final String projectsPath = "/projects/";
    private final String limitKeyword = "limit";
    private final String startKeyword = "start";

    private final String projectKey = "PRJ";
    private final String repoKey = "myrepo";
            
    public void testCreateTag() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/tag.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TagApi api = baseApi.tagApi();
        try {
            final String tagName = "release-2.0.0";
            final String commitHash = "8d351a10fb428c0c1239530256e21cf24f136e73";

            final CreateTag createTag = CreateTag.create(tagName, commitHash, null);
            final Tag tag = api.create(projectKey, repoKey, createTag);
            assertThat(tag).isNotNull();
            assertThat(tag.errors().isEmpty()).isTrue();
            assertThat(tag.id().endsWith(tagName)).isTrue();
            assertThat(commitHash.equalsIgnoreCase(tag.latestCommit())).isTrue();
            assertSent(server, "POST", restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + "/tags");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetTag() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/tag.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TagApi api = baseApi.tagApi();
        try {
            final String tagName = "release-2.0.0";

            final Tag tag = api.get(projectKey, repoKey, tagName);
            assertThat(tag).isNotNull();
            assertThat(tag.errors().isEmpty()).isTrue();
            assertThat(tag.id().endsWith(tagName)).isTrue();
            
            final String commitHash = "8d351a10fb428c0c1239530256e21cf24f136e73";
            assertThat(commitHash.equalsIgnoreCase(tag.latestCommit())).isTrue();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + tagsPath + tagName);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetTagNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TagApi api = baseApi.tagApi();
        try {
            final String tagName = "non-existent-tag";

            final Tag tag = api.get(projectKey, repoKey, tagName);
            assertThat(tag).isNotNull();
            assertThat(tag.errors()).isNotEmpty();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + tagsPath + tagName);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListTags() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/tag-page.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TagApi api = baseApi.tagApi();
        try {
            final TagPage tagPage = api.list(projectKey, repoKey, null, null, 0, 10);
            assertThat(tagPage).isNotNull();
            assertThat(tagPage.errors()).isEmpty();
            assertThat(tagPage.values()).isNotEmpty();
            assertThat(tagPage.limit()).isEqualTo(10);

            assertThat(tagPage.values().get(0).type()).isEqualTo("TAG");
            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 10, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + "/tags", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListTagsOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-page-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TagApi api = baseApi.tagApi();
        try {
            final TagPage tagPage = api.list(projectKey, repoKey, null, null, 0, 10);
            assertThat(tagPage).isNotNull();
            assertThat(tagPage.errors()).isNotEmpty();
            assertThat(tagPage.limit()).isEqualTo(-1);

            final Map<String, ?> queryParams = ImmutableMap.of(limitKeyword, 10, startKeyword, 0);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + "/tags", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteTag() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TagApi api = baseApi.tagApi();
        try {
            final String tagName = "release-2.0.0";

            final RequestStatus tag = api.delete(projectKey, repoKey, tagName);
            assertThat(tag).isNotNull();
            assertThat(tag.errors().isEmpty()).isTrue();
            assertThat(tag.value()).isTrue();
            
            assertSent(server, deleteMethod, restGitPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + tagsPath + tagName);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteTagNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TagApi api = baseApi.tagApi();
        try {
            final String tagName = "non-existent-tag";

            final RequestStatus tag = api.delete(projectKey, repoKey, tagName);
            assertThat(tag).isNotNull();
            assertThat(tag.errors()).isNotEmpty();
            assertThat(tag.value()).isFalse();
            assertSent(server, deleteMethod, restGitPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + tagsPath + tagName);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
