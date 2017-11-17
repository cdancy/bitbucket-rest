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


import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.pullrequest.Author;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;
import java.util.List;

@AutoValue
public abstract class Task implements ErrorsHolder {

    @Nullable
    public abstract TaskAnchor anchor();
    
    @Nullable
    public abstract Author author();    
    
    public abstract long createdDate();
    
    public abstract int id();

    @Nullable
    public abstract String text();
    
    @Nullable
    public abstract String state();

    @Nullable
    public abstract PermittedOperations permittedOperations();
    
    Task() {
    }

    @SerializedNames({ "anchor", "author", "createdDate", "id", "text",
            "state", "permittedOperations", "errors" })
    public static Task create(final TaskAnchor anchor, 
            final Author author, 
            final long createdDate, 
            final int id, 
            final String text, 
            final String state, 
            final PermittedOperations permittedOperations, 
            final List<Error> errors) {
        
        return new AutoValue_Task(Utils.nullToEmpty(errors), 
                anchor, 
                author, 
                createdDate, 
                id, 
                text, 
                state, 
                permittedOperations);
    }
}
