package com.cdancy.bitbucket.rest.domain.repository;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Group {

    public abstract String name();

    @SerializedNames({"name"})
    public static Group create(String name) {
        return new AutoValue_Group(name);
    }
}
