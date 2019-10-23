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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Annotation {
    public enum AnnotationSeverity {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum AnnotationType {
        VULNERABILITY,
        CODE_SMELL,
        BUG
    }

    @Nullable
    public abstract String reportKey();

    @Nullable
    public abstract String externalId();

    public abstract int line();

    @Nullable
    public abstract String link();

    @Nullable
    public abstract String message();

    @Nullable
    public abstract String path();

    @Nullable
    public abstract AnnotationSeverity severity();

    @Nullable
    public abstract AnnotationType type();

    @SerializedNames({"reportKey", "externalId", "line", "link", "message", "path", "severity", "type"})
    public static Annotation create(final String reportKey,
                                    final String externalId,
                                    final int line,
                                    final String link,
                                    final String message,
                                    final String path,
                                    final AnnotationSeverity severity,
                                    final AnnotationType type) {
        return new AutoValue_Annotation(reportKey, externalId, line, link, message, path, severity, type);
    }
}
