package com.cdancy.bitbucket.rest.domain.commit;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class DiffHunkSegment {
    @SerializedNames({"type", "lines", "truncated"})
    public static DiffHunkSegment create(final String type, final List<DiffHunkSegmentLine> lines, final boolean truncated) {
        return new AutoValue_DiffHunkSegment(type, lines, truncated);
    }

    /**
     * @return "REMOVED", "ADDED", "CONTEXT", ?
     */
    public abstract String type();

    public abstract List<DiffHunkSegmentLine> lines();

    public abstract boolean truncated();
}
