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
import static com.cdancy.bitbucket.rest.BitbucketConstants.CREDENTIALS_ENVIRONMENT_VARIABLE;
import static com.cdancy.bitbucket.rest.BitbucketConstants.CREDENTIALS_SYSTEM_PROPERTY;
import static com.cdancy.bitbucket.rest.BitbucketConstants.ENDPOINT_ENVIRONMENT_VARIABLE;
import static com.cdancy.bitbucket.rest.BitbucketConstants.ENDPOINT_SYSTEM_PROPERTY;
import static com.cdancy.bitbucket.rest.BitbucketConstants.TOKEN_ENVIRONMENT_VARIABLE;
import static com.cdancy.bitbucket.rest.BitbucketConstants.TOKEN_SYSTEM_PROPERTY;

import org.testng.annotations.Test;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.BitbucketClient;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.auth.AuthenticationType;
import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.jclouds.javax.annotation.Nullable;

@Test(groups = "live", testName = "BitbucketClientLiveTest", singleThreaded = true)
@SuppressWarnings("PMD.TooManyStaticImports")
public class BitbucketClientLiveTest extends BaseBitbucketApiLiveTest {

    private static final String DUMMY_ENDPOINT = "http://some-non-existent-host:12345";
    private static final String SYSTEM_JCLOUDS_TIMEOUT = "bitbucket.rest.jclouds.so-timeout";
    private static final String ENVIRONMENT_JCLOUDS_TIMEOUT = "BITBUCKET_REST_JCLOUDS_SO-TIMEOUT";

    @Test
    public void testCreateClient() {
        final BitbucketClient client = new BitbucketClient(this.endpoint, this.bitbucketAuthentication, null);
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

    @Test
    public void testCreateClientWithEndpointFromSystemProperties() {
        clearSystemProperties();

        System.setProperty(ENDPOINT_SYSTEM_PROPERTY, this.endpoint);
        final BitbucketClient client = new BitbucketClient(null, this.bitbucketAuthentication, null);
        assertThat(client.endPoint()).isEqualTo(this.endpoint);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isEmpty();
        clearSystemProperties();
    }

    @Test
    public void testCreateClientWithWrongEndpointFromSystemProperties() {
        clearSystemProperties();

        System.setProperty(ENDPOINT_SYSTEM_PROPERTY, DUMMY_ENDPOINT);
        final BitbucketClient client = new BitbucketClient(null, this.bitbucketAuthentication, null);
        assertThat(client.endPoint()).isEqualTo(DUMMY_ENDPOINT);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isNotEmpty();
        clearSystemProperties();
    }

    @Test
    public void testCreateClientWithAuthenticationFromSystemProperties() {
        clearSystemProperties();

        final AuthenticationType currentAuthType = this.bitbucketAuthentication.authType();
        final String correctAuth = this.bitbucketAuthentication.authValue();
        if (currentAuthType == AuthenticationType.Basic) {
            System.setProperty(CREDENTIALS_SYSTEM_PROPERTY, correctAuth);
        } else if (currentAuthType == AuthenticationType.Bearer) {
            System.setProperty(TOKEN_SYSTEM_PROPERTY, correctAuth);
        }

        final BitbucketClient client = new BitbucketClient(this.endpoint, null, null);
        assertThat(client.authType()).isEqualTo(currentAuthType);
        assertThat(client.authValue()).isEqualTo(correctAuth);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isEmpty();
        clearSystemProperties();
    }

    @Test
    public void testCreateClientWithWrongAuthenticationFromSystemProperties() {
        clearSystemProperties();

        final AuthenticationType currentAuthType = this.bitbucketAuthentication.authType();
        final String wrongAuth = TestUtilities.randomStringLettersOnly();
        if (currentAuthType == AuthenticationType.Basic) {
            System.setProperty(CREDENTIALS_SYSTEM_PROPERTY, wrongAuth);
        } else if (currentAuthType == AuthenticationType.Bearer) {
            System.setProperty(TOKEN_SYSTEM_PROPERTY, wrongAuth);
        }

        final BitbucketClient client = new BitbucketClient(this.endpoint, null, null);
        assertThat(client.authType()).isEqualTo(currentAuthType);
        assertThat(client.authValue()).isEqualTo(wrongAuth);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isNotEmpty();
        clearSystemProperties();
    }

    @Test
    public void testCreateClientWithEndpointFromEnvironmentVariables() {
        clearEnvironmentVariables(null);
        final Map<String, String> envVars = Maps.newHashMap();
        envVars.put(ENDPOINT_ENVIRONMENT_VARIABLE, this.endpoint);
        TestUtilities.addEnvironmentVariables(envVars);

        final BitbucketClient client = new BitbucketClient(null, this.bitbucketAuthentication, null);
        assertThat(client.endPoint()).isEqualTo(this.endpoint);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isEmpty();
        clearEnvironmentVariables(null);
    }

    @Test
    public void testCreateClientWithWrongEndpointFromEnvironmentVariables() {
        clearEnvironmentVariables(null);
        final Map<String, String> envVars = Maps.newHashMap();
        envVars.put(ENDPOINT_ENVIRONMENT_VARIABLE, DUMMY_ENDPOINT);
        TestUtilities.addEnvironmentVariables(envVars);

        final BitbucketClient client = new BitbucketClient(null, this.bitbucketAuthentication, null);
        assertThat(client.endPoint()).isEqualTo(DUMMY_ENDPOINT);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isNotEmpty();
        clearEnvironmentVariables(null);
    }

    @Test
    public void testCreateClientWithAuthenticationFromEnvironmentVariables() {
        clearEnvironmentVariables(null);

        final AuthenticationType currentAuthType = this.bitbucketAuthentication.authType();
        final String correctAuth = this.bitbucketAuthentication.authValue();
        final String correctAuthType;
        switch (currentAuthType) {
            case Basic:
                correctAuthType = CREDENTIALS_ENVIRONMENT_VARIABLE;
                break;
            case Bearer:
                correctAuthType = TOKEN_ENVIRONMENT_VARIABLE;
                break;
            default:
                correctAuthType = null;
                break;
        }

        final Map<String, String> envVars = Maps.newHashMap();
        envVars.put(correctAuthType, correctAuth);
        TestUtilities.addEnvironmentVariables(envVars);

        final BitbucketClient client = new BitbucketClient(this.endpoint, null, null);
        assertThat(client.authType()).isEqualTo(currentAuthType);
        assertThat(client.authValue()).isEqualTo(correctAuth);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isEmpty();
        clearEnvironmentVariables(null);
    }

    @Test
    public void testCreateClientWithWrongAuthenticationFromEnvironmentVariables() {
        clearEnvironmentVariables(null);

        final AuthenticationType currentAuthType = this.bitbucketAuthentication.authType();
        final String wrongAuth = TestUtilities.randomStringLettersOnly();
        final String correctAuthType;
        switch (currentAuthType) {
            case Basic:
                correctAuthType = CREDENTIALS_ENVIRONMENT_VARIABLE;
                break;
            case Bearer:
                correctAuthType = TOKEN_ENVIRONMENT_VARIABLE;
                break;
            default:
                correctAuthType = null;
                break;
        }

        final Map<String, String> envVars = Maps.newHashMap();
        envVars.put(correctAuthType, wrongAuth);
        TestUtilities.addEnvironmentVariables(envVars);

        final BitbucketClient client = new BitbucketClient(this.endpoint, null, null);
        assertThat(client.authType()).isEqualTo(currentAuthType);
        assertThat(client.authValue()).isEqualTo(wrongAuth);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isNotEmpty();
        clearEnvironmentVariables(null);
    }

    @Test
    public void testCreateClientWithOverridesAndFail() {
        final Properties properties = new Properties();
        properties.put("jclouds.so-timeout", -1);
        final BitbucketClient client = new BitbucketClient(this.endpoint, this.bitbucketAuthentication, properties);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isNotEmpty();
        assertThat(userPage.errors().get(0).context()).contains("timeouts can't be negative");
    }

    @Test
    public void testCreateClientWithOverridesFromSystemPropertiesAndFail() {
        System.clearProperty(SYSTEM_JCLOUDS_TIMEOUT);
        System.setProperty(SYSTEM_JCLOUDS_TIMEOUT, "-1");
        final BitbucketClient client = new BitbucketClient(this.endpoint, this.bitbucketAuthentication, null);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isNotEmpty();
        assertThat(userPage.errors().get(0).context()).contains("timeouts can't be negative");
        System.clearProperty(SYSTEM_JCLOUDS_TIMEOUT);
    }

    @Test
    public void testCreateClientWithOverridesFromEnvironmentVariablesAndFail() {
        final Map<String, String> envVars = Maps.newHashMap();
        envVars.put(ENVIRONMENT_JCLOUDS_TIMEOUT, "-1");
        TestUtilities.addEnvironmentVariables(envVars);

        final BitbucketClient client = new BitbucketClient(this.endpoint, this.bitbucketAuthentication, null);
        final UserPage userPage = client.api().adminApi().listUsers(null, 1, 1);
        assertThat(userPage).isNotNull();
        assertThat(userPage.errors()).isNotEmpty();
        assertThat(userPage.errors().get(0).context()).contains("timeouts can't be negative");

        clearEnvironmentVariables(envVars.keySet());
    }

    private void clearSystemProperties() {
        System.clearProperty(ENDPOINT_SYSTEM_PROPERTY);
        System.clearProperty(CREDENTIALS_SYSTEM_PROPERTY);
        System.clearProperty(TOKEN_SYSTEM_PROPERTY);
    }

    private void clearEnvironmentVariables(@Nullable final Collection optionalKeysToClear) {
        final List<String> envVars = Lists.newArrayList(ENDPOINT_SYSTEM_PROPERTY);
        envVars.add(CREDENTIALS_SYSTEM_PROPERTY);
        envVars.add(TOKEN_SYSTEM_PROPERTY);
        if (optionalKeysToClear != null) {
            envVars.addAll(optionalKeysToClear);
        }
        TestUtilities.removeEnvironmentVariables(envVars);
    }
}
