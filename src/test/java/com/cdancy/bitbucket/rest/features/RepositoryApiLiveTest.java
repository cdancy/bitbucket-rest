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
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "RepositoryApiLiveTest", singleThreaded = true)
public class RepositoryApiLiveTest extends BaseBitbucketApiLiveTest {

    String projectKey = randomStringLettersOnly();
    String repoKey = randomStringLettersOnly();

    @BeforeClass
    public void init() {
        CreateProject createProject = CreateProject.create(projectKey, null, null, null);
        Project project = api.projectApi().create(createProject);
        assertNotNull(project);
        assertTrue(project.errors().size() == 0);
        assertTrue(project.key().equalsIgnoreCase(projectKey));
    }

    @Test
    public void testCreateRepository() {
        CreateRepository createRepository = CreateRepository.create(repoKey, true);
        Repository repository = api().create(projectKey, createRepository);
        assertNotNull(repository);
        assertTrue(repository.errors().size() == 0);
        assertTrue(repository.name().equalsIgnoreCase(repoKey));
    }

    @Test (dependsOnMethods = "testCreateRepository")
    public void testGetRepository() {
        Repository repository = api().get(projectKey, repoKey);
        assertNotNull(repository);
        assertTrue(repository.errors().size() == 0);
        assertTrue(repository.name().equalsIgnoreCase(repoKey));
    }

    @Test (dependsOnMethods = "testGetRepository")
    public void testDeleteRepository() {
        boolean success = api().delete(projectKey, repoKey);
        assertTrue(success);
    }

    @Test
    public void testDeleteRepositoryNonExistent() {
        boolean success = api().delete(projectKey, randomStringLettersOnly());
        assertTrue(success);
    }

    @Test
    public void testGetRepositoryNonExistent() {
        Repository repository = api().get(projectKey, randomStringLettersOnly());
        assertNotNull(repository);
        assertTrue(repository.errors().size() == 1);
    }

    @Test
    public void testCreateRepositoryWithIllegalName() {
        CreateRepository createRepository = CreateRepository.create("!-_999-9*", true);
        Repository repository = api().create(projectKey, createRepository);
        assertNotNull(repository);
        assertTrue(repository.errors().size() > 0);
    }

    @AfterClass
    public void fin() {
        boolean success = api.projectApi().delete(projectKey);
        assertTrue(success);
    }

    private RepositoryApi api() {
        return api.repositoryApi();
    }
}
