package com.cdancy.bitbucket.rest.domain.commit;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class DiffHunkSegmentLine {
    @SerializedNames({"source", "destination", "line", "truncated"})
    public static DiffHunkSegmentLine create(final Integer source, final Integer destination, final String line, final boolean truncated) {
        return new AutoValue_DiffHunkSegmentLine(source, destination, line, truncated);
    }

    public abstract Integer source();

    public abstract Integer destination();

    public abstract String line();

    public abstract boolean truncated();
}
