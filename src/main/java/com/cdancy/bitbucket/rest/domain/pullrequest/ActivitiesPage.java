package com.cdancy.bitbucket.rest.domain.pullrequest;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.common.Page;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class ActivitiesPage implements Page<Activities>, ErrorsHolder {

    @SerializedNames({ "start", "limit", "size", "nextPageStart", "isLastPage", "values", "errors" })
    public static ActivitiesPage create(int start, int limit, int size, int nextPageStart, boolean isLastPage,
                                    @Nullable List<Activities> values, @Nullable List<Error> errors) {
        return new AutoValue_ActivitiesPage(start, limit, size, nextPageStart, isLastPage,
            Utils.nullToEmpty(values), Utils.nullToEmpty(errors));
    }
}
