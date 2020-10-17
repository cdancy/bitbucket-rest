/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.cdancy.bitbucket.rest.features;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.search.SearchRequest;
import com.cdancy.bitbucket.rest.domain.search.SearchResult;
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link SearchApi} class.
 */
@Test(groups = "unit", testName = "SearchApiMockTest")
public class SearchApiMockTest
    extends BaseBitbucketMockTest {

  private final String searchPhrase = "PaymentIntent";

  public void testGetVersion()
      throws Exception {
    final MockWebServer server = mockWebServer();

    server.enqueue(new MockResponse().setBody(payloadFromResource("/search.json")).setResponseCode(200));
    try (final BitbucketApi baseApi = api(server.getUrl("/"))) {
      final SearchResult actual = baseApi.searchApi().search(buildSearchRequest(searchPhrase));
      assertThat(actual).isNotNull();
      assertThat(actual.query()).isNotNull();
      assertSent(server, "POST", "/rest/search/" + BitbucketApiMetadata.API_VERSION + "/search", buildSearchRequestString(searchPhrase));
    } finally {
      server.shutdown();
    }
  }

  private SearchRequest buildSearchRequest(final String searchPhrase) {
    final Map<String, Object> request = new HashMap<>();
    final Map<String, Object> entities = new HashMap<>();
    final Map<String, Integer> code = new HashMap<>();
    final Map<String, Integer> limits = new HashMap<>();

    request.put("query", searchPhrase);
    entities.put("code", code);
    request.put("entities", entities);
    limits.put("primary", 25);
    limits.put("secondary", 10);
    request.put("limits", limits);
    
    return SearchRequest.of(request);
  }
  
  private String buildSearchRequestString(final String searchPhrase) {
    return "{\n"
        + "  \"query\": \"" + searchPhrase + "\",\n"
        + "  \"entities\": {\n"
        + "    \"code\": {}\n"
        + "  },\n"
        + "  \"limits\": {\n"
        + "    \"primary\": 25,\n"
        + "    \"secondary\": 10\n"
        + "  }\n"
        + "}";
  }
}
