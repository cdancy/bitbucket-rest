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
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.BranchPage;
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.comment.LikePage;
import com.cdancy.bitbucket.rest.domain.common.Reference;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.*;
import com.cdancy.bitbucket.rest.options.CreatePullRequest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "LikesApiLiveTest", singleThreaded = true)
public class LikesApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private String project;
    private String repo;
    private final String commentText = TestUtilities.randomString();

    private int prId = -1;
    private int commentId = -1;

    // base setup is identical to CommentsLiveTest
    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        this.project = generatedTestContents.project.key();
        this.repo = generatedTestContents.repository.name();

        final BranchPage branchPage = api.branchApi().list(project, repo, null, null, null, null, null, null);
        assertThat(branchPage).isNotNull();
        assertThat(branchPage.errors().isEmpty()).isTrue();
        assertThat(branchPage.values().size()).isEqualTo(2);

        String branchToMerge = null;
        for (final Branch branch : branchPage.values()) {
            if (!branch.id().endsWith("master")) {
                branchToMerge = branch.id();
                break;
            }
        }
        assertThat(branchToMerge).isNotNull();

        final String randomChars = TestUtilities.randomString();
        final ProjectKey proj = ProjectKey.create(project);
        final MinimalRepository repository = MinimalRepository.create(repo, null, proj);
        final Reference fromRef = Reference.create(branchToMerge, repository, branchToMerge);
        final Reference toRef = Reference.create(null, repository);
        final CreatePullRequest cpr = CreatePullRequest.create(randomChars, "Fix for issue " + randomChars, fromRef, toRef, null, null);
        final PullRequest pr = api.pullRequestApi().create(project, repo, cpr);

        assertThat(pr).isNotNull();
        assertThat(project).isEqualTo(pr.fromRef().repository().project().key());
        assertThat(repo).isEqualTo(pr.fromRef().repository().name());
        prId = pr.id();

        final Comments comm = api.commentsApi().comment(project, repo, prId, commentText);
        commentId = comm.id();
    }

    @Test ()
    public void testGetNoLikes() {
        final LikePage likePage = api().getLikes(project, repo, prId, commentId);
        assertThat(likePage).isNotNull();
        assertThat(likePage.errors().isEmpty()).isTrue();
        assertThat(likePage.values()).hasSize(0);
    }

    @Test (dependsOnMethods = "testGetNoLikes")
    public void testCreateLike() {
        final RequestStatus requestStatus = api().likeComment(project, repo, prId, commentId);
        assertThat(requestStatus).isNotNull();
        assertThat(requestStatus.errors().isEmpty()).isTrue();
        assertThat(requestStatus.value()).isEqualTo(true);
    }

    @Test (dependsOnMethods = "testCreateLike")
    public void testGetOneLike() {
        final LikePage likePage = api().getLikes(project, repo, prId, commentId);
        assertThat(likePage).isNotNull();
        assertThat(likePage.errors().isEmpty()).isTrue();
        assertThat(likePage.values()).hasSize(1);
        assertThat(likePage.values().get(0)).isNotNull(); // all contents here are based on the user running the action.
    }

    @Test (dependsOnMethods = "testGetOneLike")
    public void testUnlike() {
        final RequestStatus requestStatus = api().unlikeComment(project, repo, prId, commentId);
        assertThat(requestStatus).isNotNull();
        assertThat(requestStatus.errors().isEmpty()).isTrue();
        assertThat(requestStatus.value()).isEqualTo(true);
    }

    @Test (dependsOnMethods = "testUnlike")
    public void testGetNoLikesAfterUnlike() {
        final LikePage likePage = api().getLikes(project, repo, prId, commentId);
        assertThat(likePage).isNotNull();
        assertThat(likePage.errors().isEmpty()).isTrue();
        assertThat(likePage.values()).hasSize(0);
    }

    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private LikesApi api() {
        return api.likesApi();
    }
}
