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

import com.cdancy.bitbucket.rest.domain.common.Veto;
import java.util.List;

import org.jclouds.json.SerializedNames;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.utils.BitbucketUtils;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class MergeStatus implements ErrorsHolder {

    public abstract boolean canMerge();

    public abstract boolean conflicted();

    public abstract List<Veto> vetoes();

    MergeStatus() {
    }

    @SerializedNames({ "canMerge", "conflicted", "vetoes", "errors" })
    public static MergeStatus create(final boolean canMerge, 
            final boolean conflicted, 
            final List<Veto> vetoes, 
            final List<Error> errors) {
        
        return new AutoValue_MergeStatus(BitbucketUtils.nullToEmpty(errors), 
                canMerge, 
                conflicted, 
                BitbucketUtils.nullToEmpty(vetoes));
    }
}
