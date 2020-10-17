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

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.search.SearchRequest;
import com.cdancy.bitbucket.rest.domain.search.SearchResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

@Test(groups = "live", testName = "SearchApiLiveTest", singleThreaded = true)
public class SearchApiLiveTest
    extends BaseBitbucketApiLiveTest {

  private GeneratedTestContents generatedTestContents;
//  private String projectKey;
//  private User user;
  private final String searchPhrase = "PaymentIntent";

  @BeforeClass
  public void init() {
      generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
//      this.projectKey = generatedTestContents.project.key();
//      this.user = TestUtilities.getDefaultUser(this.bitbucketAuthentication, this.api);
  }
  
  @AfterClass
  public void fin() {
      TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
  }

  @Test
  public void testGetVersion() {
    final SearchResult actual = api().search(buildSearchRequest(searchPhrase));
    assertThat(actual).isNotNull();
    assertThat(actual.query()).isNotNull();
  }

  private SearchApi api() {
    return api.searchApi();
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
}
