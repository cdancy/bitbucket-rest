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

import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.BitbucketAuthentication;
import com.cdancy.bitbucket.rest.auth.AuthenticationType;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "BitbucketAuthenticationMockTest")
public class BitbucketAuthenticationMockTest extends BaseBitbucketMockTest {

    private final String unencodedAuth = "hello:world";
    private final String encodedAuth = "aGVsbG86d29ybGQ=";

    public void testCreateAnonymousAuth() {
        final BitbucketAuthentication auth = BitbucketAuthentication.builder().build();
        assertThat(auth).isNotNull();
        assertThat(auth.authValue()).isNull();
        assertThat(auth.authType()).isEqualTo(AuthenticationType.Anonymous);
    }

    public void testCreateBasicAuthUnencoded() {
        final BitbucketAuthentication auth = BitbucketAuthentication.builder().credentials(unencodedAuth).build();
        assertThat(auth).isNotNull();
        assertThat(auth.authValue()).isNotNull();
        assertThat(auth.authValue()).isNotEqualTo(unencodedAuth);
        assertThat(auth.authValue()).isEqualTo(encodedAuth);
        assertThat(auth.authType()).isEqualTo(AuthenticationType.Basic);
    }

    public void testCreateBasicAuthEncoded() {
        final BitbucketAuthentication auth = BitbucketAuthentication.builder().credentials(encodedAuth).build();
        assertThat(auth).isNotNull();
        assertThat(auth.authValue()).isNotNull();
        assertThat(auth.authValue()).isEqualTo(encodedAuth);
        assertThat(auth.authType()).isEqualTo(AuthenticationType.Basic);
    }

    public void testCreateBearerAuth() {
        final BitbucketAuthentication auth = BitbucketAuthentication.builder().token(encodedAuth).build();
        assertThat(auth).isNotNull();
        assertThat(auth.authValue()).isNotNull();
        assertThat(auth.authValue()).isEqualTo(encodedAuth);
        assertThat(auth.authType()).isEqualTo(AuthenticationType.Bearer);
    }
}
