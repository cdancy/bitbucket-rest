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
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "FileApiMockTest")
public class FileApiMockTest extends BaseBitbucketMockTest {

    public void testGetRawContent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        final String content = "Hello, World!";
        server.enqueue(new MockResponse().setBody(content).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        FileApi api = baseApi.fileApi();
        try {
            
            final String projectKey = "PRJ";
            final String repoKey = "myrepo";
            final String filePath = "some/random/path/MyFile.txt";
            final String rawContent = api.rawContent(projectKey, repoKey, filePath, null);
            assertThat(rawContent).isNotNull();
            assertThat(rawContent).isEqualTo(content);
            assertSentAcceptText(server, "GET", "/projects/" + projectKey + "/repos/" + repoKey + "/raw/" + filePath);

        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetRawContentOnNotFound() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        FileApi api = baseApi.fileApi();
        try {
            
            final String projectKey = "PRJ";
            final String repoKey = "myrepo";
            final String filePath = "some/random/path/MyFile.txt";
            final String rawContent = api.rawContent(projectKey, repoKey, filePath, null);
            assertThat(rawContent).isNull();
            assertSentAcceptText(server, "GET", "/projects/" + projectKey + "/repos/" + repoKey + "/raw/" + filePath);

        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
