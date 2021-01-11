package com.cdancy.bitbucket.rest.domain.labels;

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.common.Page;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class LabelsPage implements Page<Labels>, ErrorsHolder {

    @SerializedNames({"start", "limit", "size", "nextPageStart",
        "isLastPage", "values", "errors"})
    public static LabelsPage create(final int start,
                                    final int limit,
                                    final int size,
                                    final int nextPageStart,
                                    final boolean isLastPage,
                                    final List<Labels> values,
                                    @Nullable final List<Error> errors) {

        return new AutoValue_LabelsPage(start,
            limit,
            size,
            nextPageStart,
            isLastPage,
            BitbucketUtils.nullToEmpty(values),
            BitbucketUtils.nullToEmpty(errors));
    }
}
