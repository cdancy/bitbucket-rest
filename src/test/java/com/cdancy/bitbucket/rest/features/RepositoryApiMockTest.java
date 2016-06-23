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
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Mock tests for the {@link RepositoryApi} class.
 */
@Test(groups = "unit", testName = "RepositoryApiMockTest")
public class RepositoryApiMockTest extends BaseBitbucketMockTest {

    public void testCreateRepository() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository.json")).setResponseCode(201));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            CreateRepository createRepository = CreateRepository.create(repoKey, true);
            Repository repository = api.create(projectKey, createRepository);
            assertNotNull(repository);
            assertTrue(repository.errors().size() == 0);
            assertTrue(repository.name().equalsIgnoreCase(repoKey));
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateRepositoryWithIllegalName() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-illegal-name.json")).setResponseCode(400));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "!_myrepo";
            CreateRepository createRepository = CreateRepository.create(repoKey, true);
            Repository repository = api.create(projectKey, createRepository);
            assertNotNull(repository);
            assertTrue(repository.errors().size() > 0);
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetRepository() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            CreateRepository createRepository = CreateRepository.create(repoKey, true);
            Repository repository = api.get(projectKey, repoKey);
            assertNotNull(repository);
            assertTrue(repository.errors().size() == 0);
            assertTrue(repository.name().equalsIgnoreCase(repoKey));
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetRepositoryNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "notexist";
            CreateRepository createRepository = CreateRepository.create(repoKey, true);
            Repository repository = api.get(projectKey, repoKey);
            assertNotNull(repository);
            assertTrue(repository.errors().size() == 1);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteRepository() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(202));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "myrepo";
            boolean success = api.delete(projectKey, repoKey);
            assertTrue(success);
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteRepositoryNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        BitbucketApi baseApi = api(server.getUrl("/"));
        RepositoryApi api = baseApi.repositoryApi();
        try {
            String projectKey = "PRJ";
            String repoKey = "notexist";
            boolean success = api.delete(projectKey, repoKey);
            assertTrue(success);
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey + "/repos/" + repoKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
