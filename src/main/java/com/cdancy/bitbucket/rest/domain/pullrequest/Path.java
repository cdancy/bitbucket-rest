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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class Path {

    public abstract List<String> components();

    public abstract String parent();

    public abstract String name();

    public abstract String extension();

    public abstract String _toString();

    Path() {
    }

    @SerializedNames({ "components", "parent", "name", "extension", "toString" })
    public static Path create(List<String> components, String parent, String name,
                              String extension, String _toString) {
        List<String> comp = (components != null) ? ImmutableList.copyOf(components) : ImmutableList.<String>of();
        return new AutoValue_Path(comp,
                parent,
                name,
                extension,
                _toString);
    }
}
