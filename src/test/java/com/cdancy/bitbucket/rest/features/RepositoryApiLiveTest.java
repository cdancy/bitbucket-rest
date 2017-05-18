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
import com.cdancy.bitbucket.rest.domain.repository.PermissionsPage;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import org.assertj.core.api.Condition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "RepositoryApiLiveTest", singleThreaded = true)
public class RepositoryApiLiveTest extends BaseBitbucketApiLiveTest {

    String projectKey = randomStringLettersOnly();
    String repoKey = randomStringLettersOnly();

    Condition<Repository> withRepositorySlug = new Condition<Repository>() {
        @Override
        public boolean matches(Repository value) {
            return value.slug().toLowerCase().equals(repoKey.toLowerCase());
        }
    };

    @BeforeClass
    public void init() {
        CreateProject createProject = CreateProject.create(projectKey, null, null, null);
        Project project = api.projectApi().create(createProject);
        assertThat(project).isNotNull();
        assertThat(project.errors().isEmpty()).isTrue();
        assertThat(project.key().equalsIgnoreCase(projectKey)).isTrue();
    }

    @Test
    public void testCreateRepository() {
        CreateRepository createRepository = CreateRepository.create(repoKey, true);
        Repository repository = api().create(projectKey, createRepository);
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isTrue();
        assertThat(repoKey.equalsIgnoreCase(repository.name())).isTrue();
    }

    @Test (dependsOnMethods = "testCreateRepository")
    public void testGetRepository() {
        Repository repository = api().get(projectKey, repoKey);
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isTrue();
        assertThat(repoKey.equalsIgnoreCase(repository.name())).isTrue();
    }

    @Test (dependsOnMethods = "testGetRepository", priority = 1000)
    public void testDeleteRepository() {
        boolean success = api().delete(projectKey, repoKey);
        assertThat(success).isTrue();
    }

    @Test(dependsOnMethods = {"testCreateRepository", "testGetRepository"})
    public void testListProjects() {
        RepositoryPage repositoryPage = api().list(projectKey, 0, 100);

        assertThat(repositoryPage).isNotNull();
        assertThat(repositoryPage.errors()).isEmpty();
        assertThat(repositoryPage.size()).isGreaterThan(0);

        List<Repository> repositories = repositoryPage.values();
        assertThat(repositories).isNotEmpty();
        assertThat(repositories).areExactly(1, withRepositorySlug);
    }

    @Test
    public void testDeleteRepositoryNonExistent() {
        boolean success = api().delete(projectKey, randomStringLettersOnly());
        assertThat(success).isTrue();
    }

    @Test
    public void testGetRepositoryNonExistent() {
        Repository repository = api().get(projectKey, randomStringLettersOnly());
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isFalse();
    }

    @Test
    public void testCreateRepositoryWithIllegalName() {
        CreateRepository createRepository = CreateRepository.create("!-_999-9*", true);
        Repository repository = api().create(projectKey, createRepository);
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isFalse();
    }

    @Test(dependsOnMethods = "testGetRepository")
    public void testListPermissionByUser() {
        PermissionsPage permissionsPage = api().listPermissionsByUser(projectKey, repoKey, 0, 100);
        assertThat(permissionsPage.values()).isEmpty();
    }

    @Test(dependsOnMethods = "testGetRepository")
    public void testListPermissionByGroup() {
        PermissionsPage permissionsPage = api().listPermissionsByGroup(projectKey, repoKey, 0, 100);
        assertThat(permissionsPage.values()).isEmpty();
    }

    @Test(dependsOnMethods = {"testGetRepository", "testCreateRepository"})
    public void testCreatePermissionByGroupNonExistent() {
        boolean success = api().createPermissionsByGroup(projectKey, repoKey, "REPO_WRITE", randomString());
        assertThat(success).isFalse();
    }

    @Test(dependsOnMethods = {"testCreatePermissionByGroupNonExistent", "testGetRepository", "testCreateRepository"})
    public void testDeletePermissionByGroupNonExistent() {
        boolean success = api().deletePermissionsByGroup(projectKey, repoKey, randomString());
        assertThat(success).isTrue();
    }

    @Test(dependsOnMethods = {"testListPermissionByGroup","testGetRepository", "testCreateRepository"})
    public void testCreatePermissionByGroup() {
        boolean success = api().createPermissionsByGroup(projectKey, repoKey, "REPO_WRITE", "stash-users");
        assertThat(success).isTrue();
    }

    @Test(dependsOnMethods = {"testListPermissionByGroup","testGetRepository", "testCreateRepository", "testCreatePermissionByGroup"})
    public void testDeletePermissionByGroup() {
        boolean success = api().deletePermissionsByGroup(projectKey, repoKey, "stash-users");
        assertThat(success).isTrue();
    }

    @AfterClass
    public void fin() {
        boolean success = api.projectApi().delete(projectKey);
        assertThat(success).isTrue();
    }

    private RepositoryApi api() {
        return api.repositoryApi();
    }
}
