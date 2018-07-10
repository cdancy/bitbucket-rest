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

import com.cdancy.bitbucket.rest.config.BitbucketAuthenticationModule;
import com.cdancy.bitbucket.rest.domain.activities.ActivitiesPage;
import com.cdancy.bitbucket.rest.domain.participants.Participants;
import com.cdancy.bitbucket.rest.domain.participants.ParticipantsPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.MinimalRepository;
import com.cdancy.bitbucket.rest.domain.pullrequest.MergeStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ProjectKey;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.domain.pullrequest.Reference;

import com.cdancy.bitbucket.rest.options.CreateParticipants;
import com.cdancy.bitbucket.rest.options.CreatePullRequest;
import com.google.common.collect.Lists;
import org.jclouds.ContextBuilder;
import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.BitbucketAuthentication;
import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.BranchPage;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequestPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;

@Test(groups = "live", testName = "PullRequestApiLiveTest", singleThreaded = true)
public class PullRequestApiLiveTest extends BaseBitbucketApiLiveTest {

    private static final String TEST_USER_NAME = "TestUserName2";
    private static final String TEST_USER_PASSWORD = "TestUserPassword2";

    private GeneratedTestContents generatedTestContents;
    private BitbucketApi testUserApi;

    private String project;
    private String repo;
    private String branchToMerge;
    private Participants participants;
    private User foundUser;
    private int prId = -1;
    private int version = -1;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        this.project = generatedTestContents.project.key();
        this.repo = generatedTestContents.repository.name();

        final BranchPage branchPage = api.branchApi().list(project, repo, null, null, null, null, null, null);
        assertThat(branchPage).isNotNull();
        assertThat(branchPage.errors().isEmpty()).isTrue();
        assertThat(branchPage.values().size()).isEqualTo(2);

        for (final Branch branch : branchPage.values()) {
            if (!branch.id().endsWith("master")) {
                this.branchToMerge = branch.id();
                break;
            }
        }

        assertThat(branchToMerge).isNotNull();

        addTestUser();
        testUserApi = apiForTestUser();
    }

    @Test
    public void createPullRequest() {
        final String randomChars = TestUtilities.randomString();
        final ProjectKey proj = ProjectKey.create(project);
        final MinimalRepository repository = MinimalRepository.create(repo, null, proj);
        final Reference fromRef = Reference.create(branchToMerge, repository, branchToMerge);
        final Reference toRef = Reference.create(null, repository);
        final CreatePullRequest cpr = CreatePullRequest.create(randomChars, "Fix for issue " + randomChars, fromRef, toRef, null, null);
        final PullRequest pr = api().create(project, repo, cpr);
        assertThat(pr).isNotNull();
        assertThat(project.equals(pr.fromRef().repository().project().key())).isTrue();
        assertThat(repo.equals(pr.fromRef().repository().name())).isTrue();
        prId = pr.id();
        version = pr.version();
    }

    @Test (dependsOnMethods = "createPullRequest")
    public void testGetPullRequest() {
        final PullRequest pr = api().get(project, repo, prId);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isTrue();
        assertThat(project.equals(pr.fromRef().repository().project().key())).isTrue();
        assertThat(repo.equals(pr.fromRef().repository().name())).isTrue();
        assertThat(version == pr.version()).isTrue();
    }

    @Test (dependsOnMethods = "testGetPullRequest")
    public void testListPullRequest() {
        final PullRequestPage pr = api().list(project, repo, null, null, null, null, null, null, null, 10);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isTrue();
        assertThat(pr.values().size() > 0).isTrue();
    }

    @Test (dependsOnMethods = "testListPullRequest")
    public void testGetPullRequestChanges() {
        final ChangePage pr = api().changes(project, repo, prId, null, null, null);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isTrue();
        assertThat(pr.values().size() > 0).isTrue();
    }

    @Test (dependsOnMethods = "testGetPullRequestChanges")
    public void testGetPullRequestCommits() {
        final CommitPage pr = api().commits(project, repo, prId, true, 1, null);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isTrue();
        assertThat(pr.values().size() == 1).isTrue();
        assertThat(pr.totalCount() > 0).isTrue();
    }

    @Test (dependsOnMethods = {"testGetPullRequestCommits", "testAddExistingParticipant"})
    public void testDeclinePullRequest() {
        final PullRequest pr = api().decline(project, repo, prId, version);
        assertThat(pr).isNotNull();
        assertThat("DECLINED".equalsIgnoreCase(pr.state())).isTrue();
        assertThat(pr.open()).isFalse();
    }

    @Test (dependsOnMethods = "testDeclinePullRequest")
    public void testReopenPullRequest() {
        PullRequest pr = api().get(project, repo, prId);
        pr = api().reopen(project, repo, prId, pr.version());
        assertThat(pr).isNotNull();
        assertThat("OPEN".equalsIgnoreCase(pr.state())).isTrue();
        assertThat(pr.open()).isTrue();
    }

    @Test (dependsOnMethods = "testReopenPullRequest")
    public void testCanMergePullRequest() {
        final MergeStatus mergeStatus = api().canMerge(project, repo, prId);
        assertThat(mergeStatus).isNotNull();
        assertThat(mergeStatus.canMerge()).isTrue();
    }

    @Test (dependsOnMethods = "testCanMergePullRequest")
    public void testMergePullRequest() {
        PullRequest pr = api().get(project, repo, prId);
        pr = api().merge(project, repo, prId, pr.version());
        assertThat(pr).isNotNull();
        assertThat("MERGED".equalsIgnoreCase(pr.state())).isTrue();
        assertThat(pr.open()).isFalse();
    }

    @Test (dependsOnMethods = "testGetPullRequest")
    public void testGetListParticipants() {
        final ParticipantsPage pg = api().listParticipants(project, repo, prId, 100, 0);
        assertThat(pg).isNotNull();
        assertThat(pg.errors()).isEmpty();
        assertThat(pg.values()).isNotEmpty();
        participants = pg.values().get(0);
    }

    @Test (dependsOnMethods = "testGetListParticipants")
    public void testAssignDefaultParticipantsOnError() {
        final CreateParticipants createParticipants = CreateParticipants.create(participants.user(),
                participants.lastReviewedCommit(), Participants.Role.REVIEWER, participants.approved(), participants.status());
        final Participants localParticipants = api().assignParticipant(project, repo, prId, createParticipants);
        assertThat(localParticipants).isNotNull();
        assertThat(localParticipants.errors()).isNotEmpty();
    }

    @Test (dependsOnMethods = "testAssignDefaultParticipantsOnError")
    public void testAssignParticipants() {
        final UserPage userPage = api.adminApi().listUsersByGroup(defaultBitbucketGroup, null, null, null);
        assertThat(userPage).isNotNull();
        assertThat(userPage.size() > 0).isTrue();

        for (final User possibleUser : userPage.values()) {
            if (!possibleUser.name().equalsIgnoreCase(participants.user().name())) {
                foundUser = possibleUser;
                break;
            }
        }
        assertThat(foundUser).isNotNull();

        final CreateParticipants createParticipants = CreateParticipants.create(foundUser,
                participants.lastReviewedCommit(), Participants.Role.REVIEWER, participants.approved(), participants.status());
        final Participants localParticipants = api().assignParticipant(project, repo, prId, createParticipants);
        assertThat(localParticipants).isNotNull();
        assertThat(localParticipants.errors()).isEmpty();
    }

    @Test (dependsOnMethods = "testAssignParticipants")
    public void testDeleteParticipant() {
        final RequestStatus success = api().deleteParticipant(project, repo, prId, foundUser.slug());
        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();
    }

    @Test (dependsOnMethods = "testDeleteParticipant")
    public void testDeleteParticipantNonExistent() {
        final RequestStatus success = api().deleteParticipant(project, repo, prId, TestUtilities.randomString());
        assertThat(success).isNotNull();
        assertThat(success.value()).isFalse();
        assertThat(success.errors()).isNotEmpty();
    }

    @Test (dependsOnMethods = "testDeleteParticipantNonExistent")
    public void testGetListActivities() {
        final ActivitiesPage ac = api().listActivities(project, repo, prId, 100, 0);
        assertThat(ac).isNotNull();
        assertThat(ac.errors()).isEmpty();
    }

    @Test
    public void testGetNonExistentPullRequest() {
        final PullRequest pr = api().get(TestUtilities.randomString(), TestUtilities.randomString(), 999);
        assertThat(pr).isNotNull();
        assertThat(pr.errors().isEmpty()).isFalse();
    }

    @Test (dependsOnMethods = "testGetListActivities")
    public void testAddNonExistentParticipant() {
        final User testUser = getTestUser();

        final CreateParticipants createParticipants = CreateParticipants.create(testUser,
                participants.lastReviewedCommit(),
                Participants.Role.REVIEWER,
                false,
                Participants.Status.NEEDS_WORK);

        final Participants localParticipants =
                testUserApi.pullRequestApi().addParticipant(project, repo, prId, testUser.slug(), createParticipants);

        assertThat(localParticipants).isNotNull();
        assertThat(localParticipants.errors()).isEmpty();
        assertThat(localParticipants.status()).isEqualByComparingTo(Participants.Status.NEEDS_WORK);
    }

    @Test (dependsOnMethods = "testAddNonExistentParticipant")
    public void testAddExistingParticipant() {
        final User testUser = getTestUser();

        final CreateParticipants createParticipants = CreateParticipants.create(testUser,
                participants.lastReviewedCommit(),
                Participants.Role.REVIEWER,
                true,
                Participants.Status.APPROVED);

        final Participants localParticipants =
                testUserApi.pullRequestApi().addParticipant(project, repo, prId, testUser.slug(), createParticipants);

        assertThat(localParticipants).isNotNull();
        assertThat(localParticipants.errors()).isEmpty();
        assertThat(localParticipants.status()).isEqualByComparingTo(Participants.Status.APPROVED);
    }

    @AfterClass
    public void fin() throws IOException {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
        testUserApi.close();
        deleteTestUser();
    }

    private PullRequestApi api() {
        return api.pullRequestApi();
    }

    private BitbucketApi apiForTestUser() {
        final BitbucketAuthentication creds = BitbucketAuthentication
                .builder()
                .credentials(TEST_USER_NAME + ":" + TEST_USER_PASSWORD)
                .build();
        final BitbucketAuthenticationModule credsModule = new BitbucketAuthenticationModule(creds);
        return ContextBuilder.newBuilder(provider)
            .endpoint(endpoint)
            .overrides(setupProperties())
            .modules(Lists.newArrayList(credsModule))
            .buildApi(BitbucketApi.class);
    }

    private User getTestUser() {
        final UserPage userPage = api.adminApi().listUsers(TEST_USER_NAME, null, null);
        assertThat(userPage.values().isEmpty()).isFalse();
        return userPage.values().get(0);
    }

    private void addTestUser() {
        api.adminApi().createUser(TEST_USER_NAME, TEST_USER_PASSWORD, TEST_USER_NAME, "test@test.test", true, null);
    }

    private void deleteTestUser() {
        api.adminApi().deleteUser(TEST_USER_NAME);
    }
}
