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

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.repository.Hook;
import com.cdancy.bitbucket.rest.domain.repository.HookPage;
import com.cdancy.bitbucket.rest.domain.repository.HookSettings;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.google.gson.internal.LinkedTreeMap;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "HookApiLiveTest", singleThreaded = true)
public class HookApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private final String testGetRepoKeyword = "testGetRepository";

    private String projectKey;
    private String repoKey;
    private Hook foundHook;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
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

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testListHooks() {
        final HookPage hookPage = api().listHooks(projectKey, repoKey, 0, 100);
        assertThat(hookPage).isNotNull();
        assertThat(hookPage.errors()).isEmpty();
        assertThat(hookPage.size()).isGreaterThan(0);
        for (final Hook hook : hookPage.values()) {
            if (hook.details().configFormKey() == null) {
                assertThat(hook.details().key()).isNotNull();
                this.foundHook = hook;
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
        final Hook hook = api().getHook(projectKey, repoKey, foundHook.details().key());
        assertThat(hook).isNotNull();
        assertThat(hook.errors()).isEmpty();
        assertThat(hook.details().key().equals(foundHook.details().key())).isTrue();
        assertThat(hook.enabled()).isFalse();
    }

    @Test(dependsOnMethods = {"testGetHook"})
    public void testGetAndUpdateHookSettings() throws Exception {
        final HookPage hookPage = api().listHooks(projectKey, repoKey, 0, 100);
        assertThat(hookPage).isNotNull();
        assertThat(hookPage.errors()).isEmpty();
        assertThat(hookPage.size()).isGreaterThan(0);

        // iterate over each found HookSettings and attempt to update, if we have any
        // data, with the same data and ensure all works as expected.
        for (final Hook hook : hookPage.values()) {
            final HookSettings hooks = api().getHookSettings(projectKey, repoKey, hook.details().key());
            // cheap way to check if body is NOT equal to "{}" which is an empty json map
            if (!hooks.settings().getAsJsonObject().entrySet().isEmpty()) {
                assertThat(hooks.errors()).isEmpty();
                final HookSettings hookUpdate = api().updateHookSettings(projectKey, repoKey, hook.details().key(), hooks);
                assertThat(hookUpdate.errors()).isEmpty();
            }
        }
    }

    @Test(dependsOnMethods = {testGetRepoKeyword})
    public void testGetHookSettingsOnNonExistentHookKey() {
        final HookSettings hookSettings = api().getHookSettings(projectKey, 
                repoKey, 
                TestUtilities.randomStringLettersOnly());
        assertThat(hookSettings).isNotNull();
        assertThat(hookSettings.errors()).isEmpty();
    }

    @Test(dependsOnMethods = {"testGetAndUpdateHookSettings"})
    public void testUpdateHookSettingsWithRandomData() {
        final String key = "Hello";
        final String randomValue = TestUtilities.randomString();
        final LinkedTreeMap settings = new LinkedTreeMap();
        settings.put(key, randomValue);
        final HookSettings updateHook = HookSettings.of(settings);
        final HookSettings hookSettings = api().updateHookSettings(projectKey,
                repoKey,
                foundHook.details().key(),
                updateHook);
        assertThat(hookSettings).isNotNull();
        assertThat(hookSettings.errors()).isEmpty();
        final String possibleValue = hookSettings
                .settings()
                .getAsJsonObject()
                .get(key)
                .getAsString();
        assertThat(possibleValue).isEqualTo(randomValue);

        final String randomValueAgain = TestUtilities.randomString();
        settings.put(key, randomValueAgain);
        final HookSettings hookSettingsAgain = HookSettings.of(settings);
        final HookSettings hookSettingsUpdated = api().updateHookSettings(projectKey,
                repoKey,
                foundHook.details().key(),
                hookSettingsAgain);
        final String possibleValueAgain = hookSettingsUpdated
                .settings()
                .getAsJsonObject()
                .get(key)
                .getAsString();
        assertThat(possibleValueAgain).isEqualTo(randomValueAgain);
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

    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private RepositoryApi api() {
        return api.repositoryApi();
    }
}
