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

package com.cdancy.bitbucket.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.jclouds.Constants;
import org.jclouds.apis.BaseApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = "live")
public class BaseBitbucketApiLiveTest extends BaseApiLiveTest<BitbucketApi> {

    public BaseBitbucketApiLiveTest() {
        provider = "bitbucket";
    }

    @Override
    protected Iterable<Module> setupModules() {
        return ImmutableSet.<Module> of(getLoggingModule());
    }

    @Override
    protected Properties setupProperties() {
        Properties overrides = super.setupProperties();
        overrides.setProperty(Constants.PROPERTY_MAX_RETRIES, "0");
        return overrides;
    }

    protected String randomStringLettersOnly() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            char randomChar = chars[random.nextInt(chars.length)];
            sb.append(randomChar);
        }
        return sb.toString().toUpperCase();
    }

    protected String randomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    /**
     * Initialize live test contents.
     * 
     * @return GeneratedTestContents to use.
     */
    protected synchronized GeneratedTestContents initGeneratedTestContents() {
        
        // get possibly existing projectKey that user passed in
        String projectKey = System.getProperty("test.bitbucket.project");
        if (projectKey == null) {
            projectKey = randomStringLettersOnly();
        }
            
        // create test project if one does not already exist
        boolean projectPreviouslyExists = true;
        Project project = api.projectApi().get(projectKey);
        assertThat(project).isNotNull();
        if (project.errors().size() > 1) {
            
            projectPreviouslyExists = false;

            final CreateProject createProject = CreateProject.create(projectKey, null, null, null);
            project = api.projectApi().create(createProject);
            assertThat(project).isNotNull();
            assertThat(project.errors().isEmpty()).isTrue();    
        }
        
        // create test repo
        final String repoKey = randomStringLettersOnly();
        final CreateRepository createRepository = CreateRepository.create(repoKey, true);
        final Repository repository = api.repositoryApi().create(projectKey, createRepository);
        assertThat(repository).isNotNull();
        assertThat(repository.errors().isEmpty()).isTrue();
        
        final GeneratedTestContents generatedTestContents = new GeneratedTestContents(project, repository, projectPreviouslyExists);
        
        
        return generatedTestContents;
    }
    
    /**
     * Terminate live test contents.
     * 
     * @param generatedTestContents to terminate.
     */
    protected synchronized void terminateGeneratedTestContents(final GeneratedTestContents generatedTestContents) {
        assertThat(generatedTestContents).isNotNull();
        
        final Project project = generatedTestContents.project;
        final Repository repository = generatedTestContents.repository;
        
        // delete repository 
        boolean success = api.repositoryApi().delete(project.key(), repository.name());
        assertThat(success).isTrue();
        
        // delete project
        if (!generatedTestContents.projectPreviouslyExists) {
            success = api.projectApi().delete(project.key());
            assertThat(success).isTrue();
        }
    }
}
