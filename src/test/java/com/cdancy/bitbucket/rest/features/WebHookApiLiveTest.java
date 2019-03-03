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
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.TestUtilities;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.repository.WebHook;
import com.cdancy.bitbucket.rest.domain.repository.WebHookPage;
import com.cdancy.bitbucket.rest.options.CreateWebHook;
import com.google.common.collect.Lists;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.cdancy.bitbucket.rest.TestUtilities.randomStringLettersOnly;
import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "WebHookApiLiveTest", singleThreaded = true)
public class WebHookApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private final List<WebHook.EventType> eventTypes = Lists.newArrayList(WebHook.EventType.PR_COMENT_ADDED);
    private final CreateWebHook createWebHook = CreateWebHook.create(randomStringLettersOnly(),
        eventTypes,
        "http://www.google.com",
        true,
        null);

    private String projectKey;
    private String repoKey;

    private WebHook webHook;

    @BeforeClass
    public void init() {
        generatedTestContents = TestUtilities.initGeneratedTestContents(this.endpoint, this.bitbucketAuthentication, this.api);
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
    }

    @Test
    public void testCreateWebHook() {
        this.webHook = api().create(projectKey, repoKey, createWebHook);
        assertThat(webHook).isNotNull();
        assertThat(webHook.errors()).isEmpty();
    }

    @Test
    public void testCreateWebHookOnError() {
        final WebHook ref = api().create(projectKey, randomStringLettersOnly(), createWebHook);
        assertThat(ref).isNotNull();
        assertThat(ref.errors()).isNotEmpty();
    }

    @Test(dependsOnMethods = "testCreateWebHook")
    public void testGetWebHook() {
        final WebHook ref = api().get(projectKey, repoKey, webHook.id());
        assertThat(ref).isNotNull();
        assertThat(ref.errors()).isEmpty();
    }

    @Test
    public void testGetWebHookOnError() {
        final WebHook ref = api().get(projectKey, randomStringLettersOnly(), randomStringLettersOnly());
        assertThat(ref).isNotNull();
        assertThat(ref.errors()).isNotEmpty();
    }

    @Test(dependsOnMethods = "testGetWebHook")
    public void testListWebHooks() throws Exception {

        final List<WebHook> allWebHooks = Lists.newArrayList();
        Integer start = null;
        while (true) {
            final WebHookPage ref = api().list(projectKey, repoKey, start, 100);
            assertThat(ref.errors().isEmpty()).isTrue();

            allWebHooks.addAll(ref.values());
            start = ref.nextPageStart();
            if (ref.isLastPage()) {
                break;
            } else {
                System.out.println("Sleeping for 1 seconds before querying for next page");
                Thread.sleep(1000);
            }
        }
        assertThat(allWebHooks.size() > 0).isEqualTo(true);
    }

    @Test
    public void testListWebHooksOnError() {
        final WebHookPage ref = api().list(projectKey, randomStringLettersOnly(), null, 100);
        assertThat(ref).isNotNull();
        assertThat(ref.errors()).isNotEmpty();
    }

    @Test(dependsOnMethods = "testListWebHooks")
    public void testUpdateWebHook() {
        final List<WebHook.EventType> updateEventTypes = Lists.newArrayList(WebHook.EventType.PR_COMENT_DELETED);
        final CreateWebHook updateWebHook = CreateWebHook.create(webHook.name(),
            updateEventTypes,
            "http://www.google.com",
            true,
            null);

        this.webHook = api().update(projectKey, repoKey, webHook.id(), updateWebHook);
        assertThat(webHook).isNotNull();
        assertThat(webHook.errors()).isEmpty();
    }

    @Test
    public void testUpdateWebHookOnError() {
        final WebHook ref = api().update(projectKey,
            randomStringLettersOnly(),
            randomStringLettersOnly(),
            createWebHook);
        assertThat(ref).isNotNull();
        assertThat(ref.errors()).isNotEmpty();
    }

    @Test(dependsOnMethods = "testUpdateWebHook")
    public void testDeleteWebHook() {
        final RequestStatus ref = api().delete(projectKey, repoKey, webHook.id());
        assertThat(ref).isNotNull();
        assertThat(ref.value()).isTrue();
        assertThat(ref.errors()).isEmpty();
    }

    @Test
    public void testDeleteWebHookOnError() {
        final RequestStatus ref = api().delete(projectKey,
            randomStringLettersOnly(),
            randomStringLettersOnly());
        assertThat(ref).isNotNull();
        assertThat(ref.value()).isFalse();
        assertThat(ref.errors()).isNotEmpty();
    }

    @AfterClass
    public void fin() {
        TestUtilities.terminateGeneratedTestContents(this.api, generatedTestContents);
    }

    private WebHookApi api() {
        return api.webHookApi();
    }
}
