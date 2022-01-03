package com.cdancy.bitbucket.rest.domain.category;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ProjectCategoriesPage {

    /**
     * will contain error messages, if the call fails.
     */
    public abstract String message();

    /**
     * the categories assigned to the project
     */
    public abstract ProjectCategory result();

    ProjectCategoriesPage() {}

    @SerializedNames({"message", "result"})
    public static ProjectCategoriesPage create(final String message, final ProjectCategory result) {
        return new AutoValue_ProjectCategoriesPage(message, result);
    }
}
