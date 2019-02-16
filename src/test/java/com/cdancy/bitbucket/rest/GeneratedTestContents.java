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

import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Umbrella for all generated test contents.
 */
public class GeneratedTestContents {

    public final Project project;
    public final Repository repository;
    public final String emptyRepositoryName;
    public final List<String[]> projectRepoMapping = Lists.newArrayList();

    public final boolean projectPreviouslyExists;

    /**
     * Default constructor for GeneratedTestContents.
     * 
     * @param project previously created Project.
     * @param repository previously created Repository.
     * @param emptyRepositoryName previously created Repository with no contents.
     * @param projectPreviouslyExists whether the test suite created or user passed in.
     */
    public GeneratedTestContents(final Project project, 
            final Repository repository,
            final String emptyRepositoryName,
            final boolean projectPreviouslyExists) {
        
        this.project = project;
        this.repository = repository;
        this.emptyRepositoryName = emptyRepositoryName;
        this.projectPreviouslyExists = projectPreviouslyExists;
    }

    public void addRepoForDeletion(final String project, final String repository) {
        final String[] mapping = {project , repository};
        projectRepoMapping.add(mapping);
    }
}
