package com.cdancy.bitbucket.rest.domain.pullrequest;

import com.cdancy.bitbucket.rest.domain.activities.ActivitiesCommit;
import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Activities {
    public abstract long id();

    public abstract long createdDate();

    public abstract User user();

    public abstract String action();

    public abstract String fromHash();

    public abstract String previousFromHash();

    public abstract String previousToHash();

    public abstract String toHash();

    public abstract ActivitiesCommit added();

    public abstract ActivitiesCommit removed();

    @SerializedNames({"id", "createdDate", "size", "user", "action", "fromHash", "previousFromHash", "previousToHash", "toHash", "added", "removed"})
    public static Activities create(long id, long createdDate, User user, String action, String fromHash,
                                        String previousFromHash, String previousToHash, String toHash,
                                        ActivitiesCommit added, ActivitiesCommit removed) {
        return new AutoValue_Activities(id, createdDate, user, action, fromHash, previousFromHash, previousToHash,
            toHash, added, removed);
    }
}
