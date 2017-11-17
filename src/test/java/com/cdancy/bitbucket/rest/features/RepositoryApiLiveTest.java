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
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.Hook;
import com.cdancy.bitbucket.rest.domain.repository.HookPage;
import com.cdancy.bitbucket.rest.domain.repository.MergeConfig;
import com.cdancy.bitbucket.rest.domain.repository.MergeStrategy;
import com.cdancy.bitbucket.rest.domain.repository.PermissionsPage;
import com.cdancy.bitbucket.rest.domain.repository.PullRequestSettings;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.options.CreatePullRequestSettings;
import com.cdancy.bitbucket.rest.options.CreateRepository;
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
    private String hookKey;
    private User user;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.credential, this.api);
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
        this.user = TestUtilities.getDefaultUser(this.credential, this.api);
    }

    @Test 
    public void testGetRepository() {
        final Repository repository = api().get(projectKey, repoKey);
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isTrue();
        assertThat(repoKey.equalsIgnoreCase(repository.name())).isTrue();
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
    public void testDeleteRepositoryNonExistent() {
        final String random = TestUtilities.randomStringLettersOnly();
        final RequestStatus success = api().delete(projectKey, random);
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @Test
    public void testGetRepositoryNonExistent() {
        final Repository repository = api().get(projectKey, TestUtilities.randomStringLettersOnly());
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
    public void testListHooks() {
        final HookPage hookPage = api().listHooks(projectKey, repoKey, 0, 100);
        assertThat(hookPage).isNotNull();
        assertThat(hookPage.errors()).isEmpty();
        assertThat(hookPage.size()).isGreaterThan(0);
        for (final Hook hook : hookPage.values()) {
            if (hook.details().configFormKey() == null) {
                assertThat(hook.details().key()).isNotNull();
                hookKey = hook.details().key();
                break;
            }
        }
    }

    @Test()
    public void testListHookOnError() {
        final HookPage hookPage = api().listHooks(projectKey, TestUtilities.randomString(), 0, 100);
        assertThat(hookPage).isNotNull();
        assertThat(hookPage.errors()).isNotEmpty();
        assertThat(hookPage.values()).isEmpty();
    }

    @Test(dependsOnMethods = {"testListHooks"})
    public void testGetHook() {
        final Hook hook = api().getHook(projectKey, repoKey, hookKey);
        assertThat(hook).isNotNull();
        assertThat(hook.errors()).isEmpty();
        assertThat(hook.details().key().equals(hookKey)).isTrue();
        assertThat(hook.enabled()).isFalse();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testGetHookOnError() {
        final Hook hook = api().getHook(projectKey, 
                repoKey, 
                TestUtilities.randomStringLettersOnly() 
                        + ":" 
                        + TestUtilities.randomStringLettersOnly());
        assertThat(hook).isNotNull();
        assertThat(hook.errors()).isNotEmpty();
        assertThat(hook.enabled()).isFalse();
    }

    @Test(dependsOnMethods = {"testGetHook"})
    public void testEnableHook() {
        final Hook hook = api().enableHook(projectKey, repoKey, hookKey);
        assertThat(hook).isNotNull();
        assertThat(hook.errors()).isEmpty();
        assertThat(hook.details().key().equals(hookKey)).isTrue();
        assertThat(hook.enabled()).isTrue();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testEnableHookOnError() {
        final Hook hook = api().enableHook(projectKey, 
                repoKey, 
                TestUtilities.randomStringLettersOnly() 
                        + ":" 
                        + TestUtilities.randomStringLettersOnly());
        assertThat(hook).isNotNull();
        assertThat(hook.errors()).isNotEmpty();
        assertThat(hook.enabled()).isFalse();
    }

    @Test(dependsOnMethods = {"testEnableHook"})
    public void testDisableHook() {
        final Hook hook = api().disableHook(projectKey, repoKey, hookKey);
        assertThat(hook).isNotNull();
        assertThat(hook.errors()).isEmpty();
        assertThat(hook.details().key().equals(hookKey)).isTrue();
        assertThat(hook.enabled()).isFalse();
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testDisableHookOnError() {
        final Hook hook = api().disableHook(projectKey, 
                repoKey, 
                TestUtilities.randomStringLettersOnly() 
                        + ":" 
                        + TestUtilities.randomStringLettersOnly());
        assertThat(hook).isNotNull();
        assertThat(hook.errors()).isNotEmpty();
        assertThat(hook.enabled()).isFalse();
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
