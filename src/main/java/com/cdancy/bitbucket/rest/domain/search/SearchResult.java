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
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@SuppressWarnings("PMD")
@AutoValue
public abstract class SearchResult implements ErrorsHolder {

    @Nullable
    public abstract SearchScope scope();

    @Nullable
    public abstract SearchCode code();

    @Nullable
    public abstract SearchQuery query();

    SearchResult() {
    }

    @SerializedNames({ "scope", "code", "query", "errors" })
    public static SearchResult create(final SearchScope scope,
                                   final SearchCode code,
                                   final SearchQuery query,
                                   final List<Error> errors) {
        
        return new AutoValue_SearchResult(BitbucketUtils.nullToEmpty(errors),
            scope,
            code,
            query);
    }
}
