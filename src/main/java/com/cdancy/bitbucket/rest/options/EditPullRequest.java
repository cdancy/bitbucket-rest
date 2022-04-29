package com.cdancy.bitbucket.rest.options;

import com.cdancy.bitbucket.rest.domain.pullrequest.Person;
import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class EditPullRequest {

    public abstract int id();

    public abstract int version();

    public abstract String title();

    @Nullable
    public abstract String description();

    // default to empty List if null
    @Nullable
    public abstract List<Person> reviewers();

    EditPullRequest() {
    }

    @SerializedNames({"id", "version", "title", "description", "reviewers"})
    public static EditPullRequest create(final int id,
                                         final int version,
                                         final String title,
                                         final String description,
                                         final List<Person> reviewers) {
        return new AutoValue_EditPullRequest(id, version, title, description, reviewers);
    }
}
