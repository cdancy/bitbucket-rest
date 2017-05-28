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

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.cdancy.bitbucket.rest.config.BitbucketHttpApiModule;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@AutoService(ApiMetadata.class)
public class BitbucketApiMetadata extends BaseHttpApiMetadata<BitbucketApi> {

    public static final String API_VERSION = "1.0";
    public static final String BUILD_VERSION = "4.5";

    @Override
    public Builder toBuilder() {
        return new Builder().fromApiMetadata(this);
    }

    public BitbucketApiMetadata() {
        this(new Builder());
    }

    protected BitbucketApiMetadata(final Builder builder) {
        super(builder);
    }

    public static Properties defaultProperties() {
        Properties properties = BaseHttpApiMetadata.defaultProperties();
        return properties;
    }

    public static class Builder extends BaseHttpApiMetadata.Builder<BitbucketApi, Builder> {

        protected Builder() {
            super(BitbucketApi.class);
            id("bitbucket").name("Bitbucket API")
                    .identityName("Optional Username")
                    .credentialName("Optional Password")
                    .defaultIdentity("").defaultCredential("")
                    .documentation(URI.create("https://developer.atlassian.com/static/rest/bitbucket-server/latest/bitbucket-rest.html"))
                    .version(API_VERSION).buildVersion(BUILD_VERSION).defaultEndpoint("http://127.0.0.1:7990")
                    .defaultProperties(BitbucketApiMetadata.defaultProperties())
                    .defaultModules(ImmutableSet.<Class<? extends Module>> of(BitbucketHttpApiModule.class));
        }

        @Override
        public BitbucketApiMetadata build() {
            return new BitbucketApiMetadata(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Builder fromApiMetadata(final ApiMetadata in) {
            return this;
        }
    }
}
