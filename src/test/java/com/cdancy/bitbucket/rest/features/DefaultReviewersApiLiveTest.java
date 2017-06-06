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
import com.cdancy.bitbucket.rest.domain.branch.Matcher;
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.options.CreateCondition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "DefaultReviewersApiLiveTest", singleThreaded = true)
public class DefaultReviewersApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;
    private String projectKey;
    private String repoKey;
    private Long conditionId = null;

    @BeforeClass
    public void init() {
        this.generatedTestContents = initGeneratedTestContents();
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
    }

    @Test
    public void testListDefaultReviewersOnNewRepo() {
        List<Condition> conditionList = api().listConditions(projectKey, repoKey);
        assertThat(conditionList).isEmpty();
    }


    @Test(dependsOnMethods = {"testListDefaultReviewersOnNewRepo"})
    public void testCreateCondition() {
        Long requiredApprover = 1L;
        Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
        Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
        List<User> listUser = new ArrayList<>();
        listUser.add(getDefaultUser());
        CreateCondition condition = CreateCondition.create(null, generatedTestContents.repository, matcherSrc,
                matcherDst, listUser, requiredApprover);

        Condition returnCondition = api().createCondition(projectKey, repoKey, condition);
        conditionId = returnCondition.id();
        validCondition(returnCondition, requiredApprover, Matcher.MatcherId.ANY_REF, Matcher.MatcherId.ANY_REF);
    }

    @Test(dependsOnMethods = {"testListDefaultReviewersOnNewRepo", "testCreateCondition"})
    public void testCreateConditionOnError() {
        Long requiredApprover = 1L;
        Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
        Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
        List<User> listUser = new ArrayList<>();
        listUser.add(getDefaultUser());
        CreateCondition condition = CreateCondition.create(null, generatedTestContents.repository, matcherSrc,
                matcherDst, listUser, requiredApprover);

        Condition returnCondition = api().createCondition(projectKey, "1234", condition);
        assertThat(returnCondition.errors()).isNotEmpty();
        assertThat(returnCondition.targetRefMatcher()).isNull();
        assertThat(returnCondition.sourceRefMatcher()).isNull();
        assertThat(returnCondition.reviewers()).isNull();
        assertThat(returnCondition.repository()).isNull();
    }

    @Test(dependsOnMethods = {"testListDefaultReviewersOnNewRepo"})
    public void testCreateConditionMatcherDifferent() {
        Long requiredApprover = 1L;
        Matcher matcherSrc = Matcher.create(Matcher.MatcherId.MASTER, true);
        Matcher matcherDst = Matcher.create(Matcher.MatcherId.DEVELOPMENT, true);
        List<User> listUser = new ArrayList<>();
        listUser.add(getDefaultUser());
        CreateCondition condition = CreateCondition.create(null, generatedTestContents.repository, matcherSrc,
                matcherDst, listUser, requiredApprover);

        Condition returnCondition = api().createCondition(projectKey, repoKey, condition);
        validCondition(returnCondition, requiredApprover, Matcher.MatcherId.MASTER, Matcher.MatcherId.DEVELOPMENT);
    }

    @Test(dependsOnMethods = {"testListConditionsOnEmptyRepo", "testCreateCondition"})
    public void testUpdateCondition() {
        Long requiredApprover = 0L;
        Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
        Matcher matcherDst = Matcher.create(Matcher.MatcherId.DEVELOPMENT, true);
        List<User> listUser = new ArrayList<>();
        listUser.add(User.create("test", "test@test.com", 1, "test", true, "test", "NORMAL"));
        CreateCondition condition = CreateCondition.create(conditionId, generatedTestContents.repository,
                matcherSrc, matcherDst, listUser, requiredApprover);

        Condition returnCondition = api().updateCondition(generatedTestContents.project.key(),
                generatedTestContents.repository.slug(), conditionId, condition);
        validCondition(returnCondition, requiredApprover, Matcher.MatcherId.ANY_REF, Matcher.MatcherId.DEVELOPMENT);
        assertThat(returnCondition.id()).isEqualTo(conditionId);
    }

    @Test(dependsOnMethods = {"testListDefaultReviewersOnNewRepo", "testCreateCondition", "testCreateConditionMatcherDifferent"})
    public void testListConditions() {
        List<Condition> listCondition = api().listConditions(projectKey, repoKey);
        assertThat(listCondition.size()).isEqualTo(2);
        for (Condition condition : listCondition) {
            if (condition.id().equals(conditionId)) {
                validCondition(condition, 1L, Matcher.MatcherId.ANY_REF, Matcher.MatcherId.ANY_REF);
            } else {
                validCondition(condition, 1L, Matcher.MatcherId.MASTER, Matcher.MatcherId.DEVELOPMENT);
            }
        }
    }

    @AfterClass
    public void fin() {
        terminateGeneratedTestContents(generatedTestContents);
    }

    private DefaultReviewersApi api() {
        return api.defaultReviewersApi();
    }

    private void validCondition(Condition returnValue, Long requiredApprover, Matcher.MatcherId matcherSrc, Matcher.MatcherId matcherDst) {
        assertThat(returnValue.errors()).isEmpty();
        assertThat(returnValue.repository().name().equals(repoKey));
        assertThat(returnValue.id()).isNotNull();
        assertThat(returnValue.errors()).isEmpty();
        assertThat(returnValue.requiredApprovals()).isEqualTo(requiredApprover);
        assertThat(returnValue.reviewers().size()).isEqualTo(1);
        assertThat(returnValue.reviewers().get(0).id()).isEqualTo(1);
        assertThat(returnValue.sourceRefMatcher().id()).isEqualTo(matcherSrc.getId());
        assertThat(returnValue.targetRefMatcher().id()).isEqualTo(matcherDst.getId());
    }
}
