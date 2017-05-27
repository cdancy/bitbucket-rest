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

package com.cdancy.bitbucket.rest.domain.branch;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Matcher {

    public enum MatcherId {
        
        RELEASE("RELEASE", "Release", "MODEL_CATEGORY", "Branching model category"),
        DEVELOPMENT("development", "Development", "MODEL_BRANCH", "Branching model branch"),
        MASTER("production", "Production", "MODEL_BRANCH", "Branching model branch");

        private final String id;
        private final String name;
        private final String typeId;
        private final String typeName;

        MatcherId(final String id, 
                final String name, 
                final String typeId, 
                final String typeName) {
            this.id = id;
            this.name = name;
            this.typeId = typeId;
            this.typeName = typeName;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getTypeId() {
            return typeId;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    public abstract String id();

    public abstract String displayId();

    public abstract BranchPermissionType type();

    public abstract Boolean active();

    @SerializedNames({"id", "displayId", "type", "active"})
    public static Matcher create(final String id, 
            final String displayId, 
            final BranchPermissionType type,
            final Boolean active) {
        
        return new AutoValue_Matcher(id, displayId, type, active);
    }

    public static Matcher create(final MatcherId matcherId, final Boolean active) {
        return new AutoValue_Matcher(matcherId.getId(), 
                matcherId.getName(), 
                BranchPermissionType.create(matcherId), active);
    }
}
