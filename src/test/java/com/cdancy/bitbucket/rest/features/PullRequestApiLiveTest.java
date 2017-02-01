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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

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

        Reference fromRef = Reference.create("refs/heads/" + branchToMerge, repository);
        Reference toRef = Reference.create(null, repository);
        CreatePullRequest cpr = CreatePullRequest.create(randomChars, "Fix for issue " + randomChars, fromRef, toRef, null, null);
        
        System.out.println("---------> CREATED PR: " + cpr);
        PullRequest pr = api().create(project, repo, cpr);
        assertNotNull(pr);
        assertTrue(project.equals(pr.fromRef().repository().project().key()));
        assertTrue(repo.equals(pr.fromRef().repository().slug()));
        prId = pr.id();
        version = pr.version();
    }

    @Test (dependsOnMethods = "createGetPullRequest")
    public void testGetPullRequest() {
        PullRequest pr = api().get(project, repo, prId);
        assertNotNull(pr);
        assertTrue(pr.errors().isEmpty());
        assertTrue(project.equals(pr.fromRef().repository().project().key()));
        assertTrue(repo.equals(pr.fromRef().repository().slug()));
        assertTrue(version == pr.version());
    }

    @Test (dependsOnMethods = "testGetPullRequest")
    public void testGetPullRequestChanges() {
        ChangePage pr = api().changes(project, repo, prId, null, null, null);
        assertNotNull(pr);
        assertTrue(pr.errors().isEmpty());
        assertTrue(pr.values().size() > 0);
    }

    @Test (dependsOnMethods = "testGetPullRequestChanges")
    public void testGetPullRequestCommits() {
        CommitPage pr = api().commits(project, repo, prId, true, 1, null);
        assertNotNull(pr);
        assertTrue(pr.errors().isEmpty());
        assertTrue(pr.values().size() == 1);
        assertTrue(pr.totalCount() > 0);
    }

    @Test (dependsOnMethods = "testGetPullRequestCommits")
    public void testDeclinePullRequest() {
        PullRequest pr = api().decline(project, repo, prId, version);
        assertNotNull(pr);
        assertTrue("DECLINED".equalsIgnoreCase(pr.state()));
        assertFalse(pr.open());
    }

    @Test (dependsOnMethods = "testDeclinePullRequest")
    public void testReopenPullRequest() {
        PullRequest pr = api().get(project, repo, prId);
        pr = api().reopen(project, repo, prId, pr.version());
        assertNotNull(pr);
        assertTrue("OPEN".equalsIgnoreCase(pr.state()));
        assertTrue(pr.open());
    }

    @Test (dependsOnMethods = "testReopenPullRequest")
    public void testCanMergePullRequest() {
        MergeStatus mergeStatus = api().canMerge(project, repo, prId);
        assertNotNull(mergeStatus);
        assertTrue(mergeStatus.canMerge());
    }

    @Test (dependsOnMethods = "testCanMergePullRequest")
    public void testMergePullRequest() {
        PullRequest pr = api().get(project, repo, prId);
        pr = api().merge(project, repo, prId, pr.version());
        assertNotNull(pr);
        assertTrue("MERGED".equalsIgnoreCase(pr.state()));
        assertFalse(pr.open());
    }

    @Test
    public void testGetNonExistentPullRequest() {
        PullRequest pr = api().get(randomString(), randomString(), 999);
        assertNotNull(pr);
        assertFalse(pr.errors().isEmpty());
    }

    private PullRequestApi api() {
        return api.pullRequestApi();
    }
}
