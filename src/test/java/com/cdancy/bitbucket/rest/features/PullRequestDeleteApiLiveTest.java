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
import com.cdancy.bitbucket.rest.domain.pullrequest.ProjectKey;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.domain.pullrequest.Reference;

import com.cdancy.bitbucket.rest.options.CreatePullRequest;
import com.cdancy.bitbucket.rest.options.DeletePullRequest;
import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.BranchPage;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;

@Test(groups = "live", testName = "PullRequestDeleteApiLiveTest", singleThreaded = true)
public class PullRequestDeleteApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private String project;
    private String repo;
    private String branchForPrDelete;
    private int prId = -1;
    private int version = -1;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        this.project = generatedTestContents.project.key();
        this.repo = generatedTestContents.repository.name();

        final BranchPage branchPage = api.branchApi().list(project, repo, null, null, null, null, null, null);
        assertThat(branchPage).isNotNull();
        assertThat(branchPage.errors().isEmpty()).isTrue();
        assertThat(branchPage.values().size()).isEqualTo(2);

        for (final Branch branch : branchPage.values()) {
            if (!branch.id().endsWith("master")) {
                this.branchForPrDelete = branch.id();
                break;
            }
        }

        assertThat(branchForPrDelete).isNotNull();
    }

    @Test
    public void createPullRequest() {
        final String randomChars = TestUtilities.randomString();
        final ProjectKey proj = ProjectKey.create(project);
        final MinimalRepository repository = MinimalRepository.create(repo, null, proj);
        final Reference fromRef = Reference.create(branchForPrDelete, repository, branchForPrDelete);
        final Reference toRef = Reference.create(null, repository);
        final CreatePullRequest cpr = CreatePullRequest.create(randomChars, "Fix for issue " + randomChars, fromRef, toRef, null, null);
        final PullRequest pr = api().create(project, repo, cpr);
        assertThat(pr).isNotNull();
        assertThat(project.equals(pr.fromRef().repository().project().key())).isTrue();
        assertThat(repo.equals(pr.fromRef().repository().name())).isTrue();
        prId = pr.id();
        version = pr.version();
    }

    @Test (dependsOnMethods = "createPullRequest")
    public void testDeleteBadVersionOfPullRequest() {
        final DeletePullRequest deletePullRequest = DeletePullRequest.create(9999);
        final RequestStatus success = api().delete(project, repo, prId, deletePullRequest);
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @Test (dependsOnMethods = "testDeleteBadVersionOfPullRequest")
    public void testDeleteNonExistentPullRequest() {
        final DeletePullRequest deletePullRequest = DeletePullRequest.create(version);
        final RequestStatus success = api().delete(project, repo, 9999, deletePullRequest);
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @Test (dependsOnMethods = "testDeleteNonExistentPullRequest")
    public void testDeletePullRequest() {
        final DeletePullRequest deletePullRequest = DeletePullRequest.create(version);
        final RequestStatus success = api().delete(project, repo, prId, deletePullRequest);
        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();
    }

    @AfterClass
    public void fin() throws IOException {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private PullRequestApi api() {
        return api.pullRequestApi();
    }

}
