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
import static com.google.common.io.BaseEncoding.base64;

import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import com.google.common.base.Throwables;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.jclouds.util.Strings2;
import org.jclouds.Constants;
import org.jclouds.apis.BaseApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Test(groups = "live")
public class BaseBitbucketApiLiveTest extends BaseApiLiveTest<BitbucketApi> {

    protected final String defaultBitbucketGroup = "stash-users";
    private User defaultUser;

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
        overrides.setProperty(Constants.PROPERTY_MAX_RETRIES, "5");
        return overrides;
    }

    protected synchronized User getDefaultUser() {
        if (defaultUser == null) {
            String username;
            if (this.credential.contains(":")) {
                username = this.credential.split(":")[0];
            } else {
                username = new String(base64().decode(this.credential)).split(":")[0];
            }
            final UserPage userPage = api.adminApi().listUsers(username, null, null);
            assertThat(userPage).isNotNull();
            assertThat(userPage.size() > 0).isTrue();
            for (User user : userPage.values()) {
                if (username.equals(user.slug())) {
                    defaultUser = user;
                    break;
                }
            }
            assertThat(defaultUser).isNotNull();
        }

        return defaultUser;
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
     * Execute `args` at the `workingDir`.
     * 
     * @param args list of arguments to pass to Process.
     * @param workingDir directory to execute Process within.
     * @return possible output of Process.
     * @throws Exception if Process could not be successfully executed.
     */
    protected String executionToString(List<String> args, Path workingDir) throws Exception {
        assertThat(args).isNotNull();
        assertThat(args).isNotEmpty();
        assertThat(workingDir).isNotNull();
        assertThat(workingDir.toFile().isDirectory()).isTrue();
        
        final Process process = new ProcessBuilder(args)
                .directory(workingDir.toFile())
                .start();

        return Strings2.toStringAndClose(process.getInputStream());
    }
    
    /**
     * Generate a dummy file at the `baseDir` location.
     * 
     * @param baseDir directory to generate the file under.
     * @return Path pointing at generated file.
     * @throws Exception if file could not be written.
     */
    protected Path initGeneratedFile(Path baseDir) throws Exception {
        assertThat(baseDir).isNotNull();
        assertThat(baseDir.toFile().isDirectory()).isTrue();
        
        final String randomName = randomString();
        
        final List<String> lines = Arrays.asList(randomName);
        final Path file = Paths.get(new File(baseDir.toFile(), randomName + ".txt").toURI());
        return Files.write(file, lines, Charset.forName("UTF-8"));        
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
        if (!project.errors().isEmpty()) {

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
        
        final Path testDir = Paths.get(System.getProperty("test.bitbucket.basedir"));
        assertThat(testDir.toFile().exists()).isTrue();
        assertThat(testDir.toFile().isDirectory()).isTrue();

        final String randomName = randomString();
        final File generatedFileDir = new File(testDir.toFile(), randomName);
        assertThat(generatedFileDir.mkdirs()).isTrue();
        
        try {
            String foundCredential = this.credential;
            if (!foundCredential.contains(":")) {
                foundCredential = new String(base64().decode(foundCredential));
            }
            
            final URL endpointURL = new URL(this.endpoint);
            final int index = endpointURL.toString().indexOf(endpointURL.getHost());
            final String preCredentialPart = endpointURL.toString().substring(0, index);
            final String postCredentialPart = endpointURL.toString().substring(index, endpointURL.toString().length());
        
            final String generatedEndpoint = preCredentialPart 
                    + foundCredential + "@" 
                    + postCredentialPart + "/scm/" 
                    + projectKey.toLowerCase() + "/" 
                    + repoKey.toLowerCase() + ".git";

            generateGitContentsAndPush(generatedFileDir, generatedEndpoint);
            
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        
        return new GeneratedTestContents(project, repository, projectPreviouslyExists);        
    }
    
    /**
     * Initialize git repository and add some randomly generated files.
     * 
     * @param gitDirectory directory to initialize and create files within.
     * @param gitRepoURL git repository URL with embedded credentials.
     * @throws Exception if git repository could not be created or files added.
     */
    private void generateGitContentsAndPush(File gitDirectory, String gitRepoURL) throws Exception {
        
        // 1.) initialize git repository
        String initGit = executionToString(Arrays.asList("git", "init"), gitDirectory.toPath());
        System.out.println("git-init: " + initGit.trim());
        
        // 2.) create some random files and commit them
        for (int i = 0; i < 3; i++) {
            Path genFile = initGeneratedFile(gitDirectory.toPath());
            String addGit = executionToString(Arrays.asList("git", "add", genFile.toFile().getPath()), gitDirectory.toPath());
            System.out.println("git-add-1: " + addGit.trim());
            String commitGit = executionToString(Arrays.asList("git", "commit", "-m", "added"), gitDirectory.toPath());
            System.out.println("git-commit-1: " + commitGit.trim());
            
            // edit file again and create another commit
            genFile = Files.write(genFile, Arrays.asList(randomString()), Charset.forName("UTF-8"));
            addGit = executionToString(Arrays.asList("git", "add", genFile.toFile().getPath()), gitDirectory.toPath());
            System.out.println("git-add-2: " + addGit.trim());
            commitGit = executionToString(Arrays.asList("git", "commit", "-m", "added"), gitDirectory.toPath());
            System.out.println("git-commit-2: " + commitGit.trim());
        }
        
        // 3.) push changes to remote repository
        String pushGit = executionToString(Arrays.asList("git", "push", "--set-upstream", gitRepoURL, "master"), gitDirectory.toPath());
        System.out.println("git-push: " + pushGit);
        
        // 4.) create branch 
        String generatedBranchName = randomString();
        String branchGit = executionToString(Arrays.asList("git", "checkout", "-b", generatedBranchName), gitDirectory.toPath());
        System.out.println("git-branch: " + branchGit.trim());

        
        // 5.) generate random file for new branch
        Path genFile = initGeneratedFile(gitDirectory.toPath());
        String addGit = executionToString(Arrays.asList("git", "add", genFile.toFile().getPath()), gitDirectory.toPath());
        System.out.println("git-branch-add: " + addGit.trim());
        String commitGit = executionToString(Arrays.asList("git", "commit", "-m", "added"), gitDirectory.toPath());
        System.out.println("git-branch-commit: " + commitGit.trim());
        
        // 6.) push branch
        List<String> args = Arrays.asList("git", "push", "-u", gitRepoURL, generatedBranchName);
        String pushBranchGit = executionToString(args, gitDirectory.toPath());
        System.out.println("git-branch-push: " + pushBranchGit);
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
            final RequestStatus deleteStatus = api.projectApi().delete(project.key());
            assertThat(deleteStatus).isNotNull();
            assertThat(deleteStatus.value()).isTrue();
            assertThat(deleteStatus.errors()).isEmpty();
        }
    }
}


