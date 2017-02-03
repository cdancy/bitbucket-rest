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

import java.util.List;

import org.assertj.core.api.Condition;
import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.project.ProjectPage;
import com.cdancy.bitbucket.rest.options.CreateProject;

@Test(groups = "live", testName = "ProjectApiLiveTest", singleThreaded = true)
public class ProjectApiLiveTest extends BaseBitbucketApiLiveTest {

    String projectKey = randomStringLettersOnly();

    Condition<Project> withProjectKey = new Condition<Project>() {
        @Override
        public boolean matches(Project value) {
            return value.key().equals(projectKey);
        }
    };

    @Test
    public void testCreateProject() {
        CreateProject createProject = CreateProject.create(projectKey, null, null, null);
        Project project = api().create(createProject);
        assertNotNull(project);
        assertTrue(project.errors().isEmpty());
        assertTrue(projectKey.equalsIgnoreCase(project.key()));
    }

    @Test (dependsOnMethods = "testCreateProject")
    public void testGetProject() {
        Project project = api().get(projectKey);
        assertNotNull(project);
        assertTrue(project.errors().isEmpty());
        assertTrue(projectKey.equalsIgnoreCase(project.key()));
    }

    @Test (dependsOnMethods = "testGetProject")
    public void testDeleteProject() {
        boolean success = api().delete(projectKey);
        assertTrue(success);
    }

    @Test(dependsOnMethods = "testGetProject")
    public void testListProjects() {
        ProjectPage projectPage = api().list(null, null, 0, 100);

        assertNotNull(projectPage);
        assertThat(projectPage.errors()).isEmpty();
        assertThat(projectPage.size()).isGreaterThan(0);

        List<Project> projects = projectPage.values();
        assertThat(projects).isNotEmpty();
        assertThat(projects).areExactly(1, withProjectKey);
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
        assertTrue(project.errors().isEmpty());
    }

    @Test
    public void testCreateProjectWithIllegalName() {
        String illegalProjectKey = "9999";
        CreateProject createProject = CreateProject.create(illegalProjectKey, null, null, null);
        Project project = api().create(createProject);
        assertNotNull(project);
        assertFalse(project.errors().isEmpty());
    }

    private ProjectApi api() {
        return api.projectApi();
    }
}
