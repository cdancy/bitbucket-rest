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
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.BranchPage;
import com.cdancy.bitbucket.rest.domain.comment.Anchor;
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.comment.Parent;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.Change;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.domain.pullrequest.CommentPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.MinimalRepository;
import com.cdancy.bitbucket.rest.domain.pullrequest.ProjectKey;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.domain.pullrequest.Reference;
import com.cdancy.bitbucket.rest.options.CreateComment;
import com.cdancy.bitbucket.rest.options.CreatePullRequest;
import com.google.common.collect.Lists;
import java.util.List;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "CommentsApiLiveTest", singleThreaded = true)
public class CommentsApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private String project;
    private String repo;
    private String filePath;
    private final String commentText = TestUtilities.randomString();
    private final String commentReplyText = TestUtilities.randomString();
    
    private int prId = -1;
    private int commentId = -1;
    private int commentReplyId = -1;
    private int commentReplyIdVersion = -1;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.credential, this.api);
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
    }
    
    @Test
    public void testComment() {
        final Comments comm = api().comment(project, repo, prId, commentText);
        assertThat(comm).isNotNull();
        assertThat(comm.errors().isEmpty()).isTrue();
        assertThat(comm.text()).isEqualTo(commentText);
        commentId = comm.id();
    }

    @Test (dependsOnMethods = "testComment")
    public void testCreateComment() {
        final Parent parent = Parent.create(commentId);
        final CreateComment createComment = CreateComment.create(commentReplyText, parent, null);

        final Comments comm = api().create(project, repo, prId, createComment);
        assertThat(comm).isNotNull();
        assertThat(comm.errors().isEmpty()).isTrue();
        assertThat(comm.text()).isEqualTo(commentReplyText);
        commentReplyId = comm.id();
        commentReplyIdVersion = comm.version();
    }
    
    @Test (dependsOnMethods = "testCreateComment")
    public void testGetComment() {
        final Comments comm = api().get(project, repo, prId, commentReplyId);
        assertThat(comm).isNotNull();
        assertThat(comm.errors().isEmpty()).isTrue();
        assertThat(comm.text().equals(commentReplyText)).isTrue();
    }
    
    @Test (dependsOnMethods = "testGetComment")
    public void testCreateInlineComment() {
        
        final ChangePage changePage = api.pullRequestApi().changes(project, repo, prId, null, null, null);
        assertThat(changePage).isNotNull();
        assertThat(changePage.errors().isEmpty()).isTrue();
        assertThat(changePage.values().size()).isEqualTo(1);
        final Change change = changePage.values().get(0);
        this.filePath = change.path()._toString();
        
        final Anchor anchor = Anchor.create(1, Anchor.LineType.CONTEXT, 
                        Anchor.FileType.FROM, 
                        this.filePath, 
                        this.filePath);
        
        final String randomText = TestUtilities.randomString();
        final CreateComment createComment = CreateComment.create(randomText, null, anchor);

        final Comments comm = api().create(project, repo, prId, createComment);
        assertThat(comm).isNotNull();
        assertThat(comm.errors().isEmpty()).isTrue();
        assertThat(comm.text()).isEqualTo(randomText);
    }
    
    @Test (dependsOnMethods = "testCreateInlineComment")
    public void testGetFileCommentPage() throws Exception {
        
        final List<Comments> allComments = Lists.newArrayList();
        Integer start = null;
        while (true) {
            final CommentPage comm = api().fileComments(project, repo, prId, this.filePath, start, 100);
            assertThat(comm.errors().isEmpty()).isTrue();
            allComments.addAll(comm.values());
            start = comm.nextPageStart();
            if (comm.isLastPage()) {
                break;
            } else {
                System.out.println("Sleeping for 1 seconds before querying for next page");
                Thread.sleep(1000);
            }
        }
        
        assertThat(allComments.isEmpty()).isFalse();
        boolean foundComment = false;
        for (final Comments comm : allComments) {
            if (comm.anchor() != null && comm.anchor().path().equalsIgnoreCase(this.filePath)) {
                foundComment = true;
                break;
            }
        }
        assertThat(foundComment).isTrue();
    }

    @Test (dependsOnMethods = "testGetFileCommentPage")
    public void testDeleteComment() {
        final RequestStatus success = api().delete(project, repo, prId, commentReplyId, commentReplyIdVersion);
        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();
    }

    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private CommentsApi api() {
        return api.commentsApi();
    }
}
