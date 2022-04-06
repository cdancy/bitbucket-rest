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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

    public abstract Object value();

    @SerializedNames({"title", "type", "value"})
    private static InsightReportData create(final String title,
            final DataType type,
            final Object value) {
        return new AutoValue_InsightReportData(title, type, value);
    }

    public static InsightReportData createBoolean(final String title, final boolean value) {
        return create(title, DataType.BOOLEAN, value);
    }

    public static InsightReportData createDate(final String title, final LocalDate value) {
        return createDate(title, value.atStartOfDay());
    }

    public static InsightReportData createDate(final String title, final LocalDateTime value) {
        final long epochMilli = value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return createDate(title, epochMilli);
    }

    public static InsightReportData createDate(final String title, final long epochMilli) {
        return create(title, DataType.DATE, epochMilli);
    }

    public static InsightReportData createDuration(final String title, final Duration duration) {
        return create(title, DataType.DURATION, duration.toMillis());
    }

    public static InsightReportData createDuration(final String title, final long millis) {
        return create(title, DataType.DURATION, millis);
    }

    public static InsightReportData createLink(final String title, final String href) {
        return createLink(title, href, href);
    }

    public static InsightReportData createLink(final String title,
            final String href,
            final String linkText) {
        final InsightReportDataLink link = InsightReportDataLink.create(linkText, href);
        return create(title, DataType.LINK, link);
    }

    public static InsightReportData createNumber(final String title, final long number) {
        return create(title, DataType.NUMBER, number);
    }

    public static InsightReportData createPercentage(final String title, final byte percentage) {
        return create(title, DataType.PERCENTAGE, percentage);
    }

    public static InsightReportData createText(final String title, final String text) {
        return create(title, DataType.TEXT, text);
    }
}
