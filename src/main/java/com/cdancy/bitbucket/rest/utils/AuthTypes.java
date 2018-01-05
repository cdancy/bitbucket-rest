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

package com.cdancy.bitbucket.rest.utils;

/**
 * Supported Authentication Types for Bitbucket.
 */
public enum AuthTypes {

    BASIC ("Basic"),
    BEARER ("Bearer"),
    NONE ("NONE");

    private final String type;
    
    private AuthTypes(final String type) {
        this.type = type;
    }

    /**
     * Infer an AuthTypes enum from the passed String.
     * 
     * @param possibleType inferred AuthTypes.
     * @return AuthTypes or null if not found.
     */
    public static AuthTypes from(final String possibleType) {
        AuthTypes authType = null;
        if (possibleType != null) {
            final String localType = possibleType.trim();
            if (localType.length() > 0) {
                for (final AuthTypes availableType : AuthTypes.values()) {
                    if (localType.equalsIgnoreCase(availableType.toString())) {
                        authType = availableType;
                        break;
                    }
                }
            }
        }
        return authType;
    }

    @Override
    public String toString() {
        return type;
    }
}
