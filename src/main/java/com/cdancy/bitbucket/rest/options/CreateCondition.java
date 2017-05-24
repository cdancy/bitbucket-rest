package com.cdancy.bitbucket.rest.options;

import com.cdancy.bitbucket.rest.domain.branch.Matcher;
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class CreateCondition {

    @Nullable
    public abstract Long id();

    public abstract Repository repository();

    public abstract Matcher sourceMatcher();

    public abstract Matcher targetMatcher();

    public abstract List<User> reviewers();

    public abstract Long requiredApprovals();

    @SerializedNames({ "id", "repository", "sourceMatcher", "targetMatcher", "reviewers", "requiredApprovals"})
    public static CreateCondition create(Long id, Repository repository, Matcher sourceMatcher,
                                   Matcher targetMatcher, List<User> reviewers, Long requiredApprovals) {
        return new AutoValue_CreateCondition(id, repository, sourceMatcher, targetMatcher, reviewers, requiredApprovals);
    }

    public static CreateCondition create(Condition condition) {
        return new AutoValue_CreateCondition(condition.id(), condition.repository(), condition.sourceRefMatcher(),
            condition.targetRefMatcher(), condition.reviewers(), condition.requiredApprovals());
    }
}
