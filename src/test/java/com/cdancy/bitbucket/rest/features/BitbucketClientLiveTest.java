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

import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.BitbucketClient;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.admin.UserPage;

@Test(groups = "live", testName = "BitbucketClientLiveTest")
public class BitbucketClientLiveTest extends BaseBitbucketApiLiveTest {

    @Test
    public void testCreateClient() {
        final BitbucketClient client = new BitbucketClient(this.endpoint, this.bitbucketAuthentication);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isEmpty();
    }

    @Test
    public void testCreateClientWithBuilder() {
        final BitbucketClient.Builder builder = BitbucketClient.builder();
        switch (bitbucketAuthentication.authType()) {
            case Anonymous: break;
            case Basic:
                builder.credentials(bitbucketAuthentication.authValue());
                break;
            case Bearer:
                builder.token(bitbucketAuthentication.authValue());
                break;
            default: break;
        }
        final BitbucketClient client = builder.endPoint(this.endpoint).build();
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isEmpty();
    }

    @Test
    public void testCreateClientWithWrongCredentials() {
        final BitbucketClient client = new BitbucketClient(this.endpoint, TestUtilities.randomStringLettersOnly());
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isNotEmpty();
    }
}
