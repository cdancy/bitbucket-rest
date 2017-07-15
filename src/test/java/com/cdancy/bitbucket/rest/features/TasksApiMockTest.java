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

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.domain.comment.Task;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateTask;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

/**
 * Mock tests for the {@link TasksApi} class.
 */
@Test(groups = "unit", testName = "TasksApiMockTest")
public class TasksApiMockTest extends BaseBitbucketMockTest {

    public void testCreateTask() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/task.json")).setResponseCode(201));
        BitbucketApi baseApi = api(server.getUrl("/"));
        TasksApi api = baseApi.tasksApi();
        try {

            final String taskComment = "Resolve the merge conflicts";
            final CreateTask createTask = CreateTask.create(1, taskComment);
            final Task task = api.create(createTask);
            assertThat(task).isNotNull();
            assertThat(task.errors().isEmpty()).isTrue();
            assertThat(task.text()).isEqualTo(taskComment);
            assertThat(task.anchor().id()).isEqualTo(1);
            assertSent(server, "POST", "/rest/api/1.0/tasks");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testCreateTaskOnErrors() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        TasksApi api = baseApi.tasksApi();
        try {

            final CreateTask createTask = CreateTask.create(1, "Hello, World");
            final Task task = api.create(createTask);
            assertThat(task).isNotNull();
            assertThat(task.errors().isEmpty()).isFalse();
            assertThat(task.anchor()).isNull();
            assertSent(server, "POST", "/rest/api/1.0/tasks");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testGetTask() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/task.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        TasksApi api = baseApi.tasksApi();
        try {

            final int taskId = 99;
            final Task task = api.get(taskId);
            assertThat(task).isNotNull();
            assertThat(task.errors().isEmpty()).isTrue();
            assertThat(task.id()).isEqualTo(taskId);
            assertSent(server, "GET", "/rest/api/1.0/tasks/" + taskId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testGetTaskOnErrors() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        TasksApi api = baseApi.tasksApi();
        try {
            
            final int taskId = 99;
            final Task task = api.get(taskId);
            assertThat(task).isNotNull();
            assertThat(task.errors().isEmpty()).isFalse();
            assertThat(task.anchor()).isNull();
            assertSent(server, "GET", "/rest/api/1.0/tasks/" + taskId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
