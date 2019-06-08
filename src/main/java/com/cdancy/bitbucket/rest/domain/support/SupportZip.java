package com.cdancy.bitbucket.rest.domain.support;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class SupportZip {
    public abstract String taskId();

    public abstract Integer progressPercentage();

    @Nullable
    public abstract String progressMessage();

    @Nullable
    public abstract List<String> warnings();

    public abstract String status();

    public abstract String fileName();

    @SerializedNames({"taskId", "progressPercentage", "progressMessage", "warnings", "status", "fileName"})
    public static SupportZip create(final String taskId,
                                        final Integer progressPercentage,
                                        final String progressMessage,
                                        final List<String> warnings,
                                        final String status,
                                        final String fileName) {
        return new AutoValue_SupportZip(taskId,
                                            progressPercentage,
                                            progressMessage,
                                            warnings,
                                            status,
                                            fileName);
    }

}
