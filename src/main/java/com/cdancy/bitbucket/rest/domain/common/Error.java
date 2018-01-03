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

package com.cdancy.bitbucket.rest.domain.common;

import com.cdancy.bitbucket.rest.utils.BitbucketUtils;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import java.util.List;

@AutoValue
public abstract class Error {

    @Nullable
    public abstract String context();

    @Nullable
    public abstract String message();

    @Nullable
    public abstract String exceptionName();
    
    public abstract boolean conflicted();

    public abstract List<Veto> vetoes();

    Error() {
    }

    @SerializedNames({ "context", "message", "exceptionName", "conflicted", "vetoes" })
    public static Error create(final String context, 
            final String message, 
            final String exceptionName, 
            final boolean conflicted, 
            final List<Veto> vetoes) {
        
        return new AutoValue_Error(context, 
                message, 
                exceptionName, 
                conflicted, 
                BitbucketUtils.nullToEmpty(vetoes));
    }
}
