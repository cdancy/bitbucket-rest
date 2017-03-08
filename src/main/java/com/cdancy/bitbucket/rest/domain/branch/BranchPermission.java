package com.cdancy.bitbucket.rest.domain.branch;

import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class BranchPermission {
    @Nullable
    public abstract Long id();

    public abstract BranchPermissionEnumType type();

    public abstract Matcher matcher();

    public abstract List<User> users();

    public abstract List<String> groups();

    @SerializedNames({"id", "type", "matcher", "users", "groups"})
    public static BranchPermission create(Long id, BranchPermissionEnumType type, Matcher matcher,
                                          List<User> users, List<String> groups) {
        return new AutoValue_BranchPermission(id, type, matcher, users, groups);
    }
}
