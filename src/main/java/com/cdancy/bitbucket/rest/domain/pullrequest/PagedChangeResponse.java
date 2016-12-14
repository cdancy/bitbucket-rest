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

package com.cdancy.bitbucket.rest.domain.pullrequest;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PagedChangeResponse implements ErrorsHolder {

    public abstract int size();

    public abstract int limit();

    public abstract boolean isLastPage();

    public abstract List<Change> values();

    public abstract int start();

    @Nullable
    public abstract String filter();

    public abstract int nextPageStart();

    public PagedChangeResponse() {
    }

    @SerializedNames({ "size", "limit", "isLastPage", "values", "start", "filter", "nextPageStart", "errors" })
    public static PagedChangeResponse create(int size, int limit, boolean isLastPage, List<Change> values,
                                             int start, String filter, int nextPageStart, List<Error> errors) {
        return new AutoValue_PagedChangeResponse(Utils.nullToEmpty(errors), size, limit, isLastPage,
                Utils.nullToEmpty(values), start, filter, nextPageStart);
    }
}