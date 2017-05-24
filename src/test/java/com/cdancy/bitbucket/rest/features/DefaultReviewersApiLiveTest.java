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
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "DefaultReviewersApiLiveTest", singleThreaded = true)
public class DefaultReviewersApiLiveTest extends BaseBitbucketApiLiveTest {

    String projectKey = randomStringLettersOnly();
    String repoKey = randomStringLettersOnly();

    @BeforeClass
    public void init() {
        CreateProject createProject = CreateProject.create(projectKey, null, null, null);
        Project project = api.projectApi().create(createProject);
        assertThat(project).isNotNull();
        assertThat(project.errors().isEmpty()).isTrue();
        assertThat(project.key().equalsIgnoreCase(projectKey)).isTrue();
        CreateRepository createRepository = CreateRepository.create(repoKey, true);
        Repository repository = api.repositoryApi().create(projectKey, createRepository);
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isTrue();
        assertThat(repoKey.equalsIgnoreCase(repository.name())).isTrue();
    }

    public void testListDefaultReviewersOnEmptyRepo() {
        List<Condition> conditionList = api().listConditions(projectKey, repoKey);
        assertThat(conditionList).isEmpty();
    }

    @AfterClass
    public void fin() {
        boolean success = api.repositoryApi().delete(projectKey, repoKey);
        assertThat(success).isTrue();
        success = api.projectApi().delete(projectKey);
        assertThat(success).isTrue();
    }

    private DefaultReviewersApi api() {
        return api.defaultReviewersApi();
    }
}
