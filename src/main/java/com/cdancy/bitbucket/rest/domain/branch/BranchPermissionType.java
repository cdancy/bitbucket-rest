package com.cdancy.bitbucket.rest.domain.branch;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class BranchPermissionType {
    public abstract String id();
    public abstract String name();

    @SerializedNames({ "id", "name"})
    public static BranchPermissionType create(String id, String name) {
        return new AutoValue_BranchPermissionType(id, name);
    }

    public static BranchPermissionType create(Matcher.MatcherId matcherId) {
        return new AutoValue_BranchPermissionType(matcherId.getTypeId(), matcherId.getTypeName());
    }
}
