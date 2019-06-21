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

package com.cdancy.bitbucket.rest.domain.support;

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import java.util.List;

@AutoValue
public abstract class SupportZip implements ErrorsHolder{
    public abstract String taskId();

    public abstract Integer progressPercentage();

    public abstract String progressMessage();

    public abstract List<String> warnings();

    public abstract String status();

    @Nullable
    public abstract String fileName();

    @SerializedNames({"taskId", "progressPercentage", "progressMessage",
                      "warnings", "status", "fileName",
                      "errors"})
    public static SupportZip create(final String taskId,
                                    final Integer progressPercentage,
                                    final String progressMessage,
                                    final List<String> warnings,
                                    final String status,
                                    final String fileName,
                                    final List<Error> errors) {
        return new AutoValue_SupportZip(BitbucketUtils.nullToEmpty(errors),
                                        taskId,
                                        progressPercentage,
                                        progressMessage,
                                        warnings,
                                        status,
                                        fileName);
    }

}
