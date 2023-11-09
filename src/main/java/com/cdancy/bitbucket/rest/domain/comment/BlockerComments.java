package com.cdancy.bitbucket.rest.domain.comment;

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.ErrorsHolder;
import com.cdancy.bitbucket.rest.domain.pullrequest.Author;
import com.google.auto.value.AutoValue;
import com.google.gson.JsonElement;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class BlockerComments implements ErrorsHolder {
    public enum BlockingCommentState {
        OPEN,
        PENDING,
        RESOLVED
    }

    public abstract Map<String, JsonElement> properties();

    public abstract int id();

    public abstract int version();

    @Nullable
    public abstract String text();

    @Nullable
    public abstract Author author();

    public abstract long createdDate();

    public abstract long updatedDate();
    public abstract boolean threadResolved();
    public abstract String severity();
    public abstract BlockingCommentState state();

    @Nullable
    public abstract PermittedOperations permittedOperations();

    @Nullable
    public abstract Author resolver();

    BlockerComments() {
    }

    @SerializedNames({ "properties", "id", "version", "text", "author",
        "createdDate", "updatedDate", "resolver", "threadResolved", "state", "severity", "permittedOperations", "errors" })
    public static BlockerComments create(final Map<String, JsonElement> properties,
                                                                           final int id,
                                                                           final int version,
                                                                           final String text,
                                                                           final Author author,
                                                                           final long createdDate,
                                                                           final long updatedDate,
                                                                          final Author resolver,
                                                                           final Boolean threadResolved,
                                                                           final BlockingCommentState state,
                                                                           final String severity,
                                                                           final PermittedOperations permittedOperations,
                                                                           final List<Error> errors) {

        return new AutoValue_BlockerComments(BitbucketUtils.nullToEmpty(errors),
            BitbucketUtils.nullToEmpty(properties),
            id,
            version,
            text,
            author,
            createdDate,
            updatedDate,
            threadResolved,
            severity,
            state,
            permittedOperations,
            resolver);
    }
}
