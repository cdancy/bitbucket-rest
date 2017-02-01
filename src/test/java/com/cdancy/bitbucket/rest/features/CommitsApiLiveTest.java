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

import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "CommitsApiLiveTest", singleThreaded = true)
public class CommitsApiLiveTest extends BaseBitbucketApiLiveTest {

    String projectKey = "TEST";
    String repoKey = "dev";
    String commitHash = "d90ca08fa076e2e4c076592fce3832aba80a494f";

    @Test 
    public void testGetCommit() {
        Commit commit = api().get(projectKey, repoKey, commitHash, null);
        assertNotNull(commit);
        assertTrue(commit.errors().isEmpty());
        assertTrue(commit.id().equals(commitHash));
    }

    @Test
    public void testGetCommitNonExistent() {
        Commit commit = api().get(projectKey, repoKey, "1234567890", null);
        assertNotNull(commit);
        assertTrue(commit.errors().size() > 0);
    }

    private CommitsApi api() {
        return api.commitsApi();
    }
}
