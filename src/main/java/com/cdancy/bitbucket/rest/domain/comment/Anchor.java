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

package com.cdancy.bitbucket.rest.domain.comment;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Anchor {

    public enum LineType {
        ADDED,
        REMOVED,
        CONTEXT
    }

    public enum FileType {
        FROM,
        TO
    }

    @Nullable
    public abstract Integer line();

    @Nullable
    public abstract String lineType();

    @Nullable
    public abstract String fileType();

    public abstract String path();

    @Nullable
    public abstract String srcPath();

    Anchor() {
    }

    @SerializedNames({ "line", "lineType", "fileType", "path", "srcPath" })
    public static Anchor create(final Integer line, 
            final LineType lineType, 
            final FileType fileType, 
            final String path, 
            final String srcPath) {
        
        return new AutoValue_Anchor(line,
                lineType != null ? lineType.toString() : null,
                fileType != null ? fileType.toString() : null,
                path,
                srcPath);
    }

    @SerializedNames({ "line", "lineType", "fileType", "path", "srcPath" })
    public static Anchor create(final Integer line, 
            final String lineType, 
            final String fileType, 
            final String path, 
            final String srcPath) {

        String possibleLineType = null;
        if (lineType != null) {
            for (final LineType type : LineType.values()) {
                final String foundType = type.toString();
                if (foundType.equalsIgnoreCase(lineType)) {
                    possibleLineType = foundType;
                    break;
                }
            }
            if (possibleLineType == null) {
                throw new RuntimeException("Unrecognized lineType '" + lineType + "'");
            }
        }

        String possibleFileType = null;
        if (fileType != null) {
            for (final FileType type : FileType.values()) {
                final String foundType = type.toString();
                if (foundType.equalsIgnoreCase(fileType)) {
                    possibleFileType = foundType;
                    break;
                }
            }
            if (possibleFileType == null) {
                throw new RuntimeException("Unrecognized fileType '" + fileType + "'");
            }
        }

        return new AutoValue_Anchor(line,
                possibleLineType,
                possibleFileType,
                path,
                srcPath);
    }
}
