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

import org.jclouds.ContextBuilder;
import org.jclouds.javax.annotation.Nullable;

public final class BitbucketClient {

    private static final String[] ENDPOINT_PROPERTIES = { "bitbucket.rest.endpoint", "bitbucketRestEndpoint", "BITBUCKET_REST_ENDPOINT" };
    private static final String[] CREDENTIALS_PROPERTIES = { "bitbucket.rest.credentials", "bitbucketRestCredentials", "BITBUCKET_REST_CREDENTIALS" };
    private final String endPoint;
    private final String credentials;
    private final BitbucketApi bitbucketApi;

    /**
     * Create an BitbucketClient. We will query system properties and environment
     * variables for the endPoint and credentials.
     */
    public BitbucketClient() {
        this.endPoint = initEndPoint();
        this.credentials = initCredentials();
        this.bitbucketApi = createApi(this.endPoint(), this.credentials());
    }

    /**
     * Create an BitbucketClient.
     *
     * @param endPoint url of Bitbucket instance
     */
    public BitbucketClient(@Nullable final String endPoint) {
        this.endPoint = endPoint != null ? endPoint : initEndPoint();
        this.credentials = initCredentials();
        this.bitbucketApi = createApi(this.endPoint(), this.credentials());
    }

    /**
     * Create an BitbucketClient.
     *
     * @param endPoint url of Bitbucket instance
     * @param credentials the optional credentials for the etcd instance
     */
    public BitbucketClient(@Nullable final String endPoint, @Nullable final String credentials) {
        this.endPoint = endPoint != null ? endPoint : initEndPoint();
        this.credentials = credentials != null ? credentials : initCredentials();
        this.bitbucketApi = createApi(this.endPoint(), this.credentials());
    }

    /**
     * Initialize endPoint.
     *
     * @return found endpoint or null
     */
    private String initEndPoint() {
        String possibleValue = retrivePropertyValue(ENDPOINT_PROPERTIES);
        return possibleValue != null ? possibleValue : "http://127.0.0.1:7990";
    }

    /**
     * Initialize credentials.
     *
     * @return found credentials or empty String
     */
    private String initCredentials() {
        String possibleValue = retrivePropertyValue(CREDENTIALS_PROPERTIES);
        return possibleValue != null ? possibleValue : "";
    }

    public BitbucketApi createApi(String endPoint, String credentials) {
        return ContextBuilder.newBuilder(new BitbucketApiMetadata.Builder().build()).endpoint(endPoint)
                .credentials("N/A", credentials).buildApi(BitbucketApi.class);
    }

    /**
     * Retrieve property value from list of keys.
     *
     * @param keys list of keys to search
     * @return the first value found from list of keys
     */
    private String retrivePropertyValue(String... keys) {
        String value = null;
        for (String possibleKey : keys) {
            value = retrivePropertyValue(possibleKey);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    /**
     * Retrieve property value from key.
     *
     * @param key the key to search for
     * @return the value of key or null if not found
     */
    private String retrivePropertyValue(String key) {
        String value = System.getProperty(key);
        return value != null ? value : System.getenv(key);
    }

    public String endPoint() {
        return endPoint;
    }

    public String credentials() {
        return credentials;
    }

    public BitbucketApi api() {
        return bitbucketApi;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        
        private String endPoint;
        private String credentials;

        public Builder endPoint(String endPoint) {
            this.endPoint = endPoint;
            return this;
        }

        public Builder credentials(String credentials) {
            this.credentials = credentials;
            return this;
        }

        public BitbucketClient build() {
            return new BitbucketClient(endPoint, credentials);
        }
    }
}
