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
import com.cdancy.bitbucket.rest.domain.build.StatusPage;
import com.cdancy.bitbucket.rest.domain.build.Summary;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "BuildStatusApiLiveTest")
public class BuildStatusApiLiveTest extends BaseBitbucketApiLiveTest {

    final String statusPageCommitHash = "5284b6cec569346855710b535dafb915423110c2";
    final String summaryPageCommitHash = "5284b6cec569346855710b535dafb915423110c2";

    @Test
    public void testGetStatusByCommit() {
        final StatusPage statusPage = api().status(statusPageCommitHash, 0, 100);
        assertThat(statusPage).isNotNull();
        assertThat(statusPage.size() > 0).isTrue();
    }
    
    @Test
    public void testGetStatusByNonExistentCommit() {
        final StatusPage statusPage = api().status(randomString(), 0, 100);
        assertThat(statusPage).isNotNull();
        assertThat(statusPage.size() == 0).isTrue();
    }

    @Test
    public void testGetSummaryByCommit() {
        final Summary summary = api().summary(summaryPageCommitHash);
        assertThat(summary).isNotNull();
        assertThat(summary.successful() == 1).isTrue();
        assertThat(summary.inProgress() == 0).isTrue();
        assertThat(summary.failed() == 0).isTrue();
    }
    
    @Test
    public void testGetSummaryByNonExistentCommit() {
        final Summary summary = api().summary(randomString());
        assertThat(summary).isNotNull();
        assertThat(summary.successful() == 0).isTrue();
        assertThat(summary.inProgress() == 0).isTrue();
        assertThat(summary.failed() == 0).isTrue();
    }

    private BuildStatusApi api() {
        return api.buildStatusApi();
    }
}
