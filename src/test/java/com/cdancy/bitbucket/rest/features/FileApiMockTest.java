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
import com.cdancy.bitbucket.rest.domain.file.LinePage;
import com.cdancy.bitbucket.rest.domain.file.RawContent;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.util.Map;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "FileApiMockTest")
public class FileApiMockTest extends BaseBitbucketMockTest {

    public void testGetContent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        final String content = "Hello, World!";
        server.enqueue(new MockResponse().setBody(content).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        FileApi api = baseApi.fileApi();
        try {
            
            final String projectKey = "PRJ";
            final String repoKey = "myrepo";
            final String filePath = "some/random/path/MyFile.txt";
            final RawContent rawContent = api.raw(projectKey, repoKey, filePath, null);
            assertThat(rawContent).isNotNull();
            assertThat(rawContent.errors().isEmpty()).isTrue();
            assertThat(rawContent.value()).isEqualTo(content);
            assertSentAcceptText(server, "GET", "/projects/" + projectKey + "/repos/" + repoKey + "/raw/" + filePath);

        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetContentOnNotFound() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody("<html>randomString</html>").setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        FileApi api = baseApi.fileApi();
        try {
            
            final String projectKey = "PRJ";
            final String repoKey = "myrepo";
            final String filePath = "some/random/path/MyFile.txt";
            final RawContent rawContent = api.raw(projectKey, repoKey, filePath, null);
            assertThat(rawContent).isNotNull();
            assertThat(rawContent.value()).isNull();
            assertThat(rawContent.errors().isEmpty()).isFalse();
            assertThat(rawContent.errors().get(0).message()).isEqualTo("Failed retrieving raw content");
            assertSentAcceptText(server, "GET", "/projects/" + projectKey + "/repos/" + repoKey + "/raw/" + filePath);

        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testListLines() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/line-page.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        FileApi api = baseApi.fileApi();
        try {
            
            final String projectKey = "PRJ";
            final String repoKey = "myrepo";
            final String filePath = "some/random/path/MyFile.txt";
            final LinePage linePage = api.listLines(projectKey, repoKey, filePath, null, null, null, null, null, null);
            assertThat(linePage).isNotNull();
            assertThat(linePage.errors().isEmpty()).isTrue();
            assertThat(linePage.values().isEmpty()).isFalse();
            assertThat(linePage.values().get(0).text()).isEqualTo("BEARS");
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/browse/" + filePath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testListLinesWithBlame() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/line-page-with-blame.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        FileApi api = baseApi.fileApi();
        try {
            
            final String projectKey = "PRJ";
            final String repoKey = "myrepo";
            final String filePath = "some/random/path/MyFile.txt";
            final LinePage linePage = api.listLines(projectKey, repoKey, filePath, null, null, true, null, null, null);
            assertThat(linePage).isNotNull();
            assertThat(linePage.blame().isEmpty()).isFalse();
            assertThat(linePage.errors().isEmpty()).isTrue();
            assertThat(linePage.values().isEmpty()).isFalse();
            assertThat(linePage.values().get(0).text()).isEqualTo("BEARS");
            
            Map<String, ?> queryParams = ImmutableMap.of("blame", "true");
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/browse/" + filePath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListLinesOnNotFound() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-page-error.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        FileApi api = baseApi.fileApi();
        try {
            
            final String projectKey = "PRJ";
            final String repoKey = "myrepo";
            final String filePath = "some/random/path/MyFile.txt";
            final LinePage linePage = api.listLines(projectKey, repoKey, filePath, null, null, null, null, null, null);
            assertThat(linePage).isNotNull();
            assertThat(linePage.errors().isEmpty()).isFalse();
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/browse/" + filePath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
