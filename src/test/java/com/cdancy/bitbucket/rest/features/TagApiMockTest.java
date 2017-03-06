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
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateTag;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

/**
 * Mock tests for the {@link TagApi} class.
 */
@Test(groups = "unit", testName = "TagApiMockTest")
public class TagApiMockTest extends BaseBitbucketMockTest {

    public void testCreateTag() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/tag.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        TagApi api = baseApi.tagApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            String tagName = "release-2.0.0";
            String commitHash = "8d351a10fb428c0c1239530256e21cf24f136e73";

            CreateTag createTag = CreateTag.create(tagName, commitHash, null);
            Tag tag = api.create(projectKey, repoKey, createTag);
            assertThat(tag).isNotNull();
            assertThat(tag.errors().isEmpty()).isTrue();
            assertThat(tag.id().endsWith(tagName)).isTrue();
            assertThat(commitHash.equalsIgnoreCase(tag.latestCommit())).isTrue();
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/tags");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetTag() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/tag.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        TagApi api = baseApi.tagApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            String tagName = "release-2.0.0";

            Tag tag = api.get(projectKey, repoKey, tagName);
            assertThat(tag).isNotNull();
            assertThat(tag.errors().isEmpty()).isTrue();
            assertThat(tag.id().endsWith(tagName)).isTrue();
            
            String commitHash = "8d351a10fb428c0c1239530256e21cf24f136e73";
            assertThat(commitHash.equalsIgnoreCase(tag.latestCommit())).isTrue();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/tags/" + tagName);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetTagNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        TagApi api = baseApi.tagApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            String tagName = "non-existent-tag";

            Tag tag = api.get(projectKey, repoKey, tagName);
            assertThat(tag).isNull();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/tags/" + tagName);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
