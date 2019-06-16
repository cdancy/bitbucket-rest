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
import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.domain.support.SupportZip;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Mock tests for the {@link SupportApi} class.
 */
@Test(groups = "unit", testName = "SupportApiMockTest")
public class SupportApiMockTest extends BaseBitbucketMockTest {

    private String taskIdRegex = "^\\w+\\-+\\w+\\-+\\w+\\-+\\w+\\-+\\w+$";
    private String supportZipRestApiPath = "/rest/troubleshooting/latest/support-zip" ;

    public void testSupportZipCreation() throws Exception {
        MockWebServer mockWebServer = mockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody(payloadFromResource("/support-zip-create.json")).setResponseCode(200));

        try {
            final SupportApi supportApi = api(mockWebServer.getUrl("/")).supportApi() ;
            final SupportZip supportZip = supportApi.createSupportZip();

            assertSupportZipCreateRequest(mockWebServer);
            assertIntegrity(supportZip);
        } finally {
            mockWebServer.shutdown();
        }
    }

    public void testSupportZipStatus() throws Exception {
        MockWebServer mockWebServer = mockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody(payloadFromResource("/support-zip-create.json")).setResponseCode(200));
        mockWebServer.enqueue(new MockResponse().setBody(payloadFromResource("/support-zip-request-status.json")).setResponseCode(200));

        try {
            final SupportApi supportApi = api(mockWebServer.getUrl("/")).supportApi() ;
            SupportZip supportZip = supportApi.createSupportZip();
            supportZip = supportApi.getSupportZipStatus(supportZip.taskId());

            assertSupportZipCreateRequest(mockWebServer);
            assertIntegritySupportZipStatus(supportZip);
            assertSupportZipStatusRequest(mockWebServer, supportZip.taskId());
        } finally {
            mockWebServer.shutdown();
        }
    }

    private void assertIntegritySupportZipStatus(final SupportZip supportZip) {
        assertIntegrity(supportZip);
        Assert.assertNotNull(supportZip.fileName());
    }

    private void assertIntegrity(final SupportZip supportZip) {
        Assert.assertNotNull(supportZip.progressMessage());
        assertThat(supportZip.taskId().matches(taskIdRegex)).isTrue();
        Assert.assertNotNull(supportZip.progressPercentage());
        Assert.assertNotNull(supportZip.status());
    }

    private void assertSupportZipCreateRequest(final MockWebServer mockWebServer)
        throws InterruptedException {
        assertSent(mockWebServer, "POST", supportZipRestApiPath + "/local");
    }

    private void assertSupportZipStatusRequest(final MockWebServer mockWebServer,
                                              final String taskId) throws InterruptedException {
        assertSent(mockWebServer, "GET", supportZipRestApiPath + "/status/task/" + taskId);
    }
}
