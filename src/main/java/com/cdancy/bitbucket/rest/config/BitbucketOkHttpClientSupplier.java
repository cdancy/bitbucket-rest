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

package com.cdancy.bitbucket.rest.config;

import java.io.File;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import com.google.inject.Inject;
import javax.inject.Singleton;
import javax.net.SocketFactory;


import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.common.base.Supplier;

import org.jclouds.domain.Credentials;
import org.jclouds.http.okhttp.OkHttpClientSupplier;
import org.jclouds.location.Provider;
import org.jclouds.http.HttpUtils;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.TlsVersion;

@Singleton
public class BitbucketOkHttpClientSupplier implements OkHttpClientSupplier {

    private final HttpUtils utils;

    @Inject
    BitbucketOkHttpClientSupplier(HttpUtils utils) {
        this.utils = utils;
    }

    @Override
    public OkHttpClient get() {
        try {

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(utils.getConnectionTimeout(), TimeUnit.MILLISECONDS);
            okHttpClient.setReadTimeout(utils.getSocketOpenTimeout(), TimeUnit.MILLISECONDS);

            if (utils.relaxHostname()) {
                okHttpClient.setHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
                        return true;
                    }
                });
            }

            if (utils.trustAllCerts()) {
                okHttpClient.setSslSocketFactory(getTrustingSocketFactory());
            }

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private SSLSocketFactory getTrustingSocketFactory() throws Exception {

        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }
        };

        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // Create an ssl socket factory with our all-trusting manager
        return sslContext.getSocketFactory();
    }
}
