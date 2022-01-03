package com.cdancy.bitbucket.rest.domain.category;

import java.util.List;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RepositoryCategory implements ProjectHolder, CategoriesHolder {

	public abstract String repositoryId();

	public abstract String repositoryName();

	public abstract String repositorySlug();

	RepositoryCategory() {}

	@SerializedNames({ "projectId", "projectKey", "projectName", "categories", "repositoryId", "repositoryName", "repositorySlug" })
	public static RepositoryCategory create(final String projectId,
			final String projectKey,
			final String projectName,
			final List<Category> categories,
			final String repositoryId,
			final String repositoryName,
			final String repositorySlug) {
		return new AutoValue_RepositoryCategory(projectId, projectKey, projectName,
				categories,
				repositoryId, repositoryName, repositorySlug);
	}
}
