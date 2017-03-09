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

    String project = "DEV";
    String repo = "test";
    String commentText = randomString();
    String commentReplyText = randomString();
    String filePath = "some/file/path.java";
    int prId = 6571;
    int commentId = -1;
    int commentIdVersion = -1;
    int commentReplyId = -1;
    int commentReplyIdVersion = -1;

    @Test
    public void testComment() {
        Comments comm = api().comment(project, repo, prId, commentText);
        assertThat(comm).isNotNull();
        assertThat(comm.errors().isEmpty()).isTrue();
        assertThat(comm.text().equals(commentText)).isTrue();
        commentId = comm.id();
        commentIdVersion = comm.version();
    }

    @Test (dependsOnMethods = "testComment")
    public void testCreateComment() {
        Parent parent = Parent.create(commentId);
        CreateComment createComment = CreateComment.create(commentReplyText, parent, null);

        Comments comm = api().create(project, repo, prId, createComment);
        assertThat(comm).isNotNull();
        assertThat(comm.errors().isEmpty()).isTrue();
        assertThat(comm.text().equals(commentReplyText)).isTrue();
        commentReplyId = comm.id();
        commentReplyIdVersion = comm.version();
    }

    @Test (dependsOnMethods = "testCreateComment")
    public void testGetComment() {
        Comments comm = api().get(project, repo, prId, commentReplyId);
        assertThat(comm).isNotNull();
        assertThat(comm.errors().isEmpty()).isTrue();
        assertThat(comm.text().equals(commentReplyText)).isTrue();
    }
    
    @Test (dependsOnMethods = "testGetComment")
    public void testGetFileCommentPage() throws Exception {
        List<Comments> allComments = Lists.newArrayList();
        Integer start = null;
        while (true) {
            CommentPage comm = api().fileComments(project, repo, prId, filePath, start, 100);
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
        for (Comments comm : allComments) {
            if (comm.anchor() != null) {
                if (comm.anchor().path().equalsIgnoreCase(filePath)) {
                    foundComment = true;
                    break;
                }
            }
        }
        assertThat(foundComment).isTrue();
    }

    @Test (dependsOnMethods = "testGetFileCommentPage")
    public void testDeleteComment() {
        boolean success = api().delete(project, repo, prId, commentReplyId, commentReplyIdVersion);
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
