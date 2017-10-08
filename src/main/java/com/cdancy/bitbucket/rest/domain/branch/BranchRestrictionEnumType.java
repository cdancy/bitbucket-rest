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

public enum BranchRestrictionEnumType {
    
    FAST_FORWARD_ONLY("Rewriting history", 
            "fast-forward-only",
            "prevents history rewrites on the specified branch(es) - for example by a force push or rebase."),
    
    NO_DELETES("Deletion", 
            "no-deletes", 
            "prevents branch and tag deletion"),
    
    PULL_REQUEST_ONLY("Changes without a pull request", 
            "pull-request-only",
            "prevents pushing changes directly to the specified branch(es); changes are allowed only with a pull request"),
    
    READ_ONLY("All changes", 
            "read-only",
            "prevents pushes to the specified branch(es) and restricts creating new"
            + " branches matching the specified branch(es) or pattern");

    private final String name;
    private final String apiName;
    private final String description;

    BranchRestrictionEnumType(final String name, final String apiName, final String description) {
        this.name = name;
        this.apiName = apiName;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getApiName() {
        return apiName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Convert value from Api to enum.
     *
     * @param apiName ApiName
     * @return value
     */
    public static BranchRestrictionEnumType fromValue(final String apiName) {
        for (final BranchRestrictionEnumType enumType : BranchRestrictionEnumType.values()) {
            if (enumType.getApiName().equals(apiName)) {
                return enumType;
            }
        }
        throw new IllegalArgumentException("Value " + apiName + " is not a legal BranchPermission type");
    }

    @Override
    public String toString() {
        return this.getApiName();
    }
}
