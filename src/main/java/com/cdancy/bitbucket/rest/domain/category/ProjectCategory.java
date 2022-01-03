package com.cdancy.bitbucket.rest.domain.category;

import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ProjectCategory implements ProjectHolder, CategoriesHolder {

    ProjectCategory() {}

    @SerializedNames({"projectId", "projectKey", "projectName", "categories"})
    public static ProjectCategory create(final String projectId,
                                         final String projectKey,
                                         final String projectName,
                                         final List<Category> categories) {
        return new AutoValue_ProjectCategory(projectId, projectKey, projectName, categories);
    }
}
