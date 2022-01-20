package com.cdancy.bitbucket.rest.domain.category;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Category {

    public abstract int id();
    public abstract String title();

    Category() {
    }

    @SerializedNames({"id", "title"})
    public static Category create(final int id, final String title) {
        return new AutoValue_Category(id, title);
    }
}
