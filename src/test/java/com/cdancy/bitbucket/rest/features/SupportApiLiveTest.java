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
import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.domain.support.SupportZip;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "SupportApiLiveTest", singleThreaded = true)
public class SupportApiLiveTest extends BaseBitbucketApiLiveTest {
    private String taskIdRegex = "^\\w+\\-+\\w+\\-+\\w+\\-+\\w+\\-+\\w+$";

    private SupportApi api() {
        return api.supportApi();
    }

    @Test
    public void testSupportZipCreation() {
        final SupportZip supportZipTask = api().createSupportZip();
        assertIntegrity(supportZipTask);
    }

    @Test
    public void testSupportZipStatus() {
        SupportZip supportZipTask = api().createSupportZip();
        supportZipTask = api().getSupportZipStatus(supportZipTask.taskId());
        assertIntegrity(supportZipTask);
        assertThat(supportZipTask.fileName()).isNotNull();
    }

    private void assertIntegrity(final SupportZip supportZipTask) {
        assertTaskId(supportZipTask.taskId());
        assertTaskProgressPercentage(supportZipTask.progressPercentage());
        assertTaskProgressMessage(supportZipTask.progressMessage());
        assertTaskStatus(supportZipTask.status());
        assertErrors(supportZipTask);
    }

    private void assertErrors(final SupportZip supportZip) {
        assertThat(supportZip.errors().isEmpty()).isTrue();
    }

    private void assertTaskProgressMessage(final String progressMessage) {
        assertThat(progressMessage).isNotNull();
    }

    private void assertTaskStatus(final String status) {
        assertThat(!status.isEmpty()).isTrue();
    }

    private void assertTaskId(final String taskId) {
        assertThat(taskId).isNotNull();
        assertThat(taskId.matches(taskIdRegex)).isTrue();
    }

    private void assertTaskProgressPercentage(final Integer progressPercentage) {
        assertThat(progressPercentage).isNotNull();
    }
}
