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

package com.cdancy.bitbucket.rest.options;

import com.cdancy.bitbucket.rest.domain.insights.InsightReportData;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class CreateInsightReport {
    public enum RESULT {
        PASS,
        FAIL
    }

    @Nullable
    public abstract String details();

    @Nullable
    public abstract String link();

    @Nullable
    public abstract String logoUrl();

    @Nullable
    public abstract CreateInsightReport.RESULT result();

    public abstract String title();

    @Nullable
    public abstract String reporter();

    public abstract List<InsightReportData> data();

    @SerializedNames({"details", "link", "logoUrl", "result", "title", "reporter", "data"})
    public static CreateInsightReport create(final String details,
                                             final String link,
                                             final String logoUrl,
                                             final CreateInsightReport.RESULT result,
                                             final String title,
                                             final String reporter,
                                             final List<InsightReportData> data) {
        return new AutoValue_CreateInsightReport(details, link, logoUrl, result, title, reporter, data);
    }
}
