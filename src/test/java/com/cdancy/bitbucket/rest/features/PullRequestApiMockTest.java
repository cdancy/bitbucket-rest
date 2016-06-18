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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link PullRequestApi} class.
 */
@Test(groups = "unit", testName = "PullRequestApiMockTest")
public class PullRequestApiMockTest extends BaseBitbucketMockTest {

    public void testGetPullRequest() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request.json")).setResponseCode(200));
        BitbucketApi etcdJavaApi = api(server.getUrl("/"));
        PullRequestApi api = etcdJavaApi.pullRequestApi();
        try {
         PullRequest pr = api.get("PRJ", "my-repo", 101);
         assertNotNull(pr);
         assertTrue(pr.errors().size() == 0);
         assertTrue(pr.fromRef().repository().project().key().equals("PRJ"));
         assertTrue(pr.fromRef().repository().slug().equals("my-repo"));
         assertTrue(pr.id() == 101);
         assertSent(server, "GET",
               "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/PRJ/repos/my-repo/pull-requests/101");
        } finally {
         etcdJavaApi.close();
         server.shutdown();
        }
    }

    public void testDeclinePullRequest() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-decline.json")).setResponseCode(200));
      BitbucketApi etcdJavaApi = api(server.getUrl("/"));
      PullRequestApi api = etcdJavaApi.pullRequestApi();
      try {
         PullRequest pr = api.decline("PRJ", "my-repo", 101, 1);
         assertNotNull(pr);
         assertTrue(pr.errors().size() == 0);
         assertTrue(pr.fromRef().repository().project().key().equals("PRJ"));
         assertTrue(pr.fromRef().repository().slug().equals("my-repo"));
         assertTrue(pr.id() == 101);
          assertTrue(pr.state().equalsIgnoreCase("DECLINED"));
          assertFalse(pr.open());
         assertSent(server, "POST",
                 "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/PRJ/repos/my-repo/pull-requests/101/decline?version=1");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
    }

    public void testReopenPullRequest() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request.json")).setResponseCode(200));
        BitbucketApi etcdJavaApi = api(server.getUrl("/"));
        PullRequestApi api = etcdJavaApi.pullRequestApi();
        try {
            PullRequest pr = api.reopen("PRJ", "my-repo", 101, 1);
            assertNotNull(pr);
            assertTrue(pr.errors().size() == 0);
            assertTrue(pr.fromRef().repository().project().key().equals("PRJ"));
            assertTrue(pr.fromRef().repository().slug().equals("my-repo"));
            assertTrue(pr.id() == 101);
            assertTrue(pr.state().equalsIgnoreCase("OPEN"));
            assertTrue(pr.open());
            assertSent(server, "POST",
                    "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/PRJ/repos/my-repo/pull-requests/101/reopen?version=1");
        } finally {
            etcdJavaApi.close();
            server.shutdown();
        }
    }

    public void testMergePullRequest() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-merge.json")).setResponseCode(200));
        BitbucketApi etcdJavaApi = api(server.getUrl("/"));
        PullRequestApi api = etcdJavaApi.pullRequestApi();
        try {
            PullRequest pr = api.merge("PRJ", "my-repo", 101, 1);
            assertNotNull(pr);
            assertTrue(pr.errors().size() == 0);
            assertTrue(pr.fromRef().repository().project().key().equals("PRJ"));
            assertTrue(pr.fromRef().repository().slug().equals("my-repo"));
            assertTrue(pr.id() == 101);
            assertTrue(pr.state().equalsIgnoreCase("MERGED"));
            assertFalse(pr.open());
            assertSent(server, "POST",
                    "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/PRJ/repos/my-repo/pull-requests/101/merge?version=1");
        } finally {
            etcdJavaApi.close();
            server.shutdown();
        }
    }

    public void testGetPullRequestNonExistent() throws Exception {
      MockWebServer server = mockEtcdJavaWebServer();

      server.enqueue(
            new MockResponse().setBody(payloadFromResource("/pull-request-not-exist.json")).setResponseCode(404));
      BitbucketApi etcdJavaApi = api(server.getUrl("/"));
      PullRequestApi api = etcdJavaApi.pullRequestApi();
      try {
         PullRequest pr = api.get("PRJ", "my-repo", 101);
         assertNotNull(pr);
         assertTrue(pr.errors().size() > 0);
         assertTrue(pr.errors().get(0).exceptionName().endsWith("NoSuchPullRequestException"));
         assertSent(server, "GET",
               "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/PRJ/repos/my-repo/pull-requests/101");
      } finally {
         etcdJavaApi.close();
         server.shutdown();
      }
    }
}
