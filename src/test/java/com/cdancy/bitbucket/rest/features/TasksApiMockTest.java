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

import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.domain.comment.MinimalAnchor;
import com.cdancy.bitbucket.rest.domain.comment.Task;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.options.CreateTask;
import com.cdancy.bitbucket.rest.options.UpdateTask;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mock tests for the {@link TasksApi} class.
 */
@Test(groups = "unit", testName = "TasksApiMockTest")
public class TasksApiMockTest extends BaseBitbucketMockTest {

    private final String tasksEndpoint = "/rest/api/1.0/tasks";
    private final int taskId = 99;
    private static final String TASK_RESOLVED = "RESOLVED";
    private static final String TASK_OPEN = "OPEN";

    public void testCreateTask() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/task.json")).setResponseCode(201));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TasksApi api = baseApi.tasksApi();
        try {

            final String taskComment = "Resolve the merge conflicts";
            final CreateTask createTask = CreateTask.create(1, taskComment);
            final Task task = api.create(createTask);
            assertThat(task).isNotNull();
            assertThat(task.errors().isEmpty()).isTrue();
            assertThat(task.text()).isEqualTo(taskComment);
            assertThat(task.anchor().id()).isEqualTo(1);
            assertSent(server, "POST", tasksEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testCreateTaskOnErrors() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TasksApi api = baseApi.tasksApi();
        try {

            final CreateTask createTask = CreateTask.create(1, "Hello, World");
            final Task task = api.create(createTask);
            assertThat(task).isNotNull();
            assertThat(task.errors().isEmpty()).isFalse();
            assertThat(task.anchor()).isNull();
            assertSent(server, "POST", tasksEndpoint);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testGetTask() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/task.json")).setResponseCode(200));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TasksApi api = baseApi.tasksApi();
        try {

            final Task task = api.get(taskId);
            assertThat(task).isNotNull();
            assertThat(task.errors().isEmpty()).isTrue();
            assertThat(task.id()).isEqualTo(taskId);
            assertSent(server, "GET", tasksEndpoint + "/" +  taskId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testResolveTask() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/task-resolved.json")).setResponseCode(201));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TasksApi api = baseApi.tasksApi();
        try {

            final MinimalAnchor anchor = MinimalAnchor.create(1, "COMMENT");
            final UpdateTask updateTaskResolved = UpdateTask.update(anchor, taskId, TASK_RESOLVED, 1, 1);

            final Task instanceNowResolved = api.update(updateTaskResolved.id(), updateTaskResolved);
            assertThat(instanceNowResolved).isNotNull();
            assertThat(instanceNowResolved.errors().isEmpty()).isTrue();
            assertThat(instanceNowResolved.state()).isEqualTo(TASK_RESOLVED);

            String json = getTaskUpdateRequestPayload(taskId, TASK_RESOLVED);
            assertSent(server, "PUT", tasksEndpoint + "/" + taskId, json);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testOpenTask() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/task-open.json")).setResponseCode(201));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TasksApi api = baseApi.tasksApi();
        try {

            final MinimalAnchor anchor = MinimalAnchor.create(1, "COMMENT");
            final UpdateTask updateTaskOpen = UpdateTask.update(anchor, taskId, TASK_OPEN, 1, 1);

            final Task instanceNowOpen = api.update(updateTaskOpen.id(), updateTaskOpen);
            assertThat(instanceNowOpen).isNotNull();
            assertThat(instanceNowOpen.errors().isEmpty()).isTrue();
            assertThat(instanceNowOpen.state()).isEqualTo(TASK_OPEN);

            String json = getTaskUpdateRequestPayload(taskId, TASK_OPEN);
            assertSent(server, "PUT", tasksEndpoint + "/" + taskId, json);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testGetTaskOnErrors() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TasksApi api = baseApi.tasksApi();
        try {
            
            final Task task = api.get(taskId);
            assertThat(task).isNotNull();
            assertThat(task.errors().isEmpty()).isFalse();
            assertThat(task.anchor()).isNull();
            assertSent(server, "GET", tasksEndpoint + "/" + taskId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testDeleteTask() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TasksApi api = baseApi.tasksApi();
        try {

            final RequestStatus task = api.delete(taskId);
            assertThat(task).isNotNull();
            assertThat(task.errors().isEmpty()).isTrue();
            assertThat(task.value()).isTrue();
            assertSent(server, "DELETE", tasksEndpoint + "/" + taskId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
    
    public void testDeleteTaskOnErrors() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(404));
        final BitbucketApi baseApi = api(server.getUrl("/"));
        final TasksApi api = baseApi.tasksApi();
        try {
            
            final RequestStatus task = api.delete(taskId);
            assertThat(task).isNotNull();
            assertThat(task.value()).isFalse();
            assertThat(task.errors().isEmpty()).isFalse();
            assertSent(server, "DELETE", tasksEndpoint + "/" + taskId);
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    private String getTaskUpdateRequestPayload(int taskId, String state) {
        return String.format(
            "{\"anchor\":{\"id\": 1,\"type\":\"COMMENT\"},\"id\":%s,\"state\":\"%s\",\"pullRequestId\":1,\"repositoryId\":1}",
            taskId, state
        );
    }
}
