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

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

import javax.inject.Singleton;

@Singleton
public class ScrubNullFromPathFilter implements HttpRequestFilter {

    private static final String SCRUB_NULL_PARAM = "%7B.+?%7D";
    private static final String FORWARD_SLASH = "/";
    private static final String DOUBLE_FORWARD_SLASH = "\\" + FORWARD_SLASH + "\\" + FORWARD_SLASH;
    private static final String EMPTY_STRING = "";
    private static final char FORWARD_SLASH_CHAR = '/';

    @Override
    public HttpRequest filter(final HttpRequest request) throws HttpException {
        String requestPath = request.getEndpoint().getRawPath()
                .replaceAll(SCRUB_NULL_PARAM, EMPTY_STRING)
                .replaceAll(DOUBLE_FORWARD_SLASH, FORWARD_SLASH);
        if (requestPath.charAt(requestPath.length() - 1) == FORWARD_SLASH_CHAR) {
            requestPath = requestPath.substring(0, requestPath.length() - 1);
        }
        return request.toBuilder().replacePath(requestPath).build();
    }
}
