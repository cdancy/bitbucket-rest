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
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.system.Version;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Mock tests for the {@link ProjectApi} class.
 */
@Test(groups = "unit", testName = "ProjctApiMockTest")
public class ProjectApiMockTest extends BaseBitbucketMockTest {

    public void testCreateProject() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/project-create.json")).setResponseCode(201));
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
            assertTrue(project.errors().size() == 1);
            assertSent(server, "POST", "/rest/api/" + BitbucketApiMetadata.API_VERSION + "/projects");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
