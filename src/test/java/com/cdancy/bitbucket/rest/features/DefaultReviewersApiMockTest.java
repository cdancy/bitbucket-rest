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
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import com.cdancy.bitbucket.rest.internal.BaseBitbucketMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jdoire on 24/05/2017.
 */
@Test(groups = "unit", testName = "DefaultReviewersApiMockTest")
public class DefaultReviewersApiMockTest extends BaseBitbucketMockTest {

    public void testListConditions() throws Exception {
        MockWebServer server = mockEtcdJavaWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/defaultReviwers-list.json")).setResponseCode(200));
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
}
