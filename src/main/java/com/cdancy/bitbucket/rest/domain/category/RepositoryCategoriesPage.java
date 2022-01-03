package com.cdancy.bitbucket.rest.domain.category;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RepositoryCategoriesPage {

    /**
     * will contain error messages, if the call fails.
     */
    public abstract String message();

    /**
     * the categories assigned to the project/repository
     */
    public abstract RepositoryCategory result();

    RepositoryCategoriesPage() {}

    @SerializedNames({"message", "result"})
    public static RepositoryCategoriesPage create(final String message, final RepositoryCategory result) {
        return new AutoValue_RepositoryCategoriesPage(message, result);
    }
}
