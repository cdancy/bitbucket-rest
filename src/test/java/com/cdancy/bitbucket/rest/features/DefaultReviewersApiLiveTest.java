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
import com.cdancy.bitbucket.rest.domain.branch.Matcher;
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.options.CreateCondition;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "DefaultReviewersApiLiveTest", singleThreaded = true)
public class DefaultReviewersApiLiveTest extends BaseBitbucketApiLiveTest {

    String projectKey = randomStringLettersOnly();
    String repoKey = randomStringLettersOnly();
    Long conditionId = null;
    Repository repository = null;

    @BeforeClass
    public void init() {
        CreateProject createProject = CreateProject.create(projectKey, null, null, null);
        Project project = api.projectApi().create(createProject);
        assertThat(project).isNotNull();
        assertThat(project.errors().isEmpty()).isTrue();
        assertThat(project.key().equalsIgnoreCase(projectKey)).isTrue();
        CreateRepository createRepository = CreateRepository.create(repoKey, true);
        repository = api.repositoryApi().create(projectKey, createRepository);
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isTrue();
        assertThat(repoKey.equalsIgnoreCase(repository.name())).isTrue();
    }

    @Test
    public void testListConditionsOnEmptyRepo() {
        List<Condition> conditionList = api().listConditions(projectKey, repoKey);
        assertThat(conditionList).isEmpty();
    }

    @Test(dependsOnMethods = {"testListConditionsOnEmptyRepo"})
    public void testCreateCondition() {
        Long requiredApprover = 1L;
        Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
        Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
        List<User> listUser = new ArrayList<>();
        listUser.add(User.create("test", "test@test.com", 1, "test", true, "test", "NORMAL"));
        CreateCondition condition = CreateCondition.create(null, repository, matcherSrc, matcherDst, listUser, requiredApprover);

        Condition returnCondition = api().createCondition(projectKey, repoKey, condition);
        conditionId = returnCondition.id();
        validCondition(returnCondition, requiredApprover, Matcher.MatcherId.ANY_REF, Matcher.MatcherId.ANY_REF);
    }

    @Test(dependsOnMethods = {"testListConditionsOnEmptyRepo", "testCreateCondition"})
    public void testCreateConditionOnError() {
        Long requiredApprover = 1L;
        Matcher matcherSrc = Matcher.create(Matcher.MatcherId.ANY, true);
        Matcher matcherDst = Matcher.create(Matcher.MatcherId.ANY, true);
        List<User> listUser = new ArrayList<>();
        listUser.add(User.create("test", "test@test.com", 1, "test", true, "test", "NORMAL"));
        CreateCondition condition = CreateCondition.create(null, repository, matcherSrc, matcherDst, listUser, requiredApprover);

        Condition returnCondition = api().createCondition(projectKey, "1234", condition);
        assertThat(returnCondition.errors()).isNotEmpty();
        assertThat(returnCondition.targetRefMatcher()).isNull();
        assertThat(returnCondition.sourceRefMatcher()).isNull();
        assertThat(returnCondition.reviewers()).isNull();
        assertThat(returnCondition.repository()).isNull();
    }

    @Test(dependsOnMethods = {"testListConditionsOnEmptyRepo"})
    public void testCreateConditionMatcherDiff() {
        Long requiredApprover = 1L;
        Matcher matcherSrc = Matcher.create(Matcher.MatcherId.MASTER, true);
        Matcher matcherDst = Matcher.create(Matcher.MatcherId.DEVELOPMENT, true);
        List<User> listUser = new ArrayList<>();
        listUser.add(User.create("test", "test@test.com", 1, "test", true, "test", "NORMAL"));
        CreateCondition condition = CreateCondition.create(null, repository, matcherSrc, matcherDst, listUser, requiredApprover);

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
        CreateCondition condition = CreateCondition.create(conditionId, repository, matcherSrc, matcherDst, listUser, requiredApprover);

        Condition returnCondition = api().updateCondition(projectKey, repoKey, conditionId, condition);
        validCondition(returnCondition, requiredApprover, Matcher.MatcherId.ANY_REF, Matcher.MatcherId.DEVELOPMENT);
        assertThat(returnCondition.id()).isEqualTo(conditionId);
    }

    @Test(dependsOnMethods = {"testListConditionsOnEmptyRepo", "testCreateCondition", "testUpdateCondition", "testCreateConditionMatcherDiff"})
    public void testListConditions() {
        List<Condition> listCondition = api().listConditions(projectKey, repoKey);
        assertThat(listCondition.size()).isEqualTo(2);
        for (Condition condition : listCondition) {
            if (condition.id().equals(conditionId)) {
                validCondition(condition, 0L, Matcher.MatcherId.ANY_REF, Matcher.MatcherId.DEVELOPMENT);
            } else {
                validCondition(condition, 1L, Matcher.MatcherId.MASTER, Matcher.MatcherId.DEVELOPMENT);
            }
        }
    }

    @Test(dependsOnMethods = {"testListConditionsOnEmptyRepo", "testCreateCondition", "testUpdateCondition",
            "testCreateConditionMatcherDiff", "testListConditions"})
    public void testDeleteCondition() {
        boolean success = api().deleteCondition(projectKey, repoKey, conditionId);
        assertThat(success).isTrue();
    }

    @Test()
    public void testDeleteConditionOnError() {
        boolean success = api().deleteCondition(projectKey, repoKey, -1);
        assertThat(success).isFalse();
    }

    @Test(dependsOnMethods = {"testListConditionsOnEmptyRepo", "testCreateCondition", "testUpdateCondition",
            "testCreateConditionMatcherDiff", "testListConditions", "testDeleteCondition"})
    public void testListConditionsAfterDelete() {
        List<Condition> listCondition = api().listConditions(projectKey, repoKey);
        assertThat(listCondition.size()).isEqualTo(1);
        for (Condition condition : listCondition) {
            assertThat(condition.id()).isNotEqualTo(conditionId);
            validCondition(condition, 1L, Matcher.MatcherId.MASTER, Matcher.MatcherId.DEVELOPMENT);
        }
    }

    @AfterClass
    public void fin() {
        boolean success = api.repositoryApi().delete(projectKey, repoKey);
        assertThat(success).isTrue();
        success = api.projectApi().delete(projectKey);
        assertThat(success).isTrue();
    }

    private DefaultReviewersApi api() {
        return api.defaultReviewersApi();
    }

    private void validCondition(Condition returnValue, Long requiredApprover, Matcher.MatcherId matcherSrc, Matcher.MatcherId matcherDst) {
        assertThat(returnValue.errors()).isEmpty();
        assertThat(returnValue.repository().name().equals(repoKey));
        assertThat(returnValue.id()).isNotNull();
        assertThat(returnValue.requiredApprovals()).isEqualTo(requiredApprover);
        assertThat(returnValue.reviewers().size()).isEqualTo(1);
        assertThat(returnValue.reviewers().get(0).id()).isEqualTo(1);
        assertThat(returnValue.sourceRefMatcher().id()).isEqualTo(matcherSrc.getId());
        assertThat(returnValue.targetRefMatcher().id()).isEqualTo(matcherDst.getId());
    }
}
