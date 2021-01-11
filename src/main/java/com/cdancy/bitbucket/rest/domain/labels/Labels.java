package com.cdancy.bitbucket.rest.domain.labels;

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class Labels implements ErrorsHolder {

    public abstract String name();

    Labels() {
    }

    @SerializedNames({"name", "errors"})
    public static Labels create(final String name, final List<Error> errors) {
        return new AutoValue_Labels(BitbucketUtils.nullToEmpty(errors), name);
    }
}
