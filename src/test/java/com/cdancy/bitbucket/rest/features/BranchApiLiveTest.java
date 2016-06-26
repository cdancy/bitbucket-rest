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
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.options.CreateBranch;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "BranchApiLiveTest", singleThreaded = true)
public class BranchApiLiveTest extends BaseBitbucketApiLiveTest {

    /*
    String projectKey = randomStringLettersOnly();
    String repoKey = randomStringLettersOnly();
    String tagName = randomStringLettersOnly();
    String commitHash = "";
    */

    String projectKey = "TEST";
    String repoKey = "dev";
    String branchName = randomStringLettersOnly();
    String commitHash = "d90ca08fa076e2e4c076592fce3832aba80a494f";

    String defaultBranchId = "refs/heads/master";

    @BeforeClass
    public void init() {
        Branch branch = api().getDefault(projectKey, repoKey);
        assertNotNull(branch);
        assertTrue(branch.errors().size() == 0);
        defaultBranchId = branch.id();
    }

    @Test
    public void testCreateBranch() {
        CreateBranch createBranch = CreateBranch.create(branchName, commitHash, null);
        Branch branch = api().create(projectKey, repoKey, createBranch);
        assertNotNull(branch);
        assertTrue(branch.errors().size() == 0);
        assertTrue(branch.id().endsWith(branchName));
        assertTrue(branch.latestChangeset().equalsIgnoreCase(commitHash));
    }

    @Test (dependsOnMethods = "testCreateBranch")
    public void testUpdateDefaultBranch() {
        boolean success = api().updateDefault(projectKey, repoKey, "refs/heads/" + branchName);
        assertTrue(success);
    }

    @Test (dependsOnMethods = "testUpdateDefaultBranch")
    public void testGetNewDefaultBranch() {
        Branch branch = api().getDefault(projectKey, repoKey);
        assertNotNull(branch);
        assertTrue(branch.errors().size() == 0);
        assertNotNull(branch.id());
    }

    @AfterClass
    public void fin() {
        boolean success = api().updateDefault(projectKey, repoKey, defaultBranchId);
        assertTrue(success);
        success = api().delete(projectKey, repoKey, "refs/heads/" + branchName);
        assertTrue(success);
    }

    private BranchApi api() {
        return api.branchApi();
    }
}
