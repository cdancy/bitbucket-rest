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

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.options.CreateProject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "ProjectApiLiveTest", singleThreaded = true)
public class ProjectApiLiveTest extends BaseBitbucketApiLiveTest {

    String projectKey = randomStringLettersOnly();

    @Test
    public void testCreateProject() {
        CreateProject createProject = CreateProject.create(projectKey, null, null, null);
        Project project = api().create(createProject);
        assertNotNull(project);
        assertTrue(project.errors().size() == 0);
        assertTrue(project.key().equalsIgnoreCase(projectKey));
        assertTrue(project.name().equalsIgnoreCase(projectKey));
    }

    @Test (dependsOnMethods = "testCreateProject")
    public void testGetProject() {
        Project project = api().get(projectKey);
        assertNotNull(project);
        assertTrue(project.errors().size() == 0);
        assertTrue(project.key().equalsIgnoreCase(projectKey));
        assertTrue(project.name().equalsIgnoreCase(projectKey));
    }

    @Test (dependsOnMethods = "testGetProject")
    public void testDeleteProject() {
        boolean success = api().delete(projectKey);
        assertTrue(success);
    }

    @Test
    public void testDeleteProjectNonExistent() {
        boolean success = api().delete(randomStringLettersOnly());
        assertFalse(success);
    }

    @Test
    public void testGetProjectNonExistent() {
        Project project = api().get(randomStringLettersOnly());
        assertNotNull(project);
        assertTrue(project.errors().size() == 1);
    }

    @Test
    public void testCreateProjectWithIllegalName() {
        String projectKey = "9999";
        CreateProject createProject = CreateProject.create(projectKey, null, null, null);
        Project project = api().create(createProject);
        assertNotNull(project);
        assertTrue(project.errors().size() == 1);
    }

    private ProjectApi api() {
        return api.projectApi();
    }
}
