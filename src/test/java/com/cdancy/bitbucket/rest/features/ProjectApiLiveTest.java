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

import java.util.List;

import org.assertj.core.api.Condition;
import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.project.ProjectPage;
import com.cdancy.bitbucket.rest.options.CreateProject;

@Test(groups = "live", testName = "ProjectApiLiveTest", singleThreaded = true)
public class ProjectApiLiveTest extends BaseBitbucketApiLiveTest {

    final String projectKey = randomStringLettersOnly();

    final Condition<Project> withProjectKey = new Condition<Project>() {
        @Override
        public boolean matches(final Project value) {
            return value.key().equals(projectKey);
        }
    };

    @Test
    public void testCreateProject() {
        final CreateProject createProject = CreateProject.create(projectKey, null, null, null);
        final Project project = api().create(createProject);
        assertThat(project).isNotNull();
        assertThat(project.errors().isEmpty()).isTrue();
        assertThat(projectKey.equalsIgnoreCase(project.key())).isTrue();
    }

    @Test (dependsOnMethods = "testCreateProject")
    public void testGetProject() {
        final Project project = api().get(projectKey);
        assertThat(project).isNotNull();
        assertThat(project.errors().isEmpty()).isTrue();
        assertThat(projectKey.equalsIgnoreCase(project.key())).isTrue();
    }

    @Test (dependsOnMethods = "testGetProject")
    public void testDeleteProject() {
        final boolean success = api().delete(projectKey);
        assertThat(success).isTrue();
    }

    @Test(dependsOnMethods = "testGetProject")
    public void testListProjects() {
        final ProjectPage projectPage = api().list(null, null, 0, 100);

        assertThat(projectPage).isNotNull();
        assertThat(projectPage.errors()).isEmpty();
        assertThat(projectPage.size()).isGreaterThan(0);

        final List<Project> projects = projectPage.values();
        assertThat(projects).isNotEmpty();
        assertThat(projects).areExactly(1, withProjectKey);
    }

    @Test
    public void testDeleteProjectNonExistent() {
        final boolean success = api().delete(randomStringLettersOnly());
        assertThat(success).isFalse();
    }

    @Test
    public void testGetProjectNonExistent() {
        final Project project = api().get(randomStringLettersOnly());
        assertThat(project).isNotNull();
        assertThat(project.errors().isEmpty()).isTrue();
    }

    @Test
    public void testCreateProjectWithIllegalName() {
        final String illegalProjectKey = "9999";
        final CreateProject createProject = CreateProject.create(illegalProjectKey, null, null, null);
        final Project project = api().create(createProject);
        assertThat(project).isNotNull();
        assertThat(project.errors().isEmpty()).isFalse();
    }

    private ProjectApi api() {
        return api.projectApi();
    }
}
