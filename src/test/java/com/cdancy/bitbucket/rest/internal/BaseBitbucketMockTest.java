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

package com.cdancy.bitbucket.rest.internal;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.util.Strings2.toStringAndClose;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.google.common.base.Charsets;
import com.google.common.base.Functions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Base class for all Bitbucket mock tests.
 */
public class BaseBitbucketMockTest {

    protected final String restBasePath = "/rest/api/";
    protected String provider;
    private final JsonParser parser = new JsonParser();

    public BaseBitbucketMockTest() {
        provider = "bitbucket";
    }

    public BitbucketApi api(URL url) {
        return ContextBuilder.newBuilder(provider).endpoint(url.toString()).overrides(setupProperties())
                .buildApi(BitbucketApi.class);
    }

    protected Properties setupProperties() {
        Properties properties = new Properties();
        properties.setProperty(Constants.PROPERTY_MAX_RETRIES, "0");
        return properties;
    }

    /**
     * Create a MockWebServer.
     *
     * @return instance of MockWebServer
     * @throws IOException
     *             if unable to start/play server
     */
    public static MockWebServer mockWebServer() throws IOException {
        MockWebServer server = new MockWebServer();
        server.play();
        return server;
    }

    /**
     * Get the String representation of some resource to be used as payload.
     *
     * @param resource
     *            String representation of a given resource
     * @return payload in String form
     */
    public String payloadFromResource(String resource) {
        try {
            return new String(toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private static Map<String, String> extractParams(String path) {

        int qmIndex = path.indexOf('?');
        if (qmIndex <= 0) {
            return ImmutableMap.of();
        }

        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        String[] params = path.substring(qmIndex + 1).split("&");
        for (String param : params) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length > 1) {
                builder.put(keyValue[0], keyValue[1]);
            }
        }

        return builder.build();
    }

    protected RecordedRequest assertSent(MockWebServer server, String method, String path) throws InterruptedException {
        return assertSent(server, method, path, ImmutableMap.<String, Void> of());
    }

    protected RecordedRequest assertSent(MockWebServer server, String method, String expectedPath, Map<String, ?> queryParams)
            throws InterruptedException {

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(method);
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON);

        String path = request.getPath();
        String rawPath = path.contains("?") ? path.substring(0, path.indexOf('?')) : path;
        assertThat(rawPath).isEqualTo(expectedPath);

        Map<String, String> normalizedParams = Maps.transformValues(queryParams, Functions.toStringFunction());
        assertThat(normalizedParams).isEqualTo(extractParams(path));

        return request;
    }

    protected RecordedRequest assertSent(MockWebServer server, String method, String path, String json)
            throws InterruptedException {
        RecordedRequest request = assertSent(server, method, path);
        assertThat(APPLICATION_JSON).isEqualTo(request.getHeader("Content-Type"));
        assertThat(parser.parse(json)).isEqualTo(parser.parse(request.getUtf8Body()));
        return request;
    }

    protected RecordedRequest assertSentWithFormData(MockWebServer server, String method, String path, String body)
            throws InterruptedException {
        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(method);
        assertThat(request.getPath()).isEqualTo(path);
        assertThat(request.getUtf8Body()).isEqualTo(body);
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(request.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED);
        return request;
    }

    protected RecordedRequest assertSentAcceptText(MockWebServer server, String method, String path)
            throws InterruptedException {
        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(method);
        assertThat(request.getPath()).isEqualTo(path);
        assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.TEXT_PLAIN);
        return request;
    }
}
