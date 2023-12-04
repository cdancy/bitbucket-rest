package com.cdancy.bitbucket.rest.domain.pullrequest;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Conflict {
    @Nullable
    public abstract ConflictChange ourChange();

    @Nullable
    public abstract ConflictChange theirChange();

    Conflict() {
    }

    @SerializedNames({ "ourChange", "theirChange" })
    public static Conflict create(final ConflictChange ourChange,
                                final ConflictChange theirChange) {

        return new AutoValue_Conflict(ourChange, theirChange);
    }
}
