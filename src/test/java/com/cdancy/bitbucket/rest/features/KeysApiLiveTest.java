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
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.sshkey.AccessKey;
import com.cdancy.bitbucket.rest.domain.sshkey.AccessKeyPage;
import com.cdancy.bitbucket.rest.options.CreateAccessKey;
import com.cdancy.bitbucket.rest.options.CreateKey;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "KeysApiLiveTest", singleThreaded = true)
public class KeysApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private final String testPubKey =
            "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCy9f0/nwkXESzkL4v4ftZ24VJYvkQ/Nt6vsLab3iSWtJXqrRsBythCcbAU6W9"
            + "5OGxjbTSFFtp0poqMcPuogocMR7QhjY9JGG3fcnJ7nYDCGRHD4zfG5Af/tHwvJ2ew0WTYoemvlfZIG/jZ7fsuOQSyUpJoxGAlb6"
            + "/QpnfSmJjxCx0VEoppWDn8CO3VhOgzVhWx0ecne+ZcUy3Ktt3HBQN0hosRfqkVSRTvkpK4RD8TaW5PrVDe1r2Q5ab37TO+Ls4xx"
            + "t16QlPubNxWeH3dHVzXdmFAItuH0DuyLyMoW1oxZ6+NrKu+pAAERxM303gejFzKDqXid5m1EOTvk4xhyqYN user@host";

    private String projectKey;
    private String repoKey;
    private Long repoKeyId;
    private Long projectKeyId;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
    }

    @Test
    public void testAddForRepository() {
        final AccessKey newKey = api().createForRepo(projectKey, repoKey,
                CreateAccessKey.create(CreateKey.create(testPubKey), AccessKey.PermissionType.REPO_READ));

        assertThat(newKey).isNotNull();
        assertThat(newKey.errors()).isEmpty();
        assertThat(newKey.key().id()).isNotNull();

        this.repoKeyId = newKey.key().id();
    }

    @Test(dependsOnMethods = "testAddForRepository")
    public void testGetForRepository() {
        final AccessKey key = api().getForRepo(projectKey, repoKey, repoKeyId);

        assertThat(key).isNotNull();
        assertThat(key.errors()).isEmpty();
        assertThat(key.key().id()).isNotNull();
        assertThat(key.key().id().equals(repoKeyId)).isTrue();
    }

    @Test(dependsOnMethods = "testGetForRepository")
    public void testListByRepository() {
        final AccessKeyPage accessKeyPage = api().listByRepo(projectKey, repoKey, 0, 25);

        assertThat(accessKeyPage).isNotNull();
        assertThat(accessKeyPage.errors()).isEmpty();
        assertThat(accessKeyPage.size()).isGreaterThan(0);

        assertThat(accessKeyPage.values()).isNotEmpty();
        boolean found = false;
        for (final AccessKey possibleAccessKey : accessKeyPage.values()) {
            if (possibleAccessKey.key().id().equals(repoKeyId)) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test(dependsOnMethods = "testListByRepository")
    public void testDeleteFromRepository() {
        RequestStatus success = api().deleteFromRepo(projectKey, repoKey, repoKeyId);

        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();

        AccessKey missingKey = api().getForRepo(projectKey, repoKey, repoKeyId);
        assertThat(missingKey.errors()).isNotEmpty();
    }

    @Test
    public void testAddForProject() {
        final AccessKey newKey = api().createForProject(projectKey,
                CreateAccessKey.create(CreateKey.create(testPubKey), AccessKey.PermissionType.PROJECT_READ));

        assertThat(newKey).isNotNull();
        assertThat(newKey.errors()).isEmpty();
        assertThat(newKey.key().id()).isNotNull();

        this.projectKeyId = newKey.key().id();
    }

    @Test(dependsOnMethods = "testAddForProject")
    public void testGetForProject() {
        final AccessKey key = api().getForProject(projectKey, projectKeyId);

        assertThat(key).isNotNull();
        assertThat(key.errors()).isEmpty();
        assertThat(key.key().id()).isNotNull();
        assertThat(key.key().id().equals(projectKeyId)).isTrue();
    }

    @Test(dependsOnMethods = "testGetForProject")
    public void testListByProject() {
        final AccessKeyPage accessKeyPage = api().listByProject(projectKey, 0, 25);

        assertThat(accessKeyPage).isNotNull();
        assertThat(accessKeyPage.errors()).isEmpty();
        assertThat(accessKeyPage.size()).isGreaterThan(0);

        assertThat(accessKeyPage.values()).isNotEmpty();
        boolean found = false;
        for (final AccessKey possibleAccessKey : accessKeyPage.values()) {
            if (possibleAccessKey.key().id().equals(projectKeyId)) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test(dependsOnMethods = "testListByProject")
    public void testDeleteFromProject() {
        RequestStatus success = api().deleteFromProject(projectKey, projectKeyId);

        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();

        AccessKey missingKey = api().getForProject(projectKey, projectKeyId);
        assertThat(missingKey.errors()).isNotEmpty();
    }

    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private KeysApi api() {
        return api.keysApi();
    }
}
