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
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "DefaultReviewersApiLiveTest", singleThreaded = true)
public class DefaultReviewersApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;


    @BeforeClass
    public void init() {
        generatedTestContents = initGeneratedTestContents();
    }

    @Test
    public void testListDefaultReviewersOnEmptyRepo() {
        List<Condition> conditionList = api().listConditions(generatedTestContents.project.key(), generatedTestContents.repository.slug());
        assertThat(conditionList).isEmpty();
    }

    @AfterClass
    public void fin() {
        boolean success = api.repositoryApi().delete(generatedTestContents.project.key(), generatedTestContents.repository.slug());
        assertThat(success).isTrue();
        success = api.projectApi().delete(generatedTestContents.project.key());
        assertThat(success).isTrue();
    }

    private DefaultReviewersApi api() {
        return api.defaultReviewersApi();
    }
}
