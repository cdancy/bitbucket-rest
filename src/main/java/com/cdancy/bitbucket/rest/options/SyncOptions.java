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

package com.cdancy.bitbucket.rest.options;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class SyncOptions {

    public enum ACTION {
        MERGE,
        DISCARD
    }

    public abstract String refId();

    public abstract ACTION action();

    @Nullable
    public abstract Context context();

    SyncOptions() {
    }

    public static SyncOptions create(final String refId,
                                     final String action,
                                     final String message) {
        return create(refId, ACTION.valueOf(action), Context.create(message));
    }

    public static SyncOptions create(final String refId,
                                     final ACTION action,
                                     final String message) {
        return create(refId, action, Context.create(message));
    }

    @SerializedNames({ "refId", "action", "context" })
    public static SyncOptions create(final String refId,
                                     final ACTION action,
                                     final Context context) {
        return new AutoValue_SyncOptions(refId, action, context);
    }
}
