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
import com.cdancy.bitbucket.rest.GeneratedTestContents;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.file.Line;
import com.cdancy.bitbucket.rest.domain.file.LinePage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.google.common.collect.Lists;
import java.util.List;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import org.testng.annotations.BeforeClass;

@Test(groups = "live", testName = "FileApiLiveTest", singleThreaded = true)
public class FileApiLiveTest extends BaseBitbucketApiLiveTest {

    private GeneratedTestContents generatedTestContents;

    private String projectKey;
    private String repoKey;
    private String commitHashOne;
    private String commitHashTwo;
    private String filePath;
    private String content;

    @BeforeClass
    public void init() {
        generatedTestContents = initGeneratedTestContents();
        this.projectKey = generatedTestContents.project.key();
        this.repoKey = generatedTestContents.repository.name();
        
        final CommitPage commitPage = api.commitsApi().list(projectKey, repoKey, true, 10, null);
        assertThat(commitPage).isNotNull();
        assertThat(commitPage.errors().isEmpty()).isTrue();
        assertThat(commitPage.values().isEmpty()).isFalse();
        assertThat(commitPage.totalCount() > 0).isTrue();
        this.commitHashOne = commitPage.values().get(0).id();
        this.commitHashTwo = commitPage.values().get(1).id();
        
        final ChangePage commit = api.commitsApi().listChanges(projectKey, repoKey, commitHashOne, 0, 100);
        assertThat(commit).isNotNull();
        assertThat(commit.errors().isEmpty()).isTrue();
        assertThat(commit.size() > 0).isTrue();
        this.filePath = commit.values().get(0).path()._toString();
    }
    
    @Test
    public void getContent() {
        this.content = api().getContent(projectKey, repoKey, filePath, commitHashOne);
        assertThat(content).isNotNull();
    }
    
    @Test (dependsOnMethods = "getContent")
    public void listLines() throws Exception {
        final List<Line> allLines = Lists.newArrayList();
        Integer start = null;
        while (true) {
            final LinePage linePage = api().listLines(projectKey, repoKey, this.filePath, null, null, null, null, start, 100);
            assertThat(linePage.errors().isEmpty()).isTrue();
            
            allLines.addAll(linePage.values());
            start = linePage.nextPageStart();
            if (linePage.isLastPage()) {
                break;
            } else {
                System.out.println("Sleeping for 1 seconds before querying for next page");
                Thread.sleep(1000);
            }
        }
        assertThat(allLines.size() > 0).isEqualTo(true);
        final StringBuilder possibleContent = new StringBuilder();
        for (final Line possibleLine : allLines) {
            possibleContent.append(possibleLine.text());
        }
        assertThat(possibleContent.toString()).isEqualTo(this.content.trim());
    }
    
    @Test (dependsOnMethods = "getContent")
    public void listLinesAtCommit() throws Exception {
        final List<Line> allLines = Lists.newArrayList();
        Integer start = null;
        while (true) {
            final LinePage linePage = api().listLines(projectKey, repoKey, this.filePath, this.commitHashTwo, null, true, null, start, 100);
            assertThat(linePage.errors().isEmpty()).isTrue();
            assertThat(linePage.blame().isEmpty()).isFalse();
            
            allLines.addAll(linePage.values());
            start = linePage.nextPageStart();
            if (linePage.isLastPage()) {
                break;
            } else {
                System.out.println("Sleeping for 1 seconds before querying for next page");
                Thread.sleep(1000);
            }
        }
        assertThat(allLines.size() > 0).isEqualTo(true);
        final StringBuilder possibleContent = new StringBuilder();
        for (final Line possibleLine : allLines) {
            possibleContent.append(possibleLine.text());
        }
        assertThat(possibleContent.toString()).isNotEqualTo(this.content.trim());
    }
    
    @Test 
    public void listLinesOnError() {
        final LinePage linePage = api().listLines(projectKey, repoKey, this.randomString() + ".txt", null, null, null, null, null, 100);
        assertThat(linePage).isNotNull();
        assertThat(linePage.errors().isEmpty()).isFalse();
    }
    
    @Test
    public void getContentOnNotFound() {
        final String possibleContent = api().getContent(projectKey, repoKey, randomString() + ".txt", null);
        assertThat(possibleContent).isNull();
    }
    
    @AfterClass
    public void fin() {
        terminateGeneratedTestContents(generatedTestContents);
    }
    
    private FileApi api() {
        return api.fileApi();
    }
}
