package com.cdancy.bitbucket.rest.domain.commit;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class DiffHunk {
    @SerializedNames({"sourceLine", "sourceSpan", "destinationLine", "destinationSpan", "truncated", "segments"})
    public static DiffHunk create(final Integer sourceLine, final Integer sourceSpan, final Integer destinationLine, final Integer destinationSpan, final boolean truncated, final List<DiffHunkSegment> segments) {
        return new AutoValue_DiffHunk(sourceLine, sourceSpan, destinationLine, destinationSpan, truncated, segments);
    }

    public abstract Integer sourceLine();

    public abstract Integer sourceSpan();

    public abstract Integer destinationLine();

    public abstract Integer destinationSpan();

    public abstract boolean truncated();

    public abstract List<DiffHunkSegment> segments();
}
