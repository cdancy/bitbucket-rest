package com.cdancy.bitbucket.rest.domain.activities;

import com.cdancy.bitbucket.rest.domain.commit.Commit;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class ActivitiesCommit {
    @Nullable
    public abstract List<Commit> commits();

    public abstract long total();

    @SerializedNames({"commits", "total"})
    public static ActivitiesCommit create(List<Commit> commits, long total) {
        return new AutoValue_ActivitiesCommit(commits, total);
    }
}
