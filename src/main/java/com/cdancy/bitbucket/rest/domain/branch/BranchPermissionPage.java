package com.cdancy.bitbucket.rest.domain.branch;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.common.Page;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

/**
 * Created by jdoire on 07/03/2017.
 */
@AutoValue
public abstract class BranchPermissionPage implements Page<BranchPermission>, ErrorsHolder {

    @SerializedNames({ "start", "limit", "size", "nextPageStart", "isLastPage", "values", "errors" })
    public static BranchPermissionPage create(int start, int limit, int size, int nextPageStart, boolean isLastPage,
                                    @Nullable List<BranchPermission> values, @Nullable List<Error> errors) {
        return new AutoValue_BranchPermissionPage(start, limit, size, nextPageStart, isLastPage,
            Utils.nullToEmpty(values), Utils.nullToEmpty(errors));
    }
}
