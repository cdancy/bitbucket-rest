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

import com.cdancy.bitbucket.rest.utils.BitbucketUtils;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Path {

    public abstract List<String> components();

    public abstract String parent();

    public abstract String name();

    @Nullable
    public abstract String extension();

    public abstract String _toString();

    Path() {
    }

    @SerializedNames({ "components", "parent", "name", 
            "extension", "toString" })
    public static Path create(final List<String> components, 
            final String parent, 
            final String name,
            final String extension, 
            final String _toString) {
        
        return new AutoValue_Path(BitbucketUtils.nullToEmpty(components), 
                parent, 
                name, 
                extension, 
                _toString);
    }
}
