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

package com.cdancy.bitbucket.rest.domain.file;

import com.cdancy.bitbucket.rest.domain.pullrequest.Author;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Blame {
    
    public abstract Author author();

    public abstract String authorTimestamp();
    
    public abstract String commitHash();
    
    public abstract String displayCommitHash();
    
    public abstract String commitId();
    
    public abstract String commitDisplayId();
    
    public abstract String fileName();

    public abstract int lineNumber();
    
    public abstract int spannedLines();
    
    Blame() {
    }

    @SerializedNames({ "author", "authorTimestamp", "commitHash", 
            "displayCommitHash", "commitId", "commitDisplayId", 
            "fileName", "lineNumber", "spannedLines" })
    public static Blame create(Author author, String authorTimestamp, String commitHash, 
            String displayCommitHash, String commitId, String commitDisplayId, 
            String fileName, int lineNumber, int spannedLines) {
        return new AutoValue_Blame(author, authorTimestamp, commitHash, 
                displayCommitHash, commitId, commitDisplayId, 
                fileName, lineNumber, spannedLines);
    }
}
