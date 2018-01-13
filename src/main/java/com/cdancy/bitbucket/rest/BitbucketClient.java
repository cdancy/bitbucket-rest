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
import java.util.Objects;
import org.jclouds.ContextBuilder;
import org.jclouds.javax.annotation.Nullable;

public final class BitbucketClient {

    private final String endPoint;
    private final BitbucketAuthentication credentials;
    private final BitbucketApi bitbucketApi;

    /**
     * Create a BitbucketClient inferring endpoint and authentication from
     * environment and system properties.
     */
    public BitbucketClient() {
        this.endPoint = BitbucketUtils.inferEndpoint();
        this.credentials = BitbucketUtils.inferCredentials();
        this.bitbucketApi = createApi(this.endPoint(), credentials);
    }

    /**
     * Create a BitbucketClient inferring authentication from
     * environment and system properties.
     *
     * @param endPoint URL of Bitbucket instance
     */
    @Deprecated //@Nullable annotation will be removed in 2.x releases
    public BitbucketClient(@Nullable final String endPoint) {
        this.endPoint = endPoint != null
                ? endPoint
                : BitbucketUtils.inferEndpoint();
        this.credentials = BitbucketUtils.inferCredentials();
        this.bitbucketApi = createApi(this.endPoint(), credentials);
    }

    /**
     * Create an BitbucketClient using the passed in endpoint and Basic credential String.
     *
     * @param endPoint URL of Bitbucket instance
     * @param basicCredentials the optional credentials for the Bitbucket instance
     */
    @Deprecated //Constructor will be deleted in favor of 'String, BitbucketCredentials' version in 2.x releases
    public BitbucketClient(@Nullable final String endPoint, @Nullable final String basicCredentials) {
        this.endPoint = endPoint != null
                ? endPoint
                : BitbucketUtils.inferEndpoint();
        this.credentials = basicCredentials != null
                ? BitbucketAuthentication.builder().credentials(basicCredentials).build()
                : BitbucketUtils.inferCredentials();
        this.bitbucketApi = createApi(this.endPoint(), credentials);
    }

    /**
     * Create an BitbucketClient using the passed in endpoint and BitbucketCredentials instance.
     *
     * @param endPoint URL of Bitbucket instance
     * @param credentials Credentials used to connect to Bitbucket instance.
     */
    public BitbucketClient(final String endPoint, final BitbucketAuthentication credentials) {
        this.endPoint = endPoint;
        this.credentials = credentials;
        this.bitbucketApi = createApi(endPoint, Objects.requireNonNull(credentials));
    }

    private BitbucketApi createApi(final String endPoint, final BitbucketAuthentication authentication) {
        return ContextBuilder
                .newBuilder(new BitbucketApiMetadata.Builder().build())
                .endpoint(endPoint)
                .modules(Lists.newArrayList(new BitbucketAuthenticationModule(authentication)))
                .buildApi(BitbucketApi.class);
    }

    public String endPoint() {
        return this.endPoint;
    }

    @Deprecated
    public String credentials() {
        return authValue();
    }

    public String authValue() {
        return credentials.authValue();
    }

    public AuthenticationType authType() {
        return credentials.authType();
    }

    public BitbucketApi api() {
        return bitbucketApi;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String endPoint;
        private BitbucketAuthentication.Builder authBuilder;

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
         * Build an instance of BitbucketClient.
         * 
         * @return BitbucketClient
         */
        public BitbucketClient build() {

            // 1.) Use passed-in endpoint or attempt to infer one from system/environment.
            final String foundEndpoint = (endPoint != null) ? endPoint : BitbucketUtils.inferEndpoint();

            // 2.) Use passed-in credentials or attempt to infer them from system/environment.
            final BitbucketAuthentication authentication = authBuilder != null
                    ? authBuilder.build()
                    : BitbucketUtils.inferCredentials();

            return new BitbucketClient(foundEndpoint, authentication);
        } 
    }
}
