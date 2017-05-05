package com.cdancy.bitbucket.rest.domain.repository;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.common.Page;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class PermissionsPage implements Page<Permissions>, ErrorsHolder {

    @SerializedNames({ "start", "limit", "size", "nextPageStart", "isLastPage", "values", "errors" })
    public static PermissionsPage create(int start, int limit, int size, int nextPageStart, boolean isLastPage,
                                        @Nullable List<Permissions> values, @Nullable List<Error> errors) {
        return new AutoValue_PermissionsPage(start, limit, size, nextPageStart, isLastPage,
            Utils.nullToEmpty(values), Utils.nullToEmpty(errors));
    }
}
