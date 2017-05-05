package com.cdancy.bitbucket.rest.domain.defaultReviewers;

import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.Matcher;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class Conditions {
    public abstract Long id();

    public abstract Repository repository();

    public abstract Matcher sourceRefMatcher();

    public abstract Matcher targetRefMatcher();

    public abstract List<User> reviewers();

    public abstract Long requiredApprovals();

    @SerializedNames({ "id", "repository", "sourceRefMatcher", "targetRefMatcher", "reviewers", "requiredApprovals"})
    public static Conditions create(Long id, Repository repository, Matcher sourceRefMatcher, Matcher targetRefMatcher, List<User> reviewers, Long requiredApprovals) {
        return new AutoValue_Conditions(id, repository, sourceRefMatcher, targetRefMatcher, reviewers, requiredApprovals);
    }
}
