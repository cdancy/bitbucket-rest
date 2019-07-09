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

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class InsightReportData {
    public enum DataType {
        BOOLEAN,
        DATE,
        DURATION,
        LINK,
        NUMBER,
        PERCENTAGE,
        TEXT
    }

    public abstract String title();

    public abstract DataType type();

    public abstract String value();

    @SerializedNames({"title", "type", "value"})
    public static InsightReportData create(final String title,
                                           final InsightReportData.DataType type,
                                           final String value) {
        return new AutoValue_InsightReportData(title, type, value);
    }
}
