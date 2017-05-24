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
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.options.CreateCondition;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jdoire on 24/05/2017.
 */
@Test(groups = "unit", testName = "DefaultReviewersApiMockTest")
public class DefaultReviewersApiMockTest extends BaseBitbucketMockTest {

    public void testListConditions() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/default-reviwers-list.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        DefaultReviewersApi api = baseApi.defaultReviewersApi();
        try {

            String projectKey = "test";
            String repoKey = "1234";

            List<Condition> conditions = api.listConditions(projectKey, repoKey);
            assertThat(conditions).isNotNull();
            assertThat(conditions.size()).isEqualTo(3);

            assertSent(server, "GET", "/rest/default-reviewers/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/conditions/");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateCondition() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/default-reviwers-create.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        DefaultReviewersApi api = baseApi.defaultReviewersApi();
        try {
            Long requiredApprover = 1L;
            Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
            Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
            List<User> listUser = new ArrayList<>();
            listUser.add(User.create("test", "test@test.com", 1, "test", true, "test", "NORMAL"));
            Repository repository = Repository.create(null, -1, null, null, null, null, false, null, false, null, null);
            CreateCondition condition = CreateCondition.create(null, repository, matcherSrc, matcherDst, listUser, requiredApprover);

            String projectKey = "test";
            String repoKey = "1234";

            Condition returnCondition = api.createCondition(projectKey, repoKey, condition);
            assertThat(returnCondition).isNotNull();
            assertThat(returnCondition.errors()).isEmpty();
            assertThat(returnCondition.id()).isEqualTo(10);

            assertSent(server, "POST", "/rest/default-reviewers/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/" + repoKey + "/condition/");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testCreateConditionOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        DefaultReviewersApi api = baseApi.defaultReviewersApi();
        try {
            Long requiredApprover = 1L;
            Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
            Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
            List<User> listUser = new ArrayList<>();
            listUser.add(User.create("test", "test@test.com", 1, "test", true, "test", "NORMAL"));
            Repository repository = Repository.create(null, -1, null, null, null, null, false, null, false, null, null);
            CreateCondition condition = CreateCondition.create(null, repository, matcherSrc, matcherDst, listUser, requiredApprover);

            String projectKey = "test";
            String repoKey = "1234";

            Condition returnCondition = api.createCondition(projectKey, "123456", condition);
            assertThat(returnCondition).isNotNull();
            assertThat(returnCondition.errors()).isNotEmpty();
            assertThat(returnCondition.errors().size()).isEqualTo(1);

            assertSent(server, "POST", "/rest/default-reviewers/" + BitbucketApiMetadata.API_VERSION
                    + "/projects/" + projectKey + "/repos/123456/condition/");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateCondition() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/default-reviwers-create.json")).setResponseCode(200));
        BitbucketApi baseApi = api(server.getUrl("/"));
        DefaultReviewersApi api = baseApi.defaultReviewersApi();
        try {
            Long requiredApprover = 1L;
            Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
            Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
            List<User> listUser = new ArrayList<>();
            listUser.add(User.create("test", "test@test.com", 1, "test", true, "test", "NORMAL"));
            Repository repository = Repository.create(null, -1, null, null, null, null, false, null, false, null, null);
            CreateCondition condition = CreateCondition.create(10L, repository, matcherSrc, matcherDst, listUser, requiredApprover);

            String projectKey = "test";
            String repoKey = "1234";

            Condition returnCondition = api.updateCondition(projectKey, repoKey, 10L, condition);
            assertThat(returnCondition).isNotNull();
            assertThat(returnCondition.errors()).isEmpty();
            assertThat(returnCondition.id()).isEqualTo(10L);

            assertSent(server, "PUT", "/rest/default-reviewers/" + BitbucketApiMetadata.API_VERSION
                + "/projects/" + projectKey + "/repos/" + repoKey + "/condition/10");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }

    public void testUpdateConditionOnError() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/repository-not-exist.json")).setResponseCode(404));
        BitbucketApi baseApi = api(server.getUrl("/"));
        DefaultReviewersApi api = baseApi.defaultReviewersApi();
        try {
            Long requiredApprover = 1L;
            Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
            Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
            List<User> listUser = new ArrayList<>();
            listUser.add(User.create("test", "test@test.com", 1, "test", true, "test", "NORMAL"));
            Repository repository = Repository.create(null, -1, null, null, null, null, false, null, false, null, null);
            CreateCondition condition = CreateCondition.create(10L, repository, matcherSrc, matcherDst, listUser, requiredApprover);

            String projectKey = "test";
            String repoKey = "1234";

            Condition returnCondition = api.updateCondition(projectKey, "123456", 10L, condition);
            assertThat(returnCondition).isNotNull();
            assertThat(returnCondition.errors()).isNotEmpty();
            assertThat(returnCondition.errors().size()).isEqualTo(1);

            assertSent(server, "PUT", "/rest/default-reviewers/" + BitbucketApiMetadata.API_VERSION
                + "/projects/" + projectKey + "/repos/123456/condition/10");
        } finally {
            baseApi.close();
            server.shutdown();
        }
    }
}
