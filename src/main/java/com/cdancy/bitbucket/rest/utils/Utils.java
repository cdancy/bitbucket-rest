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

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;

/**
 * Collection of static methods to be used globally.
 */
public class Utils {

    /**
     * Convert passed Iterable into an ImmutableList.
     * 
     * @param <T> an arbitrary type.
     * @param input the Iterable to copy.
     * @return ImmutableList or empty ImmutableList if `input` is null.
     */
    public static <T> List<T> nullToEmpty(final Iterable<? extends T> input) {
        return (List<T>) (input == null ? ImmutableList.<T> of() : ImmutableList.copyOf(input));
    }

    /**
     * Convert passed Map into an ImmutableMap.
     * 
     * @param <K> an arbitrary type.
     * @param <V> an arbitrary type.
     * @param input the Map to copy.
     * @return ImmutableMap or empty ImmutableMap if `input` is null.
     */
    public static <K, V> Map<K, V> nullToEmpty(final Map<? extends K, ? extends V> input) {
        return (Map<K, V>) (input == null ? ImmutableMap.<K, V> of() : ImmutableMap.copyOf(input));
    }

    /**
     * Retrieve property value from list of keys.
     *
     * @param keys list of keys to search.
     * @return the first value found from list of keys.
     */
    public static String retrivePropertyValue(final Collection<String> keys) {
        for (final String possibleKey : keys) {
            final String value = retrivePropertyValue(possibleKey);
            if (value != null) {
                return value.trim();
            }
        }
        return null;
    }

    /**
     * Retrieve property value from key.
     *
     * @param key the key to search for.
     * @return the value of key or null if not found.
     */
    public static String retrivePropertyValue(final String key) {
        final String value = System.getProperty(key);
        return value != null ? value : System.getenv(key);
    }
    
    private Utils() {
        throw new UnsupportedOperationException("Purposefully not implemented");
    }
}
