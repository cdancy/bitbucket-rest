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

package com.cdancy.bitbucket.rest;

import static com.cdancy.bitbucket.rest.BitbucketConstants.BITBUCKET_REST_PROPERTY_ID;
import static com.cdancy.bitbucket.rest.BitbucketConstants.BITBUCKET_REST_VARIABLE_ID;
import static com.cdancy.bitbucket.rest.BitbucketConstants.CREDENTIALS_PROPERTIES;
import static com.cdancy.bitbucket.rest.BitbucketConstants.DEFAULT_ENDPOINT;
import static com.cdancy.bitbucket.rest.BitbucketConstants.ENDPOINT_PROPERTIES;
import static com.cdancy.bitbucket.rest.BitbucketConstants.JCLOUDS_PROPERTY_ID;
import static com.cdancy.bitbucket.rest.BitbucketConstants.JCLOUDS_VARIABLE_ID;
import static com.cdancy.bitbucket.rest.BitbucketConstants.TOKEN_PROPERTIES;
import com.google.common.base.Throwables;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;

/**
 * Collection of static methods to be used globally.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class BitbucketUtils {

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
        if (keys == null) {
            return null;
        }
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
        if (key == null) {
            return null;
        }
        final String value = System.getProperty(key);
        return value != null ? value : System.getenv(key);
    }

    /**
     * Find endpoint from system/environment.
     *
     * @return String
     */
    public static String inferEndpoint() {
        final String possibleValue = BitbucketUtils.retrivePropertyValue(ENDPOINT_PROPERTIES);
        return possibleValue != null ? possibleValue : DEFAULT_ENDPOINT;
    }

    /**
     * Find credentials (Basic, Bearer, or Anonymous) from system/environment.
     *
     * @return BitbucketCredentials
     */
    public static BitbucketAuthentication inferCredentials() {

        // 1.) Check for "Basic" auth credentials.
        final BitbucketAuthentication.Builder inferAuth = BitbucketAuthentication.builder();
        String authValue = BitbucketUtils.retrivePropertyValue(CREDENTIALS_PROPERTIES);
        if (authValue != null) {
            inferAuth.credentials(authValue);
        } else {

            // 2.) Check for "Bearer" auth token.
            authValue = BitbucketUtils.retrivePropertyValue(TOKEN_PROPERTIES);
            if (authValue != null) {
                inferAuth.token(authValue);
            }
        }

        // 3.) If neither #1 or #2 find anything "Anonymous" access is assumed.
        return inferAuth.build();
    }

    /**
     * Find jclouds overrides (e.g. Properties) first searching within System
     * Properties and then within Environment Variables (former takes precedance).
     * 
     * @return Properties object with populated jclouds properties.
     */
    public static Properties inferOverrides() {
        final Properties overrides = new Properties();

        // 1.) Iterate over system properties looking for relevant properties.
        final Properties systemProperties = System.getProperties();
        final Enumeration<String> enums = (Enumeration<String>) systemProperties.propertyNames();
        while (enums.hasMoreElements()) {
            final String key = enums.nextElement();
            if (key.startsWith(BITBUCKET_REST_PROPERTY_ID)) {
                final int index = key.indexOf(JCLOUDS_PROPERTY_ID);
                final String trimmedKey = key.substring(index, key.length());
                overrides.put(trimmedKey, systemProperties.getProperty(key));
            }
        }

        // 2.) Iterate over environment variables looking for relevant variables. System
        //     Properties take precedence here so if the same property was already found
        //     there then we don't add it or attempt to override.
        for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
            if (entry.getKey().startsWith(BITBUCKET_REST_VARIABLE_ID)) {
                final int index = entry.getKey().indexOf(JCLOUDS_VARIABLE_ID);
                final String trimmedKey = entry.getKey()
                        .substring(index, entry.getKey().length())
                        .toLowerCase()
                        .replaceAll("_", ".");
                if (!overrides.containsKey(trimmedKey)) {
                    overrides.put(trimmedKey, entry.getValue());
                }
            }
        }
        
        return overrides;
    }

    /**
     * Add the passed environment variables to the currently existing env-vars.
     * 
     * @param addEnvVars the env-vars to add.
     */
    public static void addEnvironmentVariables(final Map<String, String> addEnvVars) {
        Objects.requireNonNull(addEnvVars, "Must pass non-null Map");
        final Map<String, String> newenv = Maps.newHashMap(System.getenv());
        newenv.putAll(addEnvVars);
        setEnvironmentVariables(newenv);
    }

    /**
     * Remove the passed environment variables keys from the environment.
     * 
     * @param removeEnvVars the env-var keys to be removed.
     */
    public static void removeEnvironmentVariables(final Collection<String> removeEnvVars) {
        Objects.requireNonNull(removeEnvVars, "Must pass non-null Collection");
        final Map<String, String> newenv = Maps.newHashMap(System.getenv());
        newenv.keySet().removeAll(removeEnvVars);
        setEnvironmentVariables(newenv);
    }

    /**
     * Re-set the environment variables with passed map.
     * 
     * @param newEnvVars map to reset env-vars with.
     */
    public static void setEnvironmentVariables(final Map<String, String> newEnvVars) {
        Objects.requireNonNull(newEnvVars, "Must pass non-null Map");

        try {
            final Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            final Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            final Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newEnvVars);
            final Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            final Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newEnvVars);
        } catch (final ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            final Class[] classes = Collections.class.getDeclaredClasses();
            final Map<String, String> env = System.getenv();
            for (final Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    try {
                        final Field field = cl.getDeclaredField("m");
                        field.setAccessible(true);
                        final Object obj = field.get(env);
                        final Map<String, String> map = (Map<String, String>) obj;
                        map.clear();
                        map.putAll(newEnvVars);
                    } catch (final NoSuchFieldException | IllegalAccessException e2) {
                        throw Throwables.propagate(e2);
                    }
                }
            }
        }
    }

    protected BitbucketUtils() {
        throw new UnsupportedOperationException("Purposefully not implemented");
    }
}
