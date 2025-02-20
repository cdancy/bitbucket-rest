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
import com.cdancy.bitbucket.rest.domain.build.Status;
import com.cdancy.bitbucket.rest.domain.build.StatusPage;
import com.cdancy.bitbucket.rest.domain.build.Summary;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.options.CreateBuildStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import org.testng.annotations.BeforeClass;

@Test(groups = "live", testName = "BuildStatusApiLiveTest")
public class BuildStatusApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private String commitHash;
    
    private final CreateBuildStatus.STATE state = CreateBuildStatus.STATE.SUCCESSFUL;
    private final String key = "REPO-MASTER";
    private final String name = "REPO-MASTER-42";
    private final String url = "https://bamboo.example.com/browse/REPO-MASTER-42";
    private final String description = "Changes by John Doe";

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        final String projectKey = generatedTestContents.project.key();
        final String repoKey = generatedTestContents.repository.name();
        
        final CommitPage commitPage = api.commitsApi().list(projectKey, repoKey, true, null, null, null, null, null, null, 1, null);
        assertThat(commitPage).isNotNull();
        assertThat(commitPage.errors().isEmpty()).isTrue();
        assertThat(commitPage.values().isEmpty()).isFalse();
        assertThat(commitPage.totalCount() > 0).isTrue();
        this.commitHash = commitPage.values().get(0).id();
    }
    
    @Test 
    public void testAddStatusToCommit() {
        final CreateBuildStatus cbs = CreateBuildStatus.create(state, 
                        key, 
                        name, 
                        url, 
                        description);
        final RequestStatus success = api().add(commitHash, cbs);
        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();
    }
    
    @Test (dependsOnMethods = "testAddStatusToCommit")
    public void testGetStatusByCommit() {

        final StatusPage statusPage = api().status(commitHash, 0, 100);
        assertThat(statusPage).isNotNull();
        assertThat(statusPage.size() == 1).isTrue();
        
        final Status status = statusPage.values().get(0);
        assertThat(status.state().toString()).isEqualTo(state.toString());
        assertThat(status.key()).isEqualTo(key);
        assertThat(status.name()).isEqualTo(name);
        assertThat(status.url()).isEqualTo(url);
        assertThat(status.description()).isEqualTo(description);
    }
    
    @Test
    public void testGetStatusByNonExistentCommit() {

        final StatusPage statusPage = api().status(TestUtilities.randomString(), 0, 100);
        assertThat(statusPage).isNotNull();
        assertThat(statusPage.size() == 0).isTrue();
    }

    @Test (dependsOnMethods = "testGetStatusByCommit")
    public void testGetSummaryByCommit() {

        final Summary summary = api().summary(commitHash);
        assertThat(summary).isNotNull();
        assertThat(summary.successful() == 1).isTrue();
        assertThat(summary.inProgress() == 0).isTrue();
        assertThat(summary.failed() == 0).isTrue();
        assertThat(summary.cancelled() == 0).isTrue();
        assertThat(summary.unknown() == 0).isTrue();
    }
    
    @Test
    public void testGetSummaryByNonExistentCommit() {

        final Summary summary = api().summary(TestUtilities.randomString());
        assertThat(summary).isNotNull();
        assertThat(summary.successful() == 0).isTrue();
        assertThat(summary.inProgress() == 0).isTrue();
        assertThat(summary.failed() == 0).isTrue();
        assertThat(summary.cancelled() == 0).isTrue();
        assertThat(summary.unknown() == 0).isTrue();
    }

    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }
    
    private BuildStatusApi api() {
        return api.buildStatusApi();
    }
}
