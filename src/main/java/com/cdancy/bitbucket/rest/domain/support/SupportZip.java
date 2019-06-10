package com.cdancy.bitbucket.rest.domain.support;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class SupportZip {
    public abstract String fileName();

    //public abstract SupportZipDetails details();

    @SerializedNames({"fileName"})
    public static SupportZip create(final String fileName) {
        return new AutoValue_SupportZip(fileName);
    }

}
