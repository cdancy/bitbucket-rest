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

import com.cdancy.bitbucket.rest.domain.tags.Tag;
import com.cdancy.bitbucket.rest.options.CreateTag;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "TagApiLiveTest", singleThreaded = true)
public class TagApiLiveTest extends BaseBitbucketApiLiveTest {

    /*
    String projectKey = randomStringLettersOnly();
    String repoKey = randomStringLettersOnly();
    String tagName = randomStringLettersOnly();
    String commitHash = "";
    */

    String projectKey = "TEST";
    String repoKey = "dev";
    String tagName = randomStringLettersOnly();
    String commitHash = "d90ca08fa076e2e4c076592fce3832aba80a494f";

    @Test
    public void testCreateTag() {
        CreateTag createTag = CreateTag.create(tagName, commitHash, null);
        Tag tag = api().create(projectKey, repoKey, createTag);
        assertNotNull(tag);
        assertTrue(tag.errors().size() == 0);
        assertTrue(tag.id().endsWith(tagName));
        assertTrue(tag.latestCommit().equalsIgnoreCase(commitHash));
    }

    @Test (dependsOnMethods = "testCreateTag")
    public void testGetTag() {
        Tag tag = api().get(projectKey, repoKey, tagName);
        assertNotNull(tag);
        assertTrue(tag.errors().size() == 0);
        assertTrue(tag.id().endsWith(tagName));
        assertTrue(tag.latestCommit().equalsIgnoreCase(commitHash));
    }

    @Test
    public void testGetTagNonExistent() {
        Tag tag = api().get(projectKey, repoKey, tagName + "9999");
        assertNull(tag);
    }

    private TagApi api() { return api.tagApi(); }
}
