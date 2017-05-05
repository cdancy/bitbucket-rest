package com.cdancy.bitbucket.rest.domain.repository;

import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Permissions {

    public enum PermissionsType {
        REPO_ADMIN,
        REPO_WRITE,
        REPO_READ
    }

    @Nullable
    public abstract User user();

    @Nullable
    public abstract Group group();

    public abstract PermissionsType permission();

    @SerializedNames({"user", "group", "permission"})
    public static Permissions create(User user, Group group, PermissionsType type) {
        return new AutoValue_Permissions(user, group, type);
    }
}
