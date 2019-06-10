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

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.domain.support.SupportZipStatus;
import com.cdancy.bitbucket.rest.domain.support.SupportZipDetails;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "SupportApiLiveTest", singleThreaded = true)
public class SupportApiLiveTest extends BaseBitbucketApiLiveTest {
    private SupportApi api() {
        return api.supportApi();
    }

    @Test
    public void testSupportZipCreation() {
        final SupportZipDetails details = api().createSupportZip();
        assertIntegrity(details);
    }

    @Test
    public void testSupportZipStatus() {
        final SupportZipDetails details = api().createSupportZip();
        final SupportZipStatus supportZip = api().getSupportZipStatus(details.taskId());
        assertIntegrity(supportZip);
    }

    private void assertIntegrity(final SupportZipStatus supportZip) {
        assertTaskId(supportZip.taskId());
        assertTaskProgressMessage(supportZip.progressMessage());
        assertTaskProgressPercentage(supportZip.progressPercentage());
        assertTaskStatus(supportZip.status());
        Assert.assertNotNull(supportZip.fileName());
    }

    private void assertIntegrity(final SupportZipDetails supportZipTask) {
        assertTaskId(supportZipTask.taskId());
        assertTaskProgressPercentage(supportZipTask.progressPercentage());
        assertTaskProgressMessage(supportZipTask.progressMessage());
        assertTaskStatus(supportZipTask.status());
    }

    private void assertTaskProgressMessage(final String progressMessage) {
        Assert.assertNotNull(progressMessage);
    }

    private void assertTaskStatus(final String status) {
        Assert.assertTrue(!status.isEmpty());
    }

    private void assertTaskId(final String taskId) {
        Assert.assertNotNull(taskId);
    }

    private void assertTaskProgressPercentage(final Integer progressPercentage) {
        Assert.assertNotNull(progressPercentage);
    }
}
