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

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.branch.Matcher;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateCondition;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mock tests for the {@link CommitsApi} class.
 */
@Test(groups = "unit", testName = "DefaultReviewersApiMockTest")
public class DefaultReviewersApiMockTest extends BaseBitbucketMockTest {

    private final String projectsPath = "/projects/";
    private final String reposPath = "/repos/";
    private final String defaultReviewersPath = "/rest/default-reviewers/";
    private final String normalKeyword = "NORMAL";
    private final String testEmail = "test@test.com";
    
    private final String projectKey = "test";
    private final String repoKey = "1234";
            
    public void testListConditions() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/default-reviwers-list.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final List<Condition> conditions = baseApi.defaultReviewersApi().listConditions(projectKey, repoKey);
            assertThat(conditions).isNotNull();
            assertThat(conditions.size()).isEqualTo(3);

            assertSent(server, "GET", defaultReviewersPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + "/conditions");
        } finally {
            server.shutdown();
        }
    }

    public void testCreateCondition() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/default-reviwers-create.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final Long requiredApprover = 1L;
            final Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
            final Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
            final List<User> listUser = new ArrayList<>();
            listUser.add(User.create(projectKey, testEmail, 1, projectKey, true, projectKey, normalKeyword));
            final CreateCondition condition = CreateCondition.create(null, matcherSrc, matcherDst, listUser, requiredApprover);

            final Condition returnCondition = baseApi.defaultReviewersApi().createCondition(projectKey, repoKey, condition);
            assertThat(returnCondition).isNotNull();
            assertThat(returnCondition.errors()).isEmpty();
            assertThat(returnCondition.id()).isEqualTo(3L);

            assertSent(server, "POST", defaultReviewersPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + "/condition");
        } finally {
            server.shutdown();
        }
    }

    public void testCreateConditionOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final Long requiredApprover = 1L;
            final Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
            final Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
            final List<User> listUser = new ArrayList<>();
            listUser.add(User.create(projectKey, testEmail, 1, projectKey, true, projectKey, normalKeyword));
            final CreateCondition condition = CreateCondition.create(null, matcherSrc, matcherDst, listUser, requiredApprover);

            final Condition returnCondition = baseApi.defaultReviewersApi().createCondition(projectKey, "123456", condition);
            assertThat(returnCondition).isNotNull();
            assertThat(returnCondition.errors()).isNotEmpty();
            assertThat(returnCondition.errors().size()).isEqualTo(1);

            assertSent(server, "POST", defaultReviewersPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + "/repos/123456/condition");
        } finally {
            server.shutdown();
        }
    }

    public void testUpdateCondition() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/default-reviwers-create.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final Long requiredApprover = 1L;
            final Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
            final Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
            final List<User> listUser = new ArrayList<>();
            listUser.add(User.create(projectKey, testEmail, 1, projectKey, true, projectKey, normalKeyword));
            final CreateCondition condition = CreateCondition.create(10L, matcherSrc, matcherDst, listUser, requiredApprover);

            final Condition returnCondition = baseApi.defaultReviewersApi().updateCondition(projectKey, repoKey, 10L, condition);
            assertThat(returnCondition).isNotNull();
            assertThat(returnCondition.errors()).isEmpty();
            assertThat(returnCondition.id()).isEqualTo(3L);

            assertSent(server, "PUT", defaultReviewersPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + "/condition/10");
        } finally {
            server.shutdown();
        }
    }

    public void testUpdateConditionOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
            
            final Long requiredApprover = 1L;
            final Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
            final Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
            final List<User> listUser = new ArrayList<>();
            listUser.add(User.create(projectKey, testEmail, 1, projectKey, true, projectKey, normalKeyword));
            final CreateCondition condition = CreateCondition.create(10L, matcherSrc, matcherDst, listUser, requiredApprover);

            final Condition returnCondition = baseApi.defaultReviewersApi().updateCondition(projectKey, "123456", 10L, condition);
            assertThat(returnCondition).isNotNull();
            assertThat(returnCondition.errors()).isNotEmpty();
            assertThat(returnCondition.errors().size()).isEqualTo(1);

            assertSent(server, "PUT", defaultReviewersPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + "/repos/123456/condition/10");
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteCondition() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final RequestStatus success = baseApi.defaultReviewersApi().deleteCondition(projectKey, repoKey, 10L);
            assertThat(success).isNotNull();
            assertThat(success.value()).isTrue();
            assertThat(success.errors()).isEmpty();
            assertSent(server, "DELETE", defaultReviewersPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + "/condition/10");
        } finally {
            server.shutdown();
        }
    }

    public void testDeleteConditionOnError() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        try (final BitbucketApi baseApi = api(server.getUrl("/"))) {

            final RequestStatus success = baseApi.defaultReviewersApi().deleteCondition(projectKey, repoKey, 10L);
            assertThat(success).isNotNull();
            assertThat(success.value()).isFalse();
            assertThat(success.errors()).isNotEmpty();
            assertSent(server, "DELETE", defaultReviewersPath + BitbucketApiMetadata.API_VERSION
                    + projectsPath + projectKey + reposPath + repoKey + "/condition/10");
        } finally {
            server.shutdown();
        }
    }
}
