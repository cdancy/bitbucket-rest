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

import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.project.ProjectPage;
import com.cdancy.bitbucket.rest.options.CreateProject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

@Test(groups = "live", testName = "ProjectApiLiveTest", singleThreaded = true)
public class ProjectApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.credential, this.api);
    }

    @Test
    public void testGetProject() {
        final Project project = api().get(generatedTestContents.project.key());
        assertThat(project).isNotNull();
        assertThat(project.errors().isEmpty()).isTrue();
        assertThat(project.key()).isEqualTo(generatedTestContents.project.key());
    }

    @Test
    public void testListProjects() {
        final ProjectPage projectPage = api().list(null, null, 0, 100);

        assertThat(projectPage).isNotNull();
        assertThat(projectPage.errors()).isEmpty();
        assertThat(projectPage.size()).isGreaterThan(0);

        final List<Project> projects = projectPage.values();
        assertThat(projects).isNotEmpty();
        boolean found = false;
        for (final Project possibleProject : projectPage.values()) {
            if (possibleProject.key().equals(generatedTestContents.project.key())) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    public void testDeleteProjectNonExistent() {
        final RequestStatus success = api().delete(TestUtilities.randomStringLettersOnly());
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @Test
    public void testGetProjectNonExistent() {
        final Project project = api().get(TestUtilities.randomStringLettersOnly());
        assertThat(project).isNotNull();
        assertThat(project.errors().isEmpty()).isFalse();
    }

    @Test
    public void testCreateProjectWithIllegalName() {
        if (!generatedTestContents.projectPreviouslyExists) {
            final String illegalProjectKey = "9999";
            final CreateProject createProject = CreateProject.create(illegalProjectKey, null, null, null);
            final Project project = api().create(createProject);
            assertThat(project).isNotNull();
            assertThat(project.errors().isEmpty()).isFalse();
        } else {
            System.out.println("Project previously existed and so assuming we don't have credentials to create Projects");
        }
    }
    
    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private ProjectApi api() {
        return api.projectApi();
    }
}
