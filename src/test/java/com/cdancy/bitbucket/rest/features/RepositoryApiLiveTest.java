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

import static com.cdancy.bitbucket.rest.TestUtilities.randomStringLettersOnly;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.MergeConfig;
import com.cdancy.bitbucket.rest.domain.repository.MergeStrategy;
import com.cdancy.bitbucket.rest.domain.repository.PermissionsPage;
import com.cdancy.bitbucket.rest.domain.repository.PullRequestSettings;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.options.CreatePullRequestSettings;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import com.google.common.collect.Lists;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "RepositoryApiLiveTest", singleThreaded = true)
public class RepositoryApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private final String testGetRepoKeyword = "testGetRepository";
    private final String repoWriteKeyword = "REPO_WRITE";

    private String projectKey;
    private String repoKey;
    private User user;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
        this.user = TestUtilities.getDefaultUser(this.bitbucketAuthentication, this.api);
    }

    @Test
    public void testGetRepository() {
        final Repository repository = api().get(projectKey, repoKey);
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isTrue();
        assertThat(repoKey.equalsIgnoreCase(repository.name())).isTrue();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testForkRepository() {
        final String forkedRepoName = randomStringLettersOnly();
        final Repository repository = api().fork(projectKey, repoKey, projectKey, forkedRepoName);
        assertThat(repository).isNotNull();
        assertThat(repository.errors()).isEmpty();
        assertThat(forkedRepoName.equalsIgnoreCase(repository.name())).isTrue();
        generatedTestContents.addRepoForDeletion(projectKey, forkedRepoName);
    }

    @Test
    public void testForkRepositoryNonExistent() {
        final Repository repository = api().fork(projectKey, randomStringLettersOnly(), projectKey, randomStringLettersOnly());
        assertThat(repository).isNotNull();
        assertThat(repository.errors()).isNotEmpty();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testListRepositories() {
        final RepositoryPage repositoryPage = api().list(projectKey, 0, 100);

        assertThat(repositoryPage).isNotNull();
        assertThat(repositoryPage.errors()).isEmpty();
        assertThat(repositoryPage.size()).isGreaterThan(0);

        assertThat(repositoryPage.values()).isNotEmpty();
        boolean found = false;
        for (final Repository possibleRepo : repositoryPage.values()) {
            if (possibleRepo.name().equals(repoKey)) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    public void testListAllRepositories() {

        final List<Repository> foundRepos = Lists.newArrayList();

        int start = 0;
        int limit = 100;
        RepositoryPage repositoryPage;
        while(!(repositoryPage = api().listAll(null, null, null, null, start, limit)).isLastPage()) {
            start += limit;

            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();
            assertThat(repositoryPage.size()).isGreaterThan(0);

            foundRepos.addAll(repositoryPage.values());
        }

        assertThat(foundRepos).isNotEmpty();

        boolean found = false;
        for (final Repository possibleRepo : foundRepos) {
            if (possibleRepo.name().equals(repoKey)) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    public void testListAllRepositoriesByRepository() {
        final List<Repository> foundRepos = Lists.newArrayList();

        int start = 0;
        int limit = 100;
        RepositoryPage repositoryPage;
        while(!(repositoryPage = api().listAll(null, repoKey, null, null, start, limit)).isLastPage()) {
            start += limit;

            assertThat(repositoryPage).isNotNull();
            assertThat(repositoryPage.errors()).isEmpty();
            assertThat(repositoryPage.size()).isGreaterThan(0);

            foundRepos.addAll(repositoryPage.values());
        }

        assertThat(foundRepos).isNotEmpty();
        assertThat(foundRepos.size()).isEqualTo(1);
        assertThat(foundRepos.get(0).name()).isEqualTo(repoKey);
    }

    @Test
    public void testListAllRepositoriesByProjectNonExistent() {
        final String projectKey = "HelloWorld";
        final RepositoryPage repositoryPage = api().listAll(projectKey, null, null, null, 0, 100);
        assertThat(repositoryPage).isNotNull();
        assertThat(repositoryPage.errors()).isEmpty();
        assertThat(repositoryPage.size()).isEqualTo(0);
        assertThat(repositoryPage.values()).isEmpty();
    }

    @Test
    public void testListAllRepositoriesByRepositoryNonExistent() {
        final String repoKey = "HelloWorld";
        final RepositoryPage repositoryPage = api().listAll(null, repoKey, null, null, 0, 100);
        assertThat(repositoryPage).isNotNull();
        assertThat(repositoryPage.errors()).isEmpty();
        assertThat(repositoryPage.size()).isEqualTo(0);
        assertThat(repositoryPage.values()).isEmpty();
    }

    @Test
    public void testDeleteRepositoryNonExistent() {
        final String random = randomStringLettersOnly();
        final RequestStatus success = api().delete(projectKey, random);
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @Test
    public void testGetRepositoryNonExistent() {
        final Repository repository = api().get(projectKey, randomStringLettersOnly());
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isFalse();
    }

    @Test
    public void testCreateRepositoryWithIllegalName() {
        final CreateRepository createRepository = CreateRepository.create("!-_999-9*", true);
        final Repository repository = api().create(projectKey, createRepository);
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isFalse();
    }

    @Test (dependsOnMethods = testGetRepoKeyword)
    public void testUpdatePullRequestSettings() {
        final MergeStrategy strategy = MergeStrategy.create(null, null, null, MergeStrategy.MergeStrategyId.SQUASH, null);
        final List<MergeStrategy> listStrategy = new ArrayList<>();
        listStrategy.add(strategy);
        final MergeConfig mergeConfig = MergeConfig.create(strategy, listStrategy, MergeConfig.MergeConfigType.REPOSITORY);
        final CreatePullRequestSettings pullRequestSettings = CreatePullRequestSettings.create(mergeConfig, false, false, 0, 1);

        final PullRequestSettings settings = api().updatePullRequestSettings(projectKey, repoKey, pullRequestSettings);
        assertThat(settings).isNotNull();
        assertThat(settings.errors().isEmpty()).isTrue();
        assertThat(settings.mergeConfig().strategies()).isNotEmpty();
        assertThat(MergeStrategy.MergeStrategyId.SQUASH.equals(settings.mergeConfig().defaultStrategy().id()));
    }

    @Test (dependsOnMethods = "testUpdatePullRequestSettings")
    public void testGetPullRequestSettings() {
        final PullRequestSettings settings = api().getPullRequestSettings(projectKey, repoKey);
        assertThat(settings).isNotNull();
        assertThat(settings.errors().isEmpty()).isTrue();
        assertThat(settings.mergeConfig().strategies()).isNotEmpty();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testCreatePermissionByUser() {
        final RequestStatus success = api().createPermissionsByUser(projectKey, repoKey, repoWriteKeyword, user.slug());
        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();
    }

    @Test(dependsOnMethods = "testCreatePermissionByUser")
    public void testListPermissionByUser() {
        final PermissionsPage permissionsPage = api().listPermissionsByUser(projectKey, repoKey, 0, 100);
        assertThat(permissionsPage.values()).isNotEmpty();
    }

    @Test(dependsOnMethods = {"testListPermissionByUser"})
    public void testDeletePermissionByUser() {
        final RequestStatus success = api().deletePermissionsByUser(projectKey, repoKey, user.slug());
        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testCreatePermissionByGroup() {
        final RequestStatus success = api().createPermissionsByGroup(projectKey, repoKey, repoWriteKeyword, defaultBitbucketGroup);
        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();
    }

    @Test(dependsOnMethods = "testCreatePermissionByGroup")
    public void testListPermissionByGroup() {
        final PermissionsPage permissionsPage = api().listPermissionsByGroup(projectKey, repoKey, 0, 100);
        assertThat(permissionsPage.values()).isNotEmpty();
    }

    @Test(dependsOnMethods = {"testListPermissionByGroup"})
    public void testDeletePermissionByGroup() {
        final RequestStatus success = api().deletePermissionsByGroup(projectKey, repoKey, defaultBitbucketGroup);
        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testCreatePermissionByGroupNonExistent() {
        final RequestStatus success = api().createPermissionsByGroup(projectKey, repoKey, repoWriteKeyword, TestUtilities.randomString());
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testDeletePermissionByGroupNonExistent() {
        final RequestStatus success = api().deletePermissionsByGroup(projectKey, repoKey, TestUtilities.randomString());
        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue(); // Currently Bitbucket returns the same response if delete is success or not
        assertThat(success.errors()).isEmpty();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testCreatePermissionByUserNonExistent() {
        final RequestStatus success = api().createPermissionsByUser(projectKey, repoKey, repoWriteKeyword, TestUtilities.randomString());
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testDeletePermissionByUserNonExistent() {
        final RequestStatus success = api().deletePermissionsByUser(projectKey, repoKey, TestUtilities.randomString());
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private RepositoryApi api() {
        return api.repositoryApi();
    }
}
