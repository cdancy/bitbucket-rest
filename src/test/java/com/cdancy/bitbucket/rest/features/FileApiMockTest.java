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
import com.cdancy.bitbucket.rest.domain.commit.Commit;
import com.cdancy.bitbucket.rest.domain.file.LastModifiedSummary;
import com.cdancy.bitbucket.rest.domain.file.LinePage;
import com.cdancy.bitbucket.rest.domain.file.RawContent;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.domain.file.FilesPage;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import java.util.Collections;
import java.util.Map;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "FileApiMockTest")
public class FileApiMockTest extends BaseBitbucketMockTest {

    private final String projectKey = "PRJ";
    private final String repoKey = "myrepo";
    private final String filePath = "some/random/path/MyFile.txt";
    private final String directoryPath = "some/random/path";
    private final String restApiPath = "/rest/api/";
    private final String branch = "myBranch";
    private final String content = "file contents";
    private final String getMethod = "GET";
    private final String putMethod = "PUT";
    private final String browsePath = "/projects/PRJ/repos/myrepo/browse/";
            
    public void testGetContent() throws Exception {
        final MockWebServer server = mockWebServer();

        final String content = "Hello, World!";
        server.enqueue(new MockResponse().setBody(content).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final FileApi api = baseApi.fileApi();
        try {

            final RawContent rawContent = api.raw(projectKey, repoKey, filePath, null);
            assertThat(rawContent).isNotNull();
            assertThat(rawContent.errors().isEmpty()).isTrue();
            assertThat(rawContent.value()).isEqualTo(content);
            assertSentAcceptText(server, getMethod, "/projects/" + projectKey + "/repos/" + repoKey + "/raw/" + filePath);

        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetContentOnNotFound() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody("<html>randomString</html>").setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final FileApi api = baseApi.fileApi();
        try {

            final RawContent rawContent = api.raw(projectKey, repoKey, filePath, null);
            assertThat(rawContent).isNotNull();
            assertThat(rawContent.value()).isNull();
            assertThat(rawContent.errors().isEmpty()).isFalse();
            assertThat(rawContent.errors().get(0).message()).isEqualTo("Failed retrieving raw content");
            assertSentAcceptText(server, getMethod, "/projects/" + projectKey + "/repos/" + repoKey + "/raw/" + filePath);

        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testListLines() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/line-page.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final FileApi api = baseApi.fileApi();
        try {

            final LinePage linePage = api.listLines(projectKey, repoKey, filePath, null, null, null, null, null, null);
            assertThat(linePage).isNotNull();
            assertThat(linePage.errors().isEmpty()).isTrue();
            assertThat(linePage.values().isEmpty()).isFalse();
            assertThat(linePage.values().get(0).text()).isEqualTo("BEARS");
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + browsePath + filePath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testListLinesWithBlame() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/line-page-with-blame.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final FileApi api = baseApi.fileApi();
        try {

            final LinePage linePage = api.listLines(projectKey, repoKey, filePath, null, null, true, null, null, null);
            assertThat(linePage).isNotNull();
            assertThat(linePage.blame().isEmpty()).isFalse();
            assertThat(linePage.errors().isEmpty()).isTrue();
            assertThat(linePage.values().isEmpty()).isFalse();
            assertThat(linePage.values().get(0).text()).isEqualTo("BEARS");
            
            final Map<String, ?> queryParams = ImmutableMap.of("blame", "true");
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + browsePath + filePath, queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListLinesOnNotFound() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-page-error.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final FileApi api = baseApi.fileApi();
        try {

            final LinePage linePage = api.listLines(projectKey, repoKey, filePath, null, null, null, null, null, null);
            assertThat(linePage).isNotNull();
            assertThat(linePage.errors().isEmpty()).isFalse();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + browsePath + filePath);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateContent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {
            final Commit commit = baseApi.fileApi().updateContent(projectKey, repoKey, filePath, branch, content, null, null, null);
            assertThat(commit).isNotNull();
            assertThat(commit.errors().isEmpty()).isTrue();
            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + browsePath + filePath);
        } finally {
            server.shutdown();
        }
    }

    public void testUpdateContentBadRequest() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(400));
        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {
            final Commit commit = baseApi.fileApi().updateContent(projectKey, repoKey, filePath, branch, content, null, null, null);
            assertThat(commit).isNotNull();
            assertThat(commit.errors().isEmpty()).isFalse();
            assertSent(server, putMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + browsePath + filePath);
        } finally {
            server.shutdown();
        }
    }

    public void testListFiles() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/files-page.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final FileApi api = baseApi.fileApi();
        try {

            final String gitRef = "some-existing-commit-or-tag-or-branch";
            final FilesPage ref = api.listFiles(projectKey, repoKey, gitRef, null, null);
            assertThat(ref).isNotNull();
            assertThat(ref.errors().isEmpty()).isTrue();
            assertThat(ref.values().isEmpty()).isFalse();
            assertThat(ref.values().get(0)).isEqualTo("path/to/file.txt");

            final Map<String, ?> queryParams = ImmutableMap.of("at", gitRef);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/files", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testListFilesOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(400));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final FileApi api = baseApi.fileApi();
        try {

            final String gitRef = "some-NON-existing-commit-or-tag-or-branch";
            final FilesPage ref = api.listFiles(projectKey, repoKey, gitRef, null, null);
            assertThat(ref).isNotNull();
            assertThat(ref.errors().isEmpty()).isFalse();
            assertThat(ref.values().isEmpty()).isTrue();

            final Map<String, ?> queryParams = ImmutableMap.of("at", gitRef);
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/PRJ/repos/myrepo/files", queryParams);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testLastModified() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/last-modified-summary.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {
            LastModifiedSummary summary = baseApi.fileApi().listLastModified(projectKey, repoKey, branch);
            assertThat(summary).isNotNull();
            assertThat(summary.latestCommit()).isNotNull();
            assertThat(summary.files().isEmpty()).isFalse();
            assertThat(summary.errors().isEmpty()).isTrue();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/last-modified", Collections.singletonMap("at", branch));
        } finally {
            server.shutdown();
        }
    }

    public void testLastModifiedAtPath() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/last-modified-summary.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {
            LastModifiedSummary summary = baseApi.fileApi().listLastModified(projectKey, repoKey, directoryPath, branch);
            assertThat(summary).isNotNull();
            assertThat(summary.latestCommit()).isNotNull();
            assertThat(summary.files().isEmpty()).isFalse();
            assertThat(summary.errors().isEmpty()).isTrue();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/last-modified/" + directoryPath, Collections.singletonMap("at", branch));
        } finally {
            server.shutdown();
        }
    }

    public void testLastModifiedBadRequest() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(400));
        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {
            LastModifiedSummary summary = baseApi.fileApi().listLastModified(projectKey, repoKey, branch);
            assertThat(summary).isNotNull();
            assertThat(summary.errors().isEmpty()).isFalse();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/last-modified", Collections.singletonMap("at", branch));
        } finally {
            server.shutdown();
        }
    }

    public void testLastModifiedAtPathBadRequest() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(400));
        try (final BitbucketApi baseApi = api(server.getUrl("/"));) {
            LastModifiedSummary summary = baseApi.fileApi().listLastModified(projectKey, repoKey, directoryPath, branch);
            assertThat(summary).isNotNull();
            assertThat(summary.errors().isEmpty()).isFalse();
            assertSent(server, getMethod, restApiPath + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/last-modified/" + directoryPath, Collections.singletonMap("at", branch));
        } finally {
            server.shutdown();
        }
    }
}
