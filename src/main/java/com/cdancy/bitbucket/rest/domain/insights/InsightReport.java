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

package com.cdancy.bitbucket.rest.domain.insights;

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class InsightReport implements ErrorsHolder {
    public enum ReportResult {
        PASS,
        FAIL
    }

    public abstract long createdDate();

    @Nullable
    public abstract String details();

    @Nullable
    public abstract String key();

    @Nullable
    public abstract String link();

    @Nullable
    public abstract String logoUrl();

    @Nullable
    public abstract ReportResult result();

    @Nullable
    public abstract String title();

    @Nullable
    public abstract String reporter();

    @Nullable
    public abstract List<InsightReportData> data();

    @SerializedNames({"createdDate", "details", "key", "link", "logoUrl", "result", "title", "reporter", "data", "errors"})
    public static InsightReport create(final long createdDate,
                                       final String details,
                                       final String key,
                                       final String link,
                                       final String logoUrl,
                                       final ReportResult result,
                                       final String title,
                                       final String reporter,
                                       final List<InsightReportData> data,
                                       @Nullable final List<Error> errors) {
        return new AutoValue_InsightReport(BitbucketUtils.nullToEmpty(errors),
            createdDate,
            details,
            key,
            link,
            logoUrl,
            result,
            title,
            reporter,
            data);
    }
}
