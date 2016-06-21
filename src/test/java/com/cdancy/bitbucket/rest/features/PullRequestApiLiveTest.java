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

import com.cdancy.bitbucket.rest.domain.pullrequest.*;
import com.cdancy.bitbucket.rest.options.CreatePullRequest;
import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;

@Test(groups = "live", testName = "PullRequestApiLiveTest", singleThreaded = true)
public class PullRequestApiLiveTest extends BaseBitbucketApiLiveTest {

    String project = "TEST";
    String repo = "dev";
    String branchToMerge = "lion";
    int prId = -1;
    int version = -1;

    @Test
    public void createGetPullRequest() {
        String randomChars = randomString();
        ProjectKey proj = ProjectKey.create(project);
        Repository repository = Repository.create(repo, null, proj);

        Reference fromRef = Reference.create("refs/heads/" + branchToMerge, repository);
        Reference toRef = Reference.create(null, repository);
        CreatePullRequest cpr = CreatePullRequest.create(randomChars, "Fix for issue " + randomChars, fromRef, toRef, null, null);
        PullRequest pr = api().create(project, repo, cpr);
        assertNotNull(pr);
        assertTrue(pr.fromRef().repository().project().key().equals(project));
        assertTrue(pr.fromRef().repository().slug().equals(repo));
        prId = pr.id();
        version = pr.version();
    }

    @Test (dependsOnMethods = "createGetPullRequest")
    public void testGetPullRequest() {
        PullRequest pr = api().get(project, repo, prId);
        assertNotNull(pr);
        assertTrue(pr.errors().size() == 0);
        assertTrue(pr.fromRef().repository().project().key().equals(project));
        assertTrue(pr.fromRef().repository().slug().equals(repo));
        assertTrue(version == pr.version());
    }

    @Test (dependsOnMethods = "testGetPullRequest")
    public void testDeclinePullRequest() {
        PullRequest pr = api().decline(project, repo, prId, version);
        assertNotNull(pr);
        assertTrue(pr.state().equalsIgnoreCase("DECLINED"));
        assertFalse(pr.open());
    }

    @Test (dependsOnMethods = "testDeclinePullRequest")
    public void testReopenPullRequest() {
        PullRequest pr = api().get(project, repo, prId);
        pr = api().reopen(project, repo, prId, pr.version());
        assertNotNull(pr);
        assertTrue(pr.state().equalsIgnoreCase("OPEN"));
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
        assertTrue(pr.state().equalsIgnoreCase("MERGED"));
        assertFalse(pr.open());
    }

    @Test
    public void testGetNonExistentPullRequest() {
        PullRequest pr = api().get(randomString(), randomString(), 999);
        assertNotNull(pr);
        assertTrue(pr.errors().size() > 0);
    }

    private PullRequestApi api() {
      return api.pullRequestApi();
    }
}
