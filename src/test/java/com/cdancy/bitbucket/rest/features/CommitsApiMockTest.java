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
import com.cdancy.bitbucket.rest.domain.commit.Commit;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

/**
 * Mock tests for the {@link CommitsApi} class.
 */
@Test(groups = "unit", testName = "CommitApiMockTest")
public class CommitsApiMockTest extends BaseBitbucketMockTest {

    public void testGetCommit() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommitsApi api = baseApi.commitsApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            String commitHash = "abcdef0123abcdef4567abcdef8987abcdef6543";

            Commit commit = api.get(projectKey, repoKey, commitHash, null);
            assertThat(commit).isNotNull();
            assertThat(commit.errors().isEmpty()).isTrue();
            assertThat(commit.id().equalsIgnoreCase(commitHash)).isTrue();
            
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/commits/" + commitHash);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testGetCommitNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/commit-error.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        CommitsApi api = baseApi.commitsApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            String commitHash = "abcdef0123abcdef4567abcdef8987abcdef6543";

            Commit commit = api.get(projectKey, repoKey, commitHash, null);
            assertThat(commit).isNotNull();
            assertThat(commit.errors().size() > 0).isTrue();

            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/commits/" + commitHash);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
