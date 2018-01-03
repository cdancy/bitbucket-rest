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

import static com.google.common.io.BaseEncoding.base64;

import com.cdancy.bitbucket.rest.utils.AuthTypes;
import com.cdancy.bitbucket.rest.utils.BitbucketUtils;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jclouds.ContextBuilder;
import org.jclouds.domain.Credentials;

public final class BitbucketClient {

    private static final List<String> ENDPOINT_PROPERTIES = Collections
            .unmodifiableList(Arrays
                    .asList("bitbucket.rest.endpoint", "bitbucketRestEndpoint", "BITBUCKET_REST_ENDPOINT"));
    private static final List<String> CREDENTIALS_PROPERTIES = Collections
            .unmodifiableList(Arrays
                    .asList("bitbucket.rest.credentials", "bitbucketRestCredentials", "BITBUCKET_REST_CREDENTIALS"));

    private final BitbucketApi bitbucketApi;

    /**
     * Create an BitbucketClient.
     *
     * @param endPoint URL of Bitbucket instance
     * @param credentials Supplier for Credentials
     */
    private BitbucketClient(final String endPoint, final Supplier<Credentials> credentials) {
        this.bitbucketApi = createApi(endPoint, credentials);
    }

    private BitbucketApi createApi(final String endPoint, final Supplier<Credentials> credentials) {
        return ContextBuilder
                .newBuilder(new BitbucketApiMetadata.Builder().build())
                .endpoint(endPoint)
                .credentialsSupplier(credentials)
                .buildApi(BitbucketApi.class);
    }

    public BitbucketApi api() {
        return bitbucketApi;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String endPoint;
        private LocalCredentials credentials;

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
         * @param credentials authentication credentials.
         * @return this Builder.
         */
        public Builder credentials(final String credentials) {
            this.credentials = new LocalCredentials(credentials, AuthTypes.BASIC);
            return this;
        }

        /**
         * Optional token to use for authentication. 
         * 
         * @param token authentication token.
         * @return this Builder.
         */
        public Builder token(final String token) {
            this.credentials = new LocalCredentials(token, AuthTypes.BEARER);
            return this;
        }

        /**
         * Find Bitbucket endpoint from system/environment.
         *
         * @return found endpoint or default.
         */
        private String findEndpoint() {
            final String possibleValue = BitbucketUtils.retrivePropertyValue(ENDPOINT_PROPERTIES);
            return possibleValue != null ? possibleValue : "http://127.0.0.1:7990";
        }

        /**
         * Find Bitbucket credentials from system/environment. These can be defined in
         * the following ways:
         *
         *<p>1.) hello:world // Username and Password delimited by colon: assumes 'Basic' auth.
         *   2.) aGVsbG86d29ybGQ= // Base64 version of Username and Password delimited by colon: assumes 'Basic' auth.
         *   3.) basic@hello:world // Same as #1 but defines auth to be 'Basic'
         *   4.) basic@aGVsbG86d29ybGQ= // Same as #2 but defines auth to be 'Basic'
         *   5.) @aGVsbG86d29ybGQ= // Same as #4 and assumes 'Basic' auth if none specified.
         *   6.) bearer@some-token // Use Bearer/Token auth.
         *
         * @return Credentials.
         */
        private LocalCredentials findCredentials() {
            final String authValue = BitbucketUtils.retrivePropertyValue(CREDENTIALS_PROPERTIES);

            // if NO auth found assumed anonymous access
            if (authValue != null) {

                String value;
                final int index = authValue.indexOf('@');
                AuthTypes authTypes;
                if (index != -1) {

                    // Addresses #5 above
                    if (index == 0) {
                        value = authValue.substring(1).trim();
                        authTypes = AuthTypes.BASIC;
                        if (value.length() == 0) {
                            throw new RuntimeException(authTypes + " authentication was inferred but no credentials found: "
                                    + "credentials=" + authValue);
                        }

                    // Addresses #3, #4, and #6 above
                    } else {
                        final String possibleAuth = authValue.substring(0, index);
                        value = authValue.substring(index + 1, authValue.length());
                        authTypes = AuthTypes.from(possibleAuth);
                        if (authTypes == null) {
                            throw new RuntimeException("Unknown authentication type was defined: "
                                    + "intialValue=" + authValue
                                    + ", inferredType=" + possibleAuth);
                        }
                    }
                } else {
                    // Addresses #1 and #2 above
                    value = authValue;
                    authTypes = AuthTypes.BASIC;
                }

                return new LocalCredentials(value, authTypes);
            } else {
                return new LocalCredentials("", null);
            }
        }

        // simple data-structure to hold type of credential and its value
        private class LocalCredentials {
            public final String value;
            public final AuthTypes type;

            public LocalCredentials(final String value, final AuthTypes type) {
                this.value = value;
                this.type = type;
            }
        }

        /**
         * Create an instance of BitbucketClient from this builder.
         * 
         * @return instance of BitbucketClient
         */
        public BitbucketClient build() {
            // 1.) Use passed-in endpoint or attempt to find one.
            final String foundEndpoint = (endPoint != null) ? endPoint : findEndpoint();

            // 2.) Use passed-in credentials or attempt to find them.
            final Credentials.Builder authCredentials = new Credentials.Builder<>();
            final LocalCredentials creds = credentials != null ? credentials : findCredentials();
            if (creds.type != null) {
                authCredentials.identity(creds.type.toString());
                if (creds.type == AuthTypes.BASIC && creds.value.contains(":")) {
                    final String encodedCreds = base64().encode(creds.value.getBytes());
                    authCredentials.credential(encodedCreds);
                } else {
                    authCredentials.credential(creds.value);
                }
            } else {
                authCredentials.identity(null).credential("");
            }

            return new BitbucketClient(foundEndpoint, Suppliers.ofInstance(authCredentials.build()));
        }
    }
}
