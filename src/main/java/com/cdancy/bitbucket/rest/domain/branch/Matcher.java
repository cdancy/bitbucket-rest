package com.cdancy.bitbucket.rest.domain.branch;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Matcher {

    public enum MatcherId {
        release("RELEASE", "Release", "MODEL_CATEGORY", "Branching model category"),
        develop("development", "Development", "MODEL_BRANCH", "Branching model branch"),
        master("production", "Production", "MODEL_BRANCH", "Branching model branch");


        private String id;
        private String name;
        private String typeId;
        private String typeName;

        MatcherId(String pId, String pName, String pTypeId, String pTypeName) {
            this.id = pId;
            this.name = pName;
            this.typeId = pTypeId;
            this.typeName = pTypeName;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getTypeId() {
            return typeId;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    public abstract String id();

    public abstract String displayId();

    public abstract BranchPermissionType type();

    public abstract Boolean active();

    @SerializedNames({"id", "displayId", "type", "active"})
    public static Matcher create(String id, String displayId, BranchPermissionType type,
                                  Boolean active) {
        return new AutoValue_Matcher(id, displayId, type, active);
    }

    public static Matcher create(MatcherId matcherId, Boolean active) {
        return new AutoValue_Matcher(matcherId.getId(), matcherId.getName(), BranchPermissionType.create(matcherId), active);
    }
}
