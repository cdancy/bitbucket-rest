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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.project.ProjectPage;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link ProjectApi} class.
 */
@Test(groups = "unit", testName = "ProjctApiMockTest")
public class ProjectApiMockTest extends BaseBitbucketMockTest {

    public void testCreateProject() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project.json")).setResponseCode(201));
        BitbucketApi baseApi = api(server.getUrl("/"));
        ProjectApi api = baseApi.projectApi();
        try {
            String projectKey = "HELLO";
            CreateProject createProject = CreateProject.create(projectKey, null, null, null);
            Project project = api.create(createProject);
            assertNotNull(project);
            assertTrue(project.errors().size() == 0);
            assertTrue(project.key().equalsIgnoreCase(projectKey));
            assertTrue(project.name().equalsIgnoreCase(projectKey));
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateProjectWithIllegalName() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project-create-fail.json")).setResponseCode(400));
        BitbucketApi baseApi = api(server.getUrl("/"));
        ProjectApi api = baseApi.projectApi();
        try {
            String projectKey = "9999";
            CreateProject createProject = CreateProject.create(projectKey, null, null, null);
            Project project = api.create(createProject);
            assertNotNull(project);
            assertFalse(project.errors().isEmpty());
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetProject() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        ProjectApi api = baseApi.projectApi();
        try {
            String projectKey = "HELLO";
            Project project = api.get(projectKey);
            assertNotNull(project);
            assertTrue(project.errors().isEmpty());
            assertTrue(projectKey.equalsIgnoreCase(project.key()));
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetProjectNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project-not-exist.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        ProjectApi api = baseApi.projectApi();
        try {
            String projectKey = "HelloWorld";
            Project project = api.get(projectKey);
            assertNotNull(project);
            assertFalse(project.errors().isEmpty());
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteProject() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        BitbucketApi baseApi = api(server.getUrl("/"));
        ProjectApi api = baseApi.projectApi();
        try {
            String projectKey = "HELLO";
            boolean success = api.delete(projectKey);
            assertTrue(success);
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testDeleteProjectNonExistent() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project-not-exist.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        ProjectApi api = baseApi.projectApi();
        try {
            String projectKey = "NOTEXIST";
            boolean success = api.delete(projectKey);
            assertFalse(success);
            assertSent(server, "DELETE", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects/" + projectKey);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetProjectList() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project-page-full.json")).setResponseCode(200));
        try (BitbucketApi baseApi = api(server.getUrl("/"))) {
            ProjectApi api = baseApi.projectApi();

            ProjectPage projectPage = api.list(null, null, null, null);

            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects");

            assertNotNull(projectPage);
            assertThat(projectPage.errors()).isEmpty();

            int size = projectPage.size();
            int limit = projectPage.limit();

            assertThat(size).isLessThanOrEqualTo(limit);
            assertThat(projectPage.start()).isEqualTo(0);
            assertThat(projectPage.isLastPage()).isTrue();

            assertThat(projectPage.values()).hasSize(size);
            assertThat(projectPage.values()).hasOnlyElementsOfType(Project.class);
        } finally {
            server.shutdown();
        }
    }

    public void testGetProjectListWithLimit() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project-page-truncated.json")).setResponseCode(200));
        try (BitbucketApi baseApi = api(server.getUrl("/"))) {
            ProjectApi api = baseApi.projectApi();

            int start = 0;
            int limit = 2;
            ProjectPage projectPage = api.list(start, limit, null, null);

            Map<String, ?> queryParams = ImmutableMap.of("start", start, "limit", limit);
            assertSent(server, "GET", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects", queryParams);

            assertNotNull(projectPage);
            assertThat(projectPage.errors()).isEmpty();

            int size = projectPage.size();

            assertThat(size).isEqualTo(limit);
            assertThat(projectPage.start()).isEqualTo(start);
            assertThat(projectPage.limit()).isEqualTo(limit);
            assertThat(projectPage.isLastPage()).isFalse();
            assertThat(projectPage.nextPageStart()).isEqualTo(size);

            assertThat(projectPage.values()).hasSize(size);
            assertThat(projectPage.values()).hasOnlyElementsOfType(Project.class);
        } finally {
            server.shutdown();
        }
    }
}
