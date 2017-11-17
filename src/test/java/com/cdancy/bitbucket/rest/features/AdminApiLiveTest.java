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
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "AdminApiLiveTest", singleThreaded = true)
public class AdminApiLiveTest extends BaseBitbucketApiLiveTest {

    @Test
    public void testListUsersByGroup() {
        final UserPage userPage = api().listUsersByGroup(defaultBitbucketGroup, null, null, null);
        assertThat(userPage).isNotNull();
        assertThat(userPage.size() > 0).isTrue();
    }
    
    @Test
    public void testListUsersByNonExistentGroup() {
        final UserPage userPage = api().listUsersByGroup(TestUtilities.randomString(), null, null, null);
        assertThat(userPage).isNotNull();
        assertThat(userPage.size() == 0).isTrue();
    }
    
    @Test
    public void testListUsers() {
        final User user = TestUtilities.getDefaultUser(this.credential, this.api);
        final UserPage userPage = api().listUsers(user.slug(), null, null);
        assertThat(userPage).isNotNull();
        assertThat(userPage.size() > 0).isTrue();
    }
    
    @Test
    public void testListUsersNonExistent() {
        final UserPage userPage = api().listUsers(TestUtilities.randomString(), null, null);
        assertThat(userPage).isNotNull();
        assertThat(userPage.size() == 0).isTrue();
    }
    
    private AdminApi api() {
        return api.adminApi();
    }
}
