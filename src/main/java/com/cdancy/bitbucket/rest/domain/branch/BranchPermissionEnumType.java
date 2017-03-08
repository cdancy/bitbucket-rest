package com.cdancy.bitbucket.rest.domain.branch;

public enum BranchPermissionEnumType {
    fast_forward_only("Rewriting history", "fast-forward-only",
        "prevents history rewrites on the specified branch(es) - for example by a force push or rebase."),
    no_deletes("Deletion", "no-deletes", "prevents branch and tag deletion"),
    pull_request_only("Changes without a pull request", "pull-request-only",
        "prevents pushing changes directly to the specified branch(es); changes are allowed only with a pull request"),
    read_only("All changes", "read-only",
        "prevents pushes to the specified branch(es) and restricts creating new"
            + " branches matching the specified branch(es) or pattern");

    private String name;
    private String apiName;
    private String description;

    BranchPermissionEnumType(String name, String apiName, String description) {
        this.name = name;
        this.apiName = apiName;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getApiName() {
        return apiName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Convert value from Api to enum.
     *
     * @param apiName ApiName
     * @return value
     */
    public static BranchPermissionEnumType fromValue(String apiName) {
        for (BranchPermissionEnumType enumType : BranchPermissionEnumType.values()) {
            if (enumType.getApiName().equals(apiName)) {
                return enumType;
            }
        }
        throw new IllegalArgumentException("Value not Found");
    }

    @Override
    public String toString() {
        return this.getApiName();
    }
}
