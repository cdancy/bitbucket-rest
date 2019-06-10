package com.cdancy.bitbucket.rest.domain.support;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import java.util.List;

@AutoValue
public abstract class SupportZipDetails {
    public abstract String taskId();

    public abstract Integer progressPercentage();

    @Nullable
    public abstract String progressMessage();

    @Nullable
    public abstract List<String> warnings();

    public abstract String status();

    @SerializedNames({"taskId", "progressPercentage", "progressMessage", "warnings", "status"})
    public static SupportZipDetails create(final String taskId,
                                           final Integer progressPercentage,
                                           final String progressMessage,
                                           final List<String> warnings,
                                           final String status) {
        return new AutoValue_SupportZipDetails(taskId,
                                            progressPercentage,
                                            progressMessage,
                                            warnings,
                                            status);
    }
}
