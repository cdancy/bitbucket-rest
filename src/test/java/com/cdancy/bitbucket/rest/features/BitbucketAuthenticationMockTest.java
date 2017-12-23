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

package com.cdancy.bitbucket.rest.filters;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import static com.google.common.io.BaseEncoding.base64;

import org.testng.annotations.BeforeMethod;

/**
 * Unit tests for the {@link BitbucketAuthentication} class.
 */
@Test(groups = "unit", testName = "BitbucketAuthenticationMockTest")
public class BitbucketAuthenticationMockTest {

    private final String admin = "admin";
    private final String authorization = "authorization";
    private final String basic = "Basic ";
    private final String basicAt = "basic@";

    private HttpRequest httpRequest;

    public BitbucketAuthenticationMockTest() { }

    @SuppressWarnings("unused")
    @BeforeMethod
    private void initHttpRequest() {
        httpRequest = HttpRequest.builder()
            .endpoint("http://domain.org")
            .method("GET")
            .build();
    }

    @Test
    public void testBasicAt() {
        final String credential = "admin:password";
        final Supplier<Credentials> creds = Suppliers.ofInstance(new Credentials(admin,basicAt + credential));
        final BitbucketAuthentication bitbucketAuthentication = new BitbucketAuthentication(creds);
        httpRequest = bitbucketAuthentication.filter(httpRequest);
        assertThat(httpRequest.getFirstHeaderOrNull(authorization)).isEqualTo(basic + base64().encode(credential.getBytes()));
    }

    @Test
    public void testBasicAtEmpty() {
        final String credential = "";
        final Supplier<Credentials> creds = Suppliers.ofInstance(new Credentials(admin,basicAt + credential));
        final BitbucketAuthentication bitbucketAuthentication = new BitbucketAuthentication(creds);
        try {
            httpRequest = bitbucketAuthentication.filter(httpRequest);
        } catch (IllegalArgumentException exception) {
            assertThat(httpRequest.getFirstHeaderOrNull(authorization)).isEqualTo(null);
        }
    }

    @Test
    public void testBasicAtWithAtInPassword() {
        final String credential = "admin:pass@word";
        final Supplier<Credentials> creds = Suppliers.ofInstance(new Credentials(admin,basicAt + credential));
        final BitbucketAuthentication bitbucketAuthentication = new BitbucketAuthentication(creds);
        httpRequest = bitbucketAuthentication.filter(httpRequest);
        assertThat(httpRequest.getFirstHeaderOrNull(authorization)).isEqualTo(basic + base64().encode(credential.getBytes()));
    }

    @Test
    public void testBasic() {
        final String credential = "admin:password";
        final Supplier<Credentials> creds = Suppliers.ofInstance(new Credentials(admin,credential));
        final BitbucketAuthentication bitbucketAuthentication = new BitbucketAuthentication(creds);
        httpRequest = bitbucketAuthentication.filter(httpRequest);
        assertThat(httpRequest.getFirstHeaderOrNull(authorization)).isEqualTo(basic + base64().encode(credential.getBytes()));
    }

    @Test
    public void testBase64() {
        final String credential = "YWRtaW46cGFzc3dvcmQ=";
        final Supplier<Credentials> creds = Suppliers.ofInstance(new Credentials(admin,credential));
        final BitbucketAuthentication bitbucketAuthentication = new BitbucketAuthentication(creds);
        httpRequest = bitbucketAuthentication.filter(httpRequest);
        assertThat(httpRequest.getFirstHeaderOrNull(authorization)).isEqualTo(basic + credential);
    }

    @Test
    public void testBasicBase64() {
        final String credential = "YWRtaW46cGFzc3dvcmQ=";
        final Supplier<Credentials> creds = Suppliers.ofInstance(new Credentials(admin,basicAt + credential));
        final BitbucketAuthentication bitbucketAuthentication = new BitbucketAuthentication(creds);
        httpRequest = bitbucketAuthentication.filter(httpRequest);
        assertThat(httpRequest.getFirstHeaderOrNull(authorization)).isEqualTo(basic + credential);
    }

    @Test
    public void testBearer() {
        final String credential = "9DfK3AF9Jeke1O0dkKX5kDswps43FEDlf5Frkspma21M";
        final Supplier<Credentials> creds = Suppliers.ofInstance(new Credentials(admin,"bearer@" + credential));
        final BitbucketAuthentication bitbucketAuthentication = new BitbucketAuthentication(creds);
        httpRequest = bitbucketAuthentication.filter(httpRequest);
        assertThat(httpRequest.getFirstHeaderOrNull(authorization)).isEqualTo("Bearer " + credential);
    }

    @Test
    public void testBearerEmpty() {
        final String credential = "";
        final Supplier<Credentials> creds = Suppliers.ofInstance(new Credentials(admin,"bearer@" + credential));
        final BitbucketAuthentication bitbucketAuthentication = new BitbucketAuthentication(creds);
        try {
            httpRequest = bitbucketAuthentication.filter(httpRequest);
        } catch (IllegalArgumentException exception) {
            assertThat(httpRequest.getFirstHeaderOrNull(authorization)).isEqualTo(null);
        }
    }

    @Test
    public void testBadAuthentication() {
        final String credential = "Bearer 9DfK3AF9Jeke1O0dkKX5kDswps43FEDlf5Frkspma21M";
        final Supplier<Credentials> creds = Suppliers.ofInstance(new Credentials(admin,credential));
        final BitbucketAuthentication bitbucketAuthentication = new BitbucketAuthentication(creds);
        try {
            httpRequest = bitbucketAuthentication.filter(httpRequest);
        } catch (IllegalArgumentException exception) {
            assertThat(httpRequest.getFirstHeaderOrNull(authorization)).isEqualTo(null);
        }
    }
}
