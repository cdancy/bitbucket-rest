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

package com.cdancy.bitbucket.rest;

import java.io.Closeable;

import com.cdancy.bitbucket.rest.features.*;
import org.jclouds.rest.annotations.Delegate;

public interface BitbucketApi extends Closeable {

    @Delegate
    AdminApi adminApi();

    @Delegate
    BranchApi branchApi();

    @Delegate
    BuildStatusApi buildStatusApi();

    @Delegate
    CommentsApi commentsApi();

    @Delegate
    CommitsApi commitsApi();

    @Delegate
    CompareApi compareApi();

    @Delegate
    DefaultReviewersApi defaultReviewersApi();

    @Delegate
    FileApi fileApi();

    @Delegate
    HookApi hookApi();

    @Delegate
    WebHookApi webHookApi();

    @Delegate
    ProjectApi projectApi();

    @Delegate
    PullRequestApi pullRequestApi();

    @Delegate
    RepositoryApi repositoryApi();

    @Delegate
    SyncApi syncApi();

    @Delegate
    SystemApi systemApi();

    @Delegate
    TagApi tagApi();

    @Delegate
    TasksApi tasksApi();

    @Delegate
    InsightsApi insightsApi();

    @Delegate
    KeysApi keysApi();

    @Delegate
    SearchApi searchApi();

    @Delegate
    LabelsApi labelsApi();

    @Delegate
    PostWebHookApi postWebHookApi();

    @Delegate
    LikesApi likesApi();
}
