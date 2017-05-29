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

    private final String localMethod = "GET";
    private final String localPath = "/projects";
    
    public void testCreateProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project.json"))
                .setResponseCode(201));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final String projectKey = "HELLO";
            final CreateProject createProject = CreateProject.create(projectKey, null, null, null);
            final Project project = baseApi.projectApi().create(createProject);
            
            assertThat(project).isNotNull();
            assertThat(project.errors()).isEmpty();
            assertThat(project.key()).isEqualToIgnoringCase(projectKey);
            assertThat(project.name()).isEqualToIgnoringCase(projectKey);
            assertThat(project.links()).isNotNull();
            assertSent(server, "POST", restBasePath + BitbucketApiMetadata.API_VERSION + localPath);
        } finally {
            server.shutdown();
        }
    }

    public void testCreateProjectWithIllegalName() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-create-fail.json"))
                .setResponseCode(400));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final String projectKey = "9999";
            final CreateProject createProject = CreateProject.create(projectKey, null, null, null);
            final Project project = baseApi.projectApi().create(createProject);
            
            assertThat(project).isNotNull();
            assertThat(project.errors()).isNotEmpty();
            assertSent(server, "POST", restBasePath + BitbucketApiMetadata.API_VERSION + localPath);
        } finally {
            server.shutdown();
        }
    }

    public void testGetProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final String projectKey = "HELLO";
            final Project project = baseApi.projectApi().get(projectKey);
            
            assertThat(project).isNotNull();
            assertThat(project.errors()).isEmpty();
            assertThat(project.key()).isEqualToIgnoringCase(projectKey);
            assertThat(project.links()).isNotNull();
            assertSent(server, localMethod, restBasePath + BitbucketApiMetadata.API_VERSION + localPath + "/" + projectKey);
        } finally {
            server.shutdown();
        }
    }

    public void testGetProjectNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-not-exist.json"))
                .setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final String projectKey = "HelloWorld";
            final Project project = baseApi.projectApi().get(projectKey);
            
            assertThat(project).isNotNull();
            assertThat(project.errors()).isNotEmpty();
            assertSent(server, localMethod, restBasePath + BitbucketApiMetadata.API_VERSION + localPath + "/" + projectKey);
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteProject() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final String projectKey = "HELLO";
            final boolean success = baseApi.projectApi().delete(projectKey);
            assertThat(success).isTrue();
            assertSent(server, "DELETE", restBasePath + BitbucketApiMetadata.API_VERSION + localPath + "/" + projectKey);
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteProjectNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-not-exist.json"))
                .setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final String projectKey = "NOTEXIST";
            final boolean success = baseApi.projectApi().delete(projectKey);
            
            assertThat(success).isFalse();
            assertSent(server, "DELETE", restBasePath + BitbucketApiMetadata.API_VERSION + localPath + "/" + projectKey);
        } finally {
            server.shutdown();
        }
    }

    public void testGetProjectList() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-page-full.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final ProjectPage projectPage = baseApi.projectApi().list(null, null, null, null);

            assertThat(projectPage).isNotNull();
            assertThat(projectPage.errors()).isEmpty();
            
            assertThat(projectPage.size()).isLessThanOrEqualTo(projectPage.limit());
            assertThat(projectPage.start()).isEqualTo(0);
            assertThat(projectPage.isLastPage()).isTrue();

            assertThat(projectPage.values()).hasSize(projectPage.size());
            assertThat(projectPage.values()).hasOnlyElementsOfType(Project.class);
            assertSent(server, localMethod, restBasePath + BitbucketApiMetadata.API_VERSION + localPath);
        } finally {
            server.shutdown();
        }
    }

    public void testGetProjectListWithLimit() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-page-truncated.json"))
                .setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final int start = 0;
            final int limit = 2;
            final ProjectPage projectPage = baseApi.projectApi().list(null, null, start, limit);

            assertThat(projectPage).isNotNull();
            assertThat(projectPage.errors()).isEmpty();

            final int size = projectPage.size();

            assertThat(size).isEqualTo(limit);
            assertThat(projectPage.start()).isEqualTo(start);
            assertThat(projectPage.limit()).isEqualTo(limit);
            assertThat(projectPage.isLastPage()).isFalse();
            assertThat(projectPage.nextPageStart()).isEqualTo(size);

            assertThat(projectPage.values()).hasSize(size);
            assertThat(projectPage.values()).hasOnlyElementsOfType(Project.class);
            
            final Map<String, ?> queryParams = ImmutableMap.of("start", start, "limit", limit);
            assertSent(server, localMethod, restBasePath + BitbucketApiMetadata.API_VERSION + localPath, queryParams);
        } finally {
            server.shutdown();
        }
    }
    
    public void testGetProjectListNonExistent() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse()
                .setBody(payloadFromResource("/project-not-exist.json"))
                .setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final ProjectPage projectPage = baseApi.projectApi().list(null, null, null, null);

            assertThat(projectPage).isNotNull();
            assertThat(projectPage.errors()).isNotEmpty();
            assertSent(server, localMethod, restBasePath + BitbucketApiMetadata.API_VERSION + localPath);
        } finally {
            server.shutdown();
        }
    }
}
