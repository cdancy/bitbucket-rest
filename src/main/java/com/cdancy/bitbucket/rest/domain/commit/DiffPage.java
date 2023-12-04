package com.cdancy.bitbucket.rest.domain.commit;

import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class DiffPage implements ErrorsHolder {
    @SerializedNames({"errors", "fromHash", "toHash", "contextLines", "whitespace", "truncated", "diffs"})
    public static DiffPage create(@Nullable final List<Error> errors, final String fromHash, final String toHash, final Integer contextLines, final String whitespace, final boolean truncated, final List<Diff> diffs) {
        return new AutoValue_DiffPage(errors, fromHash, toHash, contextLines, whitespace, truncated, diffs);
    }

    public abstract String fromHash();

    public abstract String toHash();

    public abstract Integer contextLines();

    public abstract String whitespace();

    public abstract boolean truncated();

    public abstract List<Diff> diffs();
}
