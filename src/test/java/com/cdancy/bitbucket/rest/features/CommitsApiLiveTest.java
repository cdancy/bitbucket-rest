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
import com.cdancy.bitbucket.rest.domain.commit.Commit;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;

import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "CommitsApiLiveTest", singleThreaded = true)
public class CommitsApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private String projectKey;
    private String repoKey;
    private String commitHash;
    private CommitPage commitPage;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
    }
    
    @Test 
    public void testListCommits() {
        commitPage = api().list(projectKey, repoKey, true, null, null, null, null, null, null, 3, null);
        assertThat(commitPage).isNotNull();
        assertThat(commitPage.errors().isEmpty()).isTrue();
        assertThat(commitPage.values().isEmpty()).isFalse();
        assertThat(commitPage.totalCount() > 0).isTrue();
        this.commitHash = commitPage.values().get(0).id();
    }

    @Test (dependsOnMethods = "testListCommits")
    public void testListCommitsBetweenCommitHashes() {
        final String startHash = commitPage.values().get(2).id();
        final String endHash = commitPage.values().get(0).id();
        final CommitPage page = api().list(projectKey, repoKey, null, null, null, null, null, startHash, endHash, null, null);
        assertThat(page).isNotNull();
        assertThat((page.errors()).isEmpty()).isTrue();
        assertThat((page.values().size() == 2)).isTrue();
        assertThat(page.values().get(1).id().equals(commitPage.values().get(1).id())).isTrue();
    }

    @Test 
    public void testListCommitsOnError() {
        final CommitPage pr = api().list(projectKey, TestUtilities.randomStringLettersOnly(), true, null, null, null, null, null, null, 1, null);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isFalse();
        assertThat(pr.values().isEmpty()).isTrue();
    }
    
    @Test (dependsOnMethods = "testListCommits")
    public void testGetCommit() {
        final Commit commit = api().get(projectKey, repoKey, commitHash, null);
        assertThat(commit).isNotNull();
        assertThat(commit.errors().isEmpty()).isTrue();
        assertThat(commit.id().equals(commitHash)).isTrue();
    }

    @Test
    public void testGetCommitNonExistent() {
        final Commit commit = api().get(projectKey, repoKey, "1234567890", null);
        assertThat(commit).isNotNull();
        assertThat(commit.errors().size() > 0).isTrue();
    }

    @Test (dependsOnMethods = "testListCommits")
    public void testGetCommitChanges() {
        final ChangePage commit = api().listChanges(projectKey, repoKey, commitHash, 100, 0);
        assertThat(commit).isNotNull();
        assertThat(commit.errors().isEmpty()).isTrue();
        assertThat(commit.size() > 0).isTrue();
    }

    @Test
    public void testGetCommitChangesNonExistent() {
        final ChangePage commit = api().listChanges(projectKey, repoKey, "1234567890", 100, 0);
        assertThat(commit).isNotNull();
        assertThat(commit.size() == 0).isTrue();
        assertThat(commit.errors().size() > 0).isTrue();
    }
    
    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private CommitsApi api() {
        return api.commitsApi();
    }
}
