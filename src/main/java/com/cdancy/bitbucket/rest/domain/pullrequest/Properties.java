package com.cdancy.bitbucket.rest.domain.pullrequest;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.Links;
import com.cdancy.bitbucket.rest.utils.Utils;
import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

import java.util.List;

/**
 * Created by jdoire on 07/03/2017.
 */
@AutoValue
public abstract class Properties {
    public abstract Long openTaskCount();
    public abstract Long resolvedTaskCount();

    @SerializedNames({ "openTaskCount", "resolvedTaskCount"})
    public static Properties create(long openTaskCount, long resolvedTaskCount) {
        return new AutoValue_Properties(openTaskCount, resolvedTaskCount);
    }
}
