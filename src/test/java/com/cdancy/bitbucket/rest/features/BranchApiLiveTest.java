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
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.BranchModel;
import com.cdancy.bitbucket.rest.domain.branch.BranchModelConfiguration;
import com.cdancy.bitbucket.rest.domain.branch.BranchPage;
import com.cdancy.bitbucket.rest.domain.branch.BranchPermission;
import com.cdancy.bitbucket.rest.domain.branch.BranchPermissionEnumType;
import com.cdancy.bitbucket.rest.domain.branch.BranchPermissionPage;
import com.cdancy.bitbucket.rest.domain.branch.Matcher;
import com.cdancy.bitbucket.rest.domain.branch.Type;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.options.CreateBranch;
import com.cdancy.bitbucket.rest.options.CreateBranchModelConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Test(groups = "live", testName = "BranchApiLiveTest", singleThreaded = true)
public class BranchApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private String projectKey;
    private String repoKey;
    private String defaultBranchId;
    private String commitHash;

    private final String branchName = randomStringLettersOnly();
    private Long branchPermissionId;
    private BranchModelConfiguration branchModelConfiguration;

    @BeforeClass
    public void init() {
        generatedTestContents = initGeneratedTestContents();
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();

        Branch branch = api().getDefault(projectKey, repoKey);
        assertThat(branch).isNotNull();
        assertThat(branch.errors().isEmpty()).isTrue();
        defaultBranchId = branch.id();
        commitHash = branch.latestCommit();
    }

    @Test
    public void testCreateBranch() {
        CreateBranch createBranch = CreateBranch.create(branchName, commitHash, null);
        Branch branch = api().create(projectKey, repoKey, createBranch);
        assertThat(branch).isNotNull();
        assertThat(branch.errors().isEmpty()).isTrue();
        assertThat(branch.id().endsWith(branchName)).isTrue();
        assertThat(commitHash.equalsIgnoreCase(branch.latestChangeset())).isTrue();
    }

    @Test (dependsOnMethods = "testCreateBranch")
    public void testListBranches() {
        BranchPage branch = api().list(projectKey, repoKey, null, null, null, null, null, 1);
        assertThat(branch).isNotNull();
        assertThat(branch.errors().isEmpty()).isTrue();
        assertThat(branch.values().size() > 0).isTrue();
    }

    @Test (dependsOnMethods = "testListBranches")
    public void testGetBranchModel() {
        BranchModel branchModel = api().model(projectKey, repoKey);
        assertThat(branchModel).isNotNull();
        assertThat(branchModel.errors().isEmpty()).isTrue();
    }

    @Test (dependsOnMethods = "testGetBranchModel")
    public void testUpdateDefaultBranch() {
        boolean success = api().updateDefault(projectKey, repoKey, "refs/heads/" + branchName);
        assertThat(success).isTrue();
    }

    @Test (dependsOnMethods = "testUpdateDefaultBranch")
    public void testGetNewDefaultBranch() {
        Branch branch = api().getDefault(projectKey, repoKey);
        assertThat(branch).isNotNull();
        assertThat(branch.errors().isEmpty()).isTrue();
        assertThat(branch.id()).isNotNull();
    }

    @Test (dependsOnMethods = "testGetNewDefaultBranch")
    public void testCreateBranchPermission() {
        List<String> groupPermission = new ArrayList<>();
        groupPermission.add(defaultBitbucketGroup);
        List<BranchPermission> listBranchPermission = new ArrayList<>();
        listBranchPermission.add(BranchPermission.createWithId(null, BranchPermissionEnumType.FAST_FORWARD_ONLY,
                Matcher.create(Matcher.MatcherId.RELEASE, true), new ArrayList<User>(), groupPermission, null));
        listBranchPermission.add(BranchPermission.createWithId(null, BranchPermissionEnumType.FAST_FORWARD_ONLY,
                Matcher.create(Matcher.MatcherId.DEVELOPMENT, true), new ArrayList<User>(), groupPermission, null));
        listBranchPermission.add(BranchPermission.createWithId(null, BranchPermissionEnumType.FAST_FORWARD_ONLY,
                Matcher.create(Matcher.MatcherId.MASTER, true), new ArrayList<User>(), groupPermission, null));

        boolean success = api().updateBranchPermission(projectKey, repoKey, listBranchPermission);
        assertThat(success).isTrue();
    }

    @Test (dependsOnMethods = "testCreateBranchPermission")
    public void testListBranchPermission() {
        BranchPermissionPage branchPermissionPage = api().listBranchPermission(projectKey, repoKey, null, 1);
        assertThat(branchPermissionPage).isNotNull();
        assertThat(branchPermissionPage.errors().isEmpty()).isTrue();
        assertThat(branchPermissionPage.values().size() > 0).isTrue();
        branchPermissionId = branchPermissionPage.values().get(0).id();
    }

    @Test (dependsOnMethods = "testListBranchPermission")
    public void testDeleteBranchPermission() {
        if (branchPermissionId != null) {
            boolean success = api().deleteBranchPermission(projectKey, repoKey, branchPermissionId);
            assertThat(success).isTrue();
        } else {
            fail("branchPermissionId is null");
        }
    }

    @Test(dependsOnMethods = {"testListBranches"})
    public void testGetBranchModelConfiguration() {
        branchModelConfiguration = api().getModelConfiguration(projectKey, repoKey);
        checkDefaultBranchConfiguration();
    }

    @Test(dependsOnMethods = {"testGetBranchModelConfiguration"})
    public void testUpdateBranchModelConfiguration() {
        List<Type> types = new ArrayList<>();
        types.add(Type.create(Type.TypeId.BUGFIX, null, "bug/", false));
        types.add(Type.create(Type.TypeId.HOTFIX, null, "hot/", true));
        types.add(Type.create(Type.TypeId.RELEASE, null, "rel/", true));
        types.add(Type.create(Type.TypeId.FEATURE, null, "fea/", true));
        CreateBranchModelConfiguration configuration = CreateBranchModelConfiguration.create(branchModelConfiguration.development(), null, types);

        BranchModelConfiguration bmc = api().updateModelConfiguration(projectKey, repoKey, configuration);
        assertThat(bmc).isNotNull();
        assertThat(bmc.errors().isEmpty()).isTrue();
        assertThat(bmc.production()).isNull();
        assertThat(bmc.types().size()).isEqualTo(4);
        for (Type type : bmc.types()) {
            switch (type.id()) {
                case BUGFIX:
                    assertThat(type.prefix()).isEqualTo("bug/");
                    assertThat(type.enabled()).isFalse();
                    break;
                case HOTFIX:
                    assertThat(type.prefix()).isEqualTo("hot/");
                    assertThat(type.enabled()).isTrue();
                    break;
                case RELEASE:
                    assertThat(type.prefix()).isEqualTo("rel/");
                    assertThat(type.enabled()).isTrue();
                    break;
                case FEATURE:
                    assertThat(type.prefix()).isEqualTo("fea/");
                    assertThat(type.enabled()).isTrue();
                    break;
                default:
                    break;
            }
        }
    }

    @Test(dependsOnMethods = {"testUpdateBranchModelConfiguration"})
    public void testDeleteBranchModelConfiguration() {
        boolean success = api().deleteModelConfiguration(projectKey, repoKey);
        assertThat(success).isTrue();
    }

    @Test(dependsOnMethods = {"testListBranches"})
    public void testGetBranchModelConfigurationOnError() {
        BranchModelConfiguration configuration = api().getModelConfiguration(projectKey, randomString());
        assertThat(configuration).isNotNull();
        assertThat(configuration.errors()).isNotEmpty();
    }

    private void checkDefaultBranchConfiguration() {
        assertThat(branchModelConfiguration).isNotNull();
        assertThat(branchModelConfiguration.errors().isEmpty()).isTrue();
        assertThat(branchModelConfiguration.production()).isNull();
        assertThat(branchModelConfiguration.types().size()).isEqualTo(4);
        for (Type type : branchModelConfiguration.types()) {
            switch (type.id()) {
                case BUGFIX:
                    assertThat(type.prefix()).isEqualTo("bugfix/");
                    break;
                case HOTFIX:
                    assertThat(type.prefix()).isEqualTo("hotfix/");
                    break;
                case FEATURE:
                    assertThat(type.prefix()).isEqualTo("feature/");
                    break;
                case RELEASE:
                    assertThat(type.prefix()).isEqualTo("release/");
                    break;
                default:
                    break;
            }
            assertThat(type.enabled()).isTrue();
        }
    }

    @AfterClass
    public void fin() {
        boolean success = api().updateDefault(projectKey, repoKey, defaultBranchId);
        assertThat(success).isTrue();
        success = api().delete(projectKey, repoKey, "refs/heads/" + branchName);
        assertThat(success).isTrue();
        if (branchModelConfiguration != null) {
            branchModelConfiguration = api().updateModelConfiguration(projectKey, repoKey,
                CreateBranchModelConfiguration.create(branchModelConfiguration));
            checkDefaultBranchConfiguration();
        }

        terminateGeneratedTestContents(generatedTestContents);
    }

    private BranchApi api() {
        return api.branchApi();
    }
}
