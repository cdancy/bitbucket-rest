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
import com.cdancy.bitbucket.rest.domain.commit.Commit;

import static org.assertj.core.api.Assertions.assertThat;

import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "CommitsApiLiveTest", singleThreaded = true)
public class CommitsApiLiveTest extends BaseBitbucketApiLiveTest {

    private final String projectKey = "TEST";
    private final String repoKey = "dev";
    private final String commitHash = "d90ca08fa076e2e4c076592fce3832aba80a494f";

    @Test
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

    @Test
    public void testGetCommitChanges() {
        final ChangePage commit = api().listChanges(projectKey, repoKey, commitHash, 0, 100);
        assertThat(commit).isNotNull();
        assertThat(commit.errors().isEmpty()).isTrue();
        assertThat(commit.size() > 0).isTrue();
    }

    @Test
    public void testGetCommitChangesNonExistent() {
        final ChangePage commit = api().listChanges(projectKey, repoKey, "1234567890", 0, 100);
        assertThat(commit).isNotNull();
        assertThat(commit.size() == 0).isTrue();
        assertThat(commit.errors().size() > 0).isTrue();
    }

    private CommitsApi api() {
        return api.commitsApi();
    }
}
