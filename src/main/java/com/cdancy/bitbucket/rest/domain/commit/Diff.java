package com.cdancy.bitbucket.rest.domain.commit;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.pullrequest.Path;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class Diff implements ErrorsHolder {
    @SerializedNames({"errors", "source", "destination", "truncated", "hunks"})
    public static Diff create(@Nullable final List<Error> errors, final Path source, final Path destination, final boolean truncated, final List<DiffHunk> hunks) {
        return new AutoValue_Diff(errors, source, destination, truncated, hunks);
    }

    public abstract Path source();

    public abstract Path destination();

    public abstract boolean truncated();

    public abstract List<DiffHunk> hunks();
}
