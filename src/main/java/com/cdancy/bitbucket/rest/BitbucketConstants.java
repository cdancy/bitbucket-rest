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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Various constants that can be used in a global context.
 */
public class BitbucketConstants {

    public static final List<String> ENDPOINT_PROPERTIES = Collections
            .unmodifiableList(Arrays
                    .asList("bitbucket.rest.endpoint", "bitbucketRestEndpoint", "BITBUCKET_REST_ENDPOINT"));
    public static final List<String> CREDENTIALS_PROPERTIES = Collections
            .unmodifiableList(Arrays
                    .asList("bitbucket.rest.credentials", "bitbucketRestCredentials", "BITBUCKET_REST_CREDENTIALS"));
    public static final List<String> TOKEN_PROPERTIES = Collections
            .unmodifiableList(Arrays
                    .asList("bitbucket.rest.token", "bitbucketRestToken", "BITBUCKET_REST_TOKEN"));

    public static final String DEFAULT_ENDPOINT = "http://127.0.0.1:7990";

    public static final String JCLOUDS_PROPERTY_ID = "jclouds.";
    public static final String BITBUCKET_REST_PROPERTY_ID = "bitbucket.rest." + JCLOUDS_PROPERTY_ID;

    public static final String JCLOUDS_VARIABLE_ID = "JCLOUDS_";
    public static final String BITBUCKET_REST_VARIABLE_ID = "BITBUCKET_REST_" + JCLOUDS_VARIABLE_ID;

    protected BitbucketConstants() {
        throw new UnsupportedOperationException("Purposefully not implemented");
    }
}
