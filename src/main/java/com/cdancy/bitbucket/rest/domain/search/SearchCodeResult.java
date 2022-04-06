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

package com.cdancy.bitbucket.rest.domain.search;

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class SearchCodeResult {

    @Nullable
    public abstract Repository repository();
    
    public abstract List<List<SearchHitContext>> hitContexts();
    
    public abstract List<SearchPathMatch> pathMatches();

    @Nullable
    public abstract String file();

    @Nullable
    public abstract Integer hitCount();

    SearchCodeResult() {
    }

    @SerializedNames({ "repository", "hitContexts", "pathMatches", "file", "hitCount" })
    public static SearchCodeResult create(final Repository repository,
                                   final List<List<SearchHitContext>> hitContexts,
                                   final List<SearchPathMatch> pathMatches,
                                   final String file,
                                   final Integer hitCount) {
        
        return new AutoValue_SearchCodeResult(repository,
            BitbucketUtils.nullToEmpty(hitContexts),
            BitbucketUtils.nullToEmpty(pathMatches),
            file,
            hitCount);
    }
}
