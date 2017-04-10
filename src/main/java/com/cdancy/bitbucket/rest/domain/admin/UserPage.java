package com.cdancy.bitbucket.rest.domain.admin;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.common.Page;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.utils.Utils;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

public abstract class UserPage implements Page<User>, ErrorsHolder {

    @SerializedNames({ "start", "limit", "size", "nextPageStart", "isLastPage", "values", "errors" })
    public static UserPage create(int start, int limit, int size, int nextPageStart, boolean isLastPage,
                                  @Nullable List<User> values, @Nullable List<Error> errors) {
        return new AutoValue_UserPage(start, limit, size, nextPageStart, isLastPage,
            Utils.nullToEmpty(values), Utils.nullToEmpty(errors));
    }
}
