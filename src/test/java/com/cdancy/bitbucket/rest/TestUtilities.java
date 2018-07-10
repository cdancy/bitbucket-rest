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

import static com.google.common.io.BaseEncoding.base64;
import static org.assertj.core.api.Assertions.assertThat;

import com.cdancy.bitbucket.rest.auth.AuthenticationType;

import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.options.CreateProject;
import com.cdancy.bitbucket.rest.options.CreateRepository;
import com.google.common.base.Throwables;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.jclouds.util.Strings2;

/**
 * Static methods for generating test data.
 */
public class TestUtilities extends BitbucketUtils {

    public static final String TEST_CREDENTIALS_SYSTEM_PROPERTY = "test.bitbucket.rest.credentials";
    public static final String TEST_CREDENTIALS_ENVIRONMENT_VARIABLE = TEST_CREDENTIALS_SYSTEM_PROPERTY.replaceAll("\\.", "_").toUpperCase();

    public static final String TEST_TOKEN_SYSTEM_PROPERTY = "test.bitbucket.rest.token";
    public static final String TEST_TOKEN_ENVIRONMENT_VARIABLE = TEST_TOKEN_SYSTEM_PROPERTY.replaceAll("\\.", "_").toUpperCase();

    private static final String GIT_COMMAND = "git";
    private static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    private static User defaultUser;

    /**
     * Get the default User from the passed credential String. Once user
     * is found we will cache for later usage.
     *
     * @param api Bitbucket api object.
     * @param auth the BitbucketAuthentication instance.
     * @return User or null if user can't be inferred.
     */
    public static synchronized User getDefaultUser(final BitbucketAuthentication auth, final BitbucketApi api) {
        if (defaultUser == null) {
            assertThat(auth).isNotNull();
            assertThat(api).isNotNull();

            if (auth.authType() == AuthenticationType.Basic) {
                final String username = new String(base64().decode(auth.authValue())).split(":")[0];
                final UserPage userPage = api.adminApi().listUsers(username, null, null);
                assertThat(userPage).isNotNull();
                assertThat(userPage.size() > 0).isTrue();
                for (final User user : userPage.values()) {
                    if (username.equals(user.slug())) {
                        defaultUser = user;
                        break;
                    }
                }
                assertThat(defaultUser).isNotNull();
            }
        }

        return defaultUser;
    }

    /**
     * Execute `args` at the `workingDir`.
     *
     * @param args list of arguments to pass to Process.
     * @param workingDir directory to execute Process within.
     * @return possible output of Process.
     * @throws Exception if Process could not be successfully executed.
     */
    public static String executionToString(final List<String> args, final Path workingDir) throws Exception {
        assertThat(args).isNotNull().isNotEmpty();
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
    public static Path initGeneratedFile(final Path baseDir) throws Exception {
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
     * @param endpoint Bitbucket endpoint.
     * @param credential Bitbucket credential string.
     * @param api Bitbucket api object.
     * @return GeneratedTestContents to use.
     */
    public static synchronized GeneratedTestContents initGeneratedTestContents(final String endpoint,
                                                                                final BitbucketAuthentication credential,
                                                                                final BitbucketApi api) {
        assertThat(endpoint).isNotNull();
        assertThat(credential).isNotNull();
        assertThat(api).isNotNull();

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
            final String foundCredential = getDefaultUser(credential, api).slug();
            final URL endpointURL = new URL(endpoint);
            final int index = endpointURL.toString().indexOf(endpointURL.getHost());
            final String preCredentialPart = endpointURL.toString().substring(0, index);
            final String postCredentialPart = endpointURL.toString().substring(index, endpointURL.toString().length());

            final String generatedEndpoint = preCredentialPart
                    + foundCredential + "@"
                    + postCredentialPart + "/scm/"
                    + projectKey.toLowerCase() + "/"
                    + repoKey.toLowerCase() + ".git";

            generateGitContentsAndPush(generatedFileDir, generatedEndpoint);

        } catch (final Exception e) {
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
    public static void generateGitContentsAndPush(final File gitDirectory, final String gitRepoURL) throws Exception {

        // 1.) initialize git repository
        final String initGit = TestUtilities.executionToString(Arrays.asList(GIT_COMMAND, "init"), gitDirectory.toPath());
        System.out.println("git-init: " + initGit.trim());

        // 2.) create some random files and commit them
        for (int i = 0; i < 3; i++) {
            Path genFile = initGeneratedFile(gitDirectory.toPath());
            String addGit = TestUtilities.executionToString(Arrays.asList(GIT_COMMAND, "add", genFile.toFile().getPath()), gitDirectory.toPath());
            System.out.println("git-add-1: " + addGit.trim());
            String commitGit = TestUtilities.executionToString(Arrays.asList(GIT_COMMAND, "commit", "-m", "added"), gitDirectory.toPath());
            System.out.println("git-commit-1: " + commitGit.trim());

            // edit file again and create another commit
            genFile = Files.write(genFile, Arrays.asList(randomString()), Charset.forName("UTF-8"));
            addGit = TestUtilities.executionToString(Arrays.asList(GIT_COMMAND, "add", genFile.toFile().getPath()), gitDirectory.toPath());
            System.out.println("git-add-2: " + addGit.trim());
            commitGit = TestUtilities.executionToString(Arrays.asList(GIT_COMMAND, "commit", "-m", "added"), gitDirectory.toPath());
            System.out.println("git-commit-2: " + commitGit.trim());
            final String tagGit = TestUtilities
                    .executionToString(Arrays
                            .asList(GIT_COMMAND, "tag", "-a", "v0.0." + (i + 1), "-m", "\"generated version\""), gitDirectory.toPath());
            System.out.println("git-tag-commit: " + tagGit.trim());
        }

        // 3.) push changes to remote repository
        final String pushGit = TestUtilities.executionToString(Arrays.asList(GIT_COMMAND,
                "push",
                "--set-upstream",
                gitRepoURL,
                "master"), gitDirectory.toPath());
        System.out.println("git-push: " + pushGit);

        // 4.) create branch
        final String generatedBranchName = randomString();
        final String branchGit = TestUtilities.executionToString(Arrays.asList(GIT_COMMAND,
                "checkout", "-b",
                generatedBranchName),
                gitDirectory.toPath());
        System.out.println("git-branch: " + branchGit.trim());


        // 5.) generate random file for new branch
        final Path genFile = initGeneratedFile(gitDirectory.toPath());
        final String addGit = TestUtilities.executionToString(Arrays.asList(GIT_COMMAND, "add", genFile.toFile().getPath()), gitDirectory.toPath());
        System.out.println("git-branch-add: " + addGit.trim());
        final String commitGit = TestUtilities.executionToString(Arrays.asList(GIT_COMMAND, "commit", "-m", "added"), gitDirectory.toPath());
        System.out.println("git-branch-commit: " + commitGit.trim());

        // 6.) push branch
        final List<String> args = Arrays.asList(GIT_COMMAND, "push", "--tags", "-u", gitRepoURL, generatedBranchName);
        final String pushBranchGit = TestUtilities.executionToString(args, gitDirectory.toPath());
        System.out.println("git-branch-push: " + pushBranchGit);
    }

    /**
     * Terminate live test contents.
     *
     * @param api BitbucketApi object
     * @param generatedTestContents to terminate.
     */
    public static synchronized void terminateGeneratedTestContents(final BitbucketApi api,
            final GeneratedTestContents generatedTestContents) {
        assertThat(api).isNotNull();
        assertThat(generatedTestContents).isNotNull();

        final Project project = generatedTestContents.project;
        final Repository repository = generatedTestContents.repository;

        // delete repository
        final RequestStatus success = api.repositoryApi().delete(project.key(), repository.name());
        assertThat(success).isNotNull();
        assertThat(success.value()).isTrue();
        assertThat(success.errors()).isEmpty();

        // delete project
        if (!generatedTestContents.projectPreviouslyExists) {
            final RequestStatus deleteStatus = api.projectApi().delete(project.key());
            assertThat(deleteStatus).isNotNull();
            assertThat(deleteStatus.value()).isTrue();
            assertThat(deleteStatus.errors()).isEmpty();
        }
    }

    /**
     * Generate a random String with letters only.
     *
     * @return random String.
     */
    public static String randomStringLettersOnly() {
        final StringBuilder sb = new StringBuilder();
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final char randomChar = CHARS[random.nextInt(CHARS.length)];
            sb.append(randomChar);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * Generate a random String with numbers and letters.
     *
     * @return random String.
     */
    public static String randomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * Find credentials (Basic, Bearer, or Anonymous) from system/environment.
     *
     * @return BitbucketCredentials
     */
    public static BitbucketAuthentication inferTestAuthentication() {

        // 1.) Check for "Basic" auth credentials.
        final BitbucketAuthentication.Builder inferAuth = BitbucketAuthentication.builder();
        String authValue = BitbucketUtils
                .retriveExternalValue(TEST_CREDENTIALS_SYSTEM_PROPERTY,
                        TEST_CREDENTIALS_ENVIRONMENT_VARIABLE);
        if (authValue != null) {
            inferAuth.credentials(authValue);
        } else {

            // 2.) Check for "Bearer" auth token.
            authValue = BitbucketUtils
                    .retriveExternalValue(TEST_TOKEN_SYSTEM_PROPERTY,
                            TEST_TOKEN_ENVIRONMENT_VARIABLE);
            if (authValue != null) {
                inferAuth.token(authValue);
            }
        }

        // 3.) If neither #1 or #2 find anything "Anonymous" access is assumed.
        return inferAuth.build();
    }

    private TestUtilities() {
        throw new UnsupportedOperationException("Purposefully not implemented");
    }
}
