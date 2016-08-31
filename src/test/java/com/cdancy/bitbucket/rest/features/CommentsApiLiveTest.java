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
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.comment.Parent;
import com.cdancy.bitbucket.rest.options.CreateComment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "CommentsApiLiveTest", singleThreaded = true)
public class CommentsApiLiveTest extends BaseBitbucketApiLiveTest {

    String project = "TEST";
    String repo = "dev";
    String commentText = randomString();
    String commentReplyText = randomString();
    int prId = -1;
    int commentId = -1;
    int commentIdVersion = -1;
    int commentReplyId = -1;
    int commentReplyIdVersion = -1;

    @Test
    public void testComment() {
        Comments comm = api().comment(project, repo, prId, commentText);
        assertNotNull(comm);
        assertTrue(comm.errors().size() == 0);
        assertTrue(comm.text().equals(commentText));
        commentId = comm.id();
        commentIdVersion = comm.version();
    }

    @Test (dependsOnMethods = "testComment")
    public void testCreateComment() {
        Parent parent = Parent.create(commentId);
        CreateComment createComment = CreateComment.create(commentReplyText, parent, null);

        Comments comm = api().create(project, repo, prId, createComment);
        assertNotNull(comm);
        assertTrue(comm.errors().size() == 0);
        assertTrue(comm.text().equals(commentReplyText));
        commentReplyId = comm.id();
        commentReplyIdVersion = comm.version();
    }

    @Test (dependsOnMethods = "testCreateComment")
    public void testGetComment() {
        Comments comm = api().get(project, repo, prId, commentReplyId);
        assertNotNull(comm);
        assertTrue(comm.errors().size() == 0);
        assertTrue(comm.text().equals(commentReplyText));
    }

    @Test (dependsOnMethods = "testGetComment")
    public void testDeleteComment() {
        boolean success = api().delete(project, repo, prId, commentReplyId, commentReplyIdVersion);
        assertTrue(success);
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
