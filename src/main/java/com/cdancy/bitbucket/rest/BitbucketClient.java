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

package com.cdancy.bitbucket.rest;

import com.cdancy.bitbucket.rest.auth.AuthenticationType;
import com.cdancy.bitbucket.rest.config.BitbucketAuthenticationModule;
import com.google.common.collect.Lists;
import java.util.Properties;
import org.jclouds.ContextBuilder;
import org.jclouds.javax.annotation.Nullable;

public final class BitbucketClient {

    private final String endPoint;
    private final BitbucketAuthentication credentials;
    private final BitbucketApi bitbucketApi;
    private final Properties overrides;

    /**
     * Create a BitbucketClient inferring endpoint and authentication from
     * environment and system properties.
     */
    public BitbucketClient() {
        this(null, null, null);
    }

    /**
     * Create an BitbucketClient. If any of the passed in variables are null we
     * will query System Properties and Environment Variables, in order, to 
     * search for values that may be set in a devops/CI fashion. The only
     * difference is the `overrides` which gets merged, but takes precedence,
     * with those System Properties and Environment Variables found.
     *
     * @param endPoint URL of Bitbucket instance.
     * @param authentication authentication used to connect to Bitbucket instance.
     * @param overrides jclouds Properties to override defaults when creating a new BitbucketApi.
     */
    public BitbucketClient(@Nullable final String endPoint,
            @Nullable final BitbucketAuthentication authentication,
            @Nullable final Properties overrides) {
        this.endPoint = endPoint != null
                ? endPoint
                : BitbucketUtils.inferEndpoint();
        this.credentials = authentication != null
                ? authentication
                : BitbucketUtils.inferAuthentication();
        this.overrides = mergeOverrides(overrides);
        this.bitbucketApi = createApi(this.endPoint, this.credentials, this.overrides);
    }

    private BitbucketApi createApi(final String endPoint, final BitbucketAuthentication authentication, final Properties overrides) {
        return ContextBuilder
                .newBuilder(new BitbucketApiMetadata.Builder().build())
                .endpoint(endPoint)
                .modules(Lists.newArrayList(new BitbucketAuthenticationModule(authentication)))
                .overrides(overrides)
                .buildApi(BitbucketApi.class);
    }

    /**
     * Query System Properties and Environment Variables for overrides and merge
     * the potentially passed in overrides with those.
     * 
     * @param possibleOverrides Optional passed in overrides.
     * @return Properties object.
     */
    private Properties mergeOverrides(final Properties possibleOverrides) {
        final Properties inferOverrides = BitbucketUtils.inferOverrides();
        if (possibleOverrides != null) {
            inferOverrides.putAll(possibleOverrides);
        }
        return inferOverrides;
    }

    public String endPoint() {
        return this.endPoint;
    }

    @Deprecated
    public String credentials() {
        return this.authValue();
    }

    public Properties overrides() {
        return this.overrides;
    }

    public String authValue() {
        return this.credentials.authValue();
    }

    public AuthenticationType authType() {
        return this.credentials.authType();
    }

    public BitbucketApi api() {
        return this.bitbucketApi;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String endPoint;
        private BitbucketAuthentication.Builder authBuilder;
        private Properties overrides;

        /**
         * Define the base endpoint to connect to.
         * 
         * @param endPoint Bitbucket base endpoint.
         * @return this Builder.
         */
        public Builder endPoint(final String endPoint) {
            this.endPoint = endPoint;
            return this;
        }

        /**
         * Optional credentials to use for authentication. Must take the form of
         * `username:password` or its base64 encoded version.
         * 
         * @param optionallyBase64EncodedCredentials authentication credentials.
         * @return this Builder.
         */
        public Builder credentials(final String optionallyBase64EncodedCredentials) {
            authBuilder = BitbucketAuthentication.builder()
                    .credentials(optionallyBase64EncodedCredentials);
            return this;
        }

        /**
         * Optional token to use for authentication. 
         *
         * @param token authentication token.
         * @return this Builder.
         */
        public Builder token(final String token) {
            authBuilder = BitbucketAuthentication.builder()
                    .token(token);
            return this;
        }

        /**
         * Optional jclouds Properties to override. What can be overridden can
         * be found here:
         * 
         * <p>https://github.com/jclouds/jclouds/blob/master/core/src/main/java/org/jclouds/Constants.java
         *
         * @param overrides optional jclouds Properties to override.
         * @return this Builder.
         */
        public Builder overrides(final Properties overrides) {
            this.overrides = overrides;
            return this;
        }

        /**
         * Build an instance of BitbucketClient.
         * 
         * @return BitbucketClient
         */
        public BitbucketClient build() {

            // 1.) If user passed in some auth use/build that.
            final BitbucketAuthentication authentication = authBuilder != null
                    ? authBuilder.build()
                    : null;

            return new BitbucketClient(endPoint, authentication, overrides);
        } 
    }
}
