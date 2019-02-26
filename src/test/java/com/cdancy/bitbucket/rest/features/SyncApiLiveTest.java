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
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.sync.SyncState;
import com.cdancy.bitbucket.rest.domain.sync.SyncStatus;
import com.cdancy.bitbucket.rest.options.SyncOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "SyncApiLiveTest", singleThreaded = true)
public class SyncApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private String projectKey;
    private String repoKey;
    private String newProjectKey;
    private String newRepoKey = randomStringLettersOnly();

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
        this.newProjectKey = this.projectKey;

        final Repository repo = api.repositoryApi().fork(projectKey, repoKey, newProjectKey, newRepoKey);
        assertThat(repo).isNotNull();
        assertThat(repo.errors()).isEmpty();
        generatedTestContents.addRepoForDeletion(newProjectKey, newRepoKey);
    }
    
    @Test 
    public void testDisableSync() {
        final SyncStatus status = api().enable(newProjectKey, newRepoKey, false);
        assertThat(status).isNotNull();
        assertThat(status.available()).isTrue();
        assertThat(status.enabled()).isFalse();
        assertThat(status.divergedRefs()).isEmpty();
        assertThat(status.errors()).isEmpty();
    }

    @Test (dependsOnMethods = "testDisableSync")
    public void testEnableSync() {
        final SyncStatus status = api().enable(newProjectKey, newRepoKey, true);
        assertThat(status).isNotNull();
        assertThat(status.available()).isTrue();
        assertThat(status.enabled()).isTrue();
        assertThat(status.errors()).isEmpty();
    }

    @Test (dependsOnMethods = "testEnableSync")
    public void testSynchronzie() {
        final SyncState status = api().synchronize(newProjectKey, newRepoKey, SyncOptions.discard(null));
        assertThat(status).isNotNull();
        assertThat(status.errors()).isNotEmpty();

        // expected as the there is no code in the repo
        assertThat(status.errors().get(0).message().contains("cannot be synchronized"));
    }

    @Test (dependsOnMethods = "testSynchronzie")
    public void testGetSyncStatus() {
        final SyncStatus status = api().status(newProjectKey, newRepoKey, null);
        assertThat(status).isNotNull();
        assertThat(status.available()).isTrue();
        assertThat(status.enabled()).isTrue();
        assertThat(status.aheadRefs()).isEmpty();
        assertThat(status.divergedRefs()).isEmpty();
        assertThat(status.orphanedRefs()).isEmpty();
        assertThat(status.errors()).isEmpty();
    }

    @Test 
    public void testEnableSyncOnError() {
        final SyncStatus status = api().enable(newProjectKey, randomStringLettersOnly(), true);
        assertThat(status).isNotNull();
        assertThat(status.errors()).isNotEmpty();
    }

    @Test
    public void testSyncStatusOnError() {
        final SyncStatus status = api().status(newProjectKey, randomStringLettersOnly(), null);
        assertThat(status).isNotNull();
        assertThat(status.errors()).isNotEmpty();
    }

    @Test
    public void testSynchronizeOnError() {
        final SyncState status = api().synchronize(newProjectKey, randomStringLettersOnly(), SyncOptions.merge(null));
        assertThat(status).isNotNull();
        assertThat(status.errors()).isNotEmpty();
    }
    
    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private SyncApi api() {
        return api.syncApi();
    }
}
