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
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.comment.Parent;
import com.cdancy.bitbucket.rest.domain.pullrequest.CommentPage;
import com.cdancy.bitbucket.rest.options.CreateComment;
import com.google.common.collect.Lists;
import java.util.List;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "CommentsApiLiveTest", singleThreaded = true)
public class CommentsApiLiveTest extends BaseBitbucketApiLiveTest {

    final String project = "DEV";
    final String repo = "test";
    final String commentText = randomString();
    final String commentReplyText = randomString();
    final String filePath = "some/file/path.java";
    final int prId = 6571;
    int commentId = -1;
    int commentIdVersion = -1;
    int commentReplyId = -1;
    int commentReplyIdVersion = -1;

    @Test
    public void testComment() {
        final Comments comm = api().comment(project, repo, prId, commentText);
        assertThat(comm).isNotNull();
        assertThat(comm.errors().isEmpty()).isTrue();
        assertThat(comm.text().equals(commentText)).isTrue();
        commentId = comm.id();
        commentIdVersion = comm.version();
    }

    @Test (dependsOnMethods = "testComment")
    public void testCreateComment() {
        final Parent parent = Parent.create(commentId);
        final CreateComment createComment = CreateComment.create(commentReplyText, parent, null);

        final Comments comm = api().create(project, repo, prId, createComment);
        assertThat(comm).isNotNull();
        assertThat(comm.errors().isEmpty()).isTrue();
        assertThat(comm.text().equals(commentReplyText)).isTrue();
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
    public void testGetFileCommentPage() throws Exception {
        final List<Comments> allComments = Lists.newArrayList();
        Integer start = null;
        while (true) {
            final CommentPage comm = api().fileComments(project, repo, prId, filePath, start, 100);
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
            if (comm.anchor() != null && comm.anchor().path().equalsIgnoreCase(filePath)) {
                foundComment = true;
                break;
            }
        }
        assertThat(foundComment).isTrue();
    }

    @Test (dependsOnMethods = "testGetFileCommentPage")
    public void testDeleteComment() {
        final boolean success = api().delete(project, repo, prId, commentReplyId, commentReplyIdVersion);
        assertThat(success).isTrue();
    }

    @AfterClass
    public void fin() {
        api().delete(project, repo, prId, commentReplyId, commentReplyIdVersion);
        api().delete(project, repo, prId, commentReplyId, commentIdVersion);
    }

    private CommentsApi api() {
        return api.commentsApi();
    }
}
