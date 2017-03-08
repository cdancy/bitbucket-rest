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

import com.cdancy.bitbucket.rest.domain.pullrequest.MinimalRepository;
import com.cdancy.bitbucket.rest.domain.pullrequest.MergeStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ProjectKey;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.domain.pullrequest.Reference;

import com.cdancy.bitbucket.rest.options.CreatePullRequest;
import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequestPage;

@Test(groups = "live", testName = "PullRequestApiLiveTest", singleThreaded = true)
public class PullRequestApiLiveTest extends BaseBitbucketApiLiveTest {

    String project = "BUILD";
    String repo = "dancc-test";
    String branchToMerge = "TIGER";
    int prId = -1;
    int version = -1;

    @Test
    public void createGetPullRequest() {
        String randomChars = randomString();
        ProjectKey proj = ProjectKey.create(project);
        MinimalRepository repository = MinimalRepository.create(repo, null, proj);

        Reference fromRef = Reference.create("refs/heads/" + branchToMerge, repository, branchToMerge);
        Reference toRef = Reference.create(null, repository, null);
        CreatePullRequest cpr = CreatePullRequest.create(randomChars, "Fix for issue " + randomChars, fromRef, toRef, null, null);

        System.out.println("---------> CREATED PR: " + cpr);
        PullRequest pr = api().create(project, repo, cpr);
        assertThat(pr).isNotNull();
        assertThat(project.equals(pr.fromRef().repository().project().key())).isTrue();
        assertThat(repo.equals(pr.fromRef().repository().slug())).isTrue();
        prId = pr.id();
        version = pr.version();
    }

    @Test (dependsOnMethods = "createGetPullRequest")
    public void testGetPullRequest() {
        PullRequest pr = api().get(project, repo, prId);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isTrue();
        assertThat(project.equals(pr.fromRef().repository().project().key())).isTrue();
        assertThat(repo.equals(pr.fromRef().repository().slug())).isTrue();
        assertThat(version == pr.version()).isTrue();
    }

    @Test (dependsOnMethods = "createGetPullRequest")
    public void testListPullRequest() {
        PullRequestPage pr = api().list(project, repo, null, null, null, null, null, null, null, 10);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isTrue();
        assertThat(pr.values().size() > 0).isTrue();
    }

    @Test (dependsOnMethods = "testListPullRequest")
    public void testGetPullRequestChanges() {
        ChangePage pr = api().changes(project, repo, prId, null, null, null);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isTrue();
        assertThat(pr.values().size() > 0).isTrue();
    }

    @Test (dependsOnMethods = "testGetPullRequestChanges")
    public void testGetPullRequestCommits() {
        CommitPage pr = api().commits(project, repo, prId, true, 1, null);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isTrue();
        assertThat(pr.values().size() == 1).isTrue();
        assertThat(pr.totalCount() > 0).isTrue();
    }

    @Test (dependsOnMethods = "testGetPullRequestCommits")
    public void testDeclinePullRequest() {
        PullRequest pr = api().decline(project, repo, prId, version);
        assertThat(pr).isNotNull();
        assertThat("DECLINED".equalsIgnoreCase(pr.state())).isTrue();
        assertThat(pr.open()).isFalse();
    }

    @Test (dependsOnMethods = "testDeclinePullRequest")
    public void testReopenPullRequest() {
        PullRequest pr = api().get(project, repo, prId);
        pr = api().reopen(project, repo, prId, pr.version());
        assertThat(pr).isNotNull();
        assertThat("OPEN".equalsIgnoreCase(pr.state())).isTrue();
        assertThat(pr.open()).isTrue();
    }

    @Test (dependsOnMethods = "testReopenPullRequest")
    public void testCanMergePullRequest() {
        MergeStatus mergeStatus = api().canMerge(project, repo, prId);
        assertThat(mergeStatus).isNotNull();
        assertThat(mergeStatus.canMerge()).isTrue();
    }

    @Test (dependsOnMethods = "testCanMergePullRequest")
    public void testMergePullRequest() {
        PullRequest pr = api().get(project, repo, prId);
        pr = api().merge(project, repo, prId, pr.version());
        assertThat(pr).isNotNull();
        assertThat("MERGED".equalsIgnoreCase(pr.state())).isTrue();
        assertThat(pr.open()).isFalse();
    }

    @Test
    public void testGetNonExistentPullRequest() {
        PullRequest pr = api().get(randomString(), randomString(), 999);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isFalse();
    }

    private PullRequestApi api() {
        return api.pullRequestApi();
    }
}
