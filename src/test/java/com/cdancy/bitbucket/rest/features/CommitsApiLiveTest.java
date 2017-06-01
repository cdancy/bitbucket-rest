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

    @BeforeClass
    public void init() {
        generatedTestContents = initGeneratedTestContents();
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
    }
    
    @Test 
    public void testListCommits() {
        CommitPage commitPage = api().list(projectKey, repoKey, true, 1, null);
        assertThat(commitPage).isNotNull();
        assertThat(commitPage.errors().isEmpty()).isTrue();
        assertThat(commitPage.values().isEmpty()).isFalse();
        assertThat(commitPage.totalCount() > 0).isTrue();
        this.commitHash = commitPage.values().get(0).id();
    }
    
    @Test 
    public void testListCommitsOnError() {
        CommitPage pr = api().list(projectKey, randomStringLettersOnly(), true, 1, null);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isFalse();
        assertThat(pr.values().isEmpty()).isTrue();
    }
    
    @Test (dependsOnMethods = "testListCommits")
    public void testGetCommit() {
        Commit commit = api().get(projectKey, repoKey, commitHash, null);
        assertThat(commit).isNotNull();
        assertThat(commit.errors().isEmpty()).isTrue();
        assertThat(commit.id().equals(commitHash)).isTrue();
    }

    @Test
    public void testGetCommitNonExistent() {
        Commit commit = api().get(projectKey, repoKey, "1234567890", null);
        assertThat(commit).isNotNull();
        assertThat(commit.errors().size() > 0).isTrue();
    }

    @Test (dependsOnMethods = "testListCommits")
    public void testGetCommitChanges() {
        ChangePage commit = api().listChanges(projectKey, repoKey, commitHash, 0, 100);
        assertThat(commit).isNotNull();
        assertThat(commit.errors().isEmpty()).isTrue();
        assertThat(commit.size() > 0).isTrue();
    }

    @Test
    public void testGetCommitChangesNonExistent() {
        ChangePage commit = api().listChanges(projectKey, repoKey, "1234567890", 0, 100);
        assertThat(commit).isNotNull();
        assertThat(commit.size() == 0).isTrue();
        assertThat(commit.errors().size() > 0).isTrue();
    }
    
    @AfterClass
    public void fin() {
        terminateGeneratedTestContents(generatedTestContents);
    }

    private CommitsApi api() {
        return api.commitsApi();
    }
}
