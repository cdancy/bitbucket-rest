/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.bitbucket.rest.fallbacks;

import com.cdancy.bitbucket.rest.domain.activities.ActivitiesPage;
import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.BranchModel;
import com.cdancy.bitbucket.rest.domain.branch.BranchModelConfiguration;
import com.cdancy.bitbucket.rest.domain.branch.BranchPage;
import com.cdancy.bitbucket.rest.domain.branch.BranchPermissionPage;
import com.cdancy.bitbucket.rest.domain.build.StatusPage;
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.commit.Commit;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.participants.Participants;
import com.cdancy.bitbucket.rest.domain.participants.Participants.Role;
import com.cdancy.bitbucket.rest.domain.participants.Participants.Status;
import com.cdancy.bitbucket.rest.domain.participants.ParticipantsPage;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.project.ProjectPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.domain.pullrequest.CommentPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.MergeStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequestPage;
import com.cdancy.bitbucket.rest.domain.repository.Hook;
import com.cdancy.bitbucket.rest.domain.repository.HookPage;
import com.cdancy.bitbucket.rest.domain.repository.PermissionsPage;
import com.cdancy.bitbucket.rest.domain.repository.PullRequestSettings;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.domain.tags.Tag;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jclouds.Fallback;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

public final class BitbucketFallbacks {

    private static final JsonParser parser = new JsonParser();

    public static final class FalseOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return Boolean.FALSE;
            }
            throw propagate(throwable);
        }
    }

    public static final class BranchOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createBranchFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class BranchModelOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createBranchModelFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class BranchModelConfigurationOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createBranchModelConfigurationFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class BranchPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createBranchPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class UserPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createUserPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class StatusPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createStatusPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class BranchPermissionPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createBranchPermissionPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ChangePageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createChangePageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class CommentsOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createCommentsFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class CommentPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createCommentPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class CommitPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createCommitPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class CommitOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createCommitFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class TagOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createTagFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class RepositoryOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createRepositoryFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class RepositoryPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createRepositoryPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class PermissionsPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createPermissionsPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class HookPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createHookPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class HookOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createHookFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ProjectOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createProjectFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class PullRequestSettingsOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createPullRequestSettingsFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ProjectPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createProjectPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class PullRequestOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createPullRequestFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ActivitiesPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createActivitiesPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ParticipantsPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createParticipantsPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ParticipantsOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createParticipantsFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class PullRequestPageOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createPullRequestPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class MergeStatusOnError implements Fallback<Object> {
        public Object createOrPropagate(Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createMergeStatusFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static Branch createBranchFromErrors(List<Error> errors) {
        return Branch.create(null, null, null, null, null, false, null, errors);
    }

    public static BranchModel createBranchModelFromErrors(List<Error> errors) {
        return BranchModel.create(null, null, null, errors);
    }

    public static BranchModelConfiguration createBranchModelConfigurationFromErrors(List<Error> errors) {
        return BranchModelConfiguration.create(null, null, null, errors);
    }

    public static BranchPage createBranchPageFromErrors(List<Error> errors) {
        return BranchPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static UserPage createUserPageFromErrors(List<Error> errors) {
        return UserPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static StatusPage createStatusPageFromErrors(List<Error> errors) {
        return StatusPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static BranchPermissionPage createBranchPermissionPageFromErrors(List<Error> errors) {
        return BranchPermissionPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static ChangePage createChangePageFromErrors(List<Error> errors) {
        return ChangePage.create(0, 0, 0, 0, true, null, errors);
    }

    public static Comments createCommentsFromErrors(List<Error> errors) {
        return Comments.create(null, 0, 0, null, null, 0, 0, null, null, null, null, errors);
    }

    public static CommentPage createCommentPageFromErrors(List<Error> errors) {
        return CommentPage.create(0, 0, 0, 0, true, null, errors);
    }

    public static CommitPage createCommitPageFromErrors(List<Error> errors) {
        return CommitPage.create(0, 0, 0, 0, true, null, errors, -1, -1);
    }

    public static Commit createCommitFromErrors(List<Error> errors) {
        return Commit.create("-1", "-1", null, 0, null, null, errors);
    }

    public static Tag createTagFromErrors(List<Error> errors) {
        return Tag.create(null, null, null, null, null, null, errors);
    }

    public static Repository createRepositoryFromErrors(List<Error> errors) {
        return Repository.create(null, -1, null, null, null, null, false, null, false, null, errors);
    }

    public static RepositoryPage createRepositoryPageFromErrors(List<Error> errors) {
        return RepositoryPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static PermissionsPage createPermissionsPageFromErrors(List<Error> errors) {
        return PermissionsPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static HookPage createHookPageFromErrors(List<Error> errors) {
        return HookPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static Hook createHookFromErrors(List<Error> errors) {
        return Hook.create(null, false, false, errors);
    }

    public static Project createProjectFromErrors(List<Error> errors) {
        return Project.create(null, -1, null, null, false, null, null, errors);
    }

    public static PullRequestSettings createPullRequestSettingsFromErrors(List<Error> errors) {
        return PullRequestSettings.create(null, null, null, null, null, errors);
    }

    public static ProjectPage createProjectPageFromErrors(List<Error> errors) {
        return ProjectPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static PullRequest createPullRequestFromErrors(List<Error> errors) {
        return PullRequest.create(-1, -1, null, null, null,
                false, false, 0, 0, null,
                null, false, null, null, null,
                null, null, errors);
    }

    public static ActivitiesPage createActivitiesPageFromErrors(List<Error> errors) {
        return ActivitiesPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static ParticipantsPage createParticipantsPageFromErrors(List<Error> errors) {
        return ParticipantsPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static Participants createParticipantsFromErrors(List<Error> errors) {
        return Participants.create(null, null, Role.REVIEWER, false, Status.UNAPPROVED, errors);
    }

    public static PullRequestPage createPullRequestPageFromErrors(List<Error> errors) {
        return PullRequestPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static MergeStatus createMergeStatusFromErrors(List<Error> errors) {
        return MergeStatus.create(false, false, null, errors);
    }

    /**
     * Parse list of Error's from output.
     *
     * @param output json containing errors hash
     * @return List of Error's or empty list if none could be found
     */
    public static List<Error> getErrors(String output) {
        JsonElement element = parser.parse(output);
        JsonObject object = element.getAsJsonObject();
        JsonArray errorsArray = object.get("errors").getAsJsonArray();

        List<Error> errors = Lists.newArrayList();
        Iterator<JsonElement> it = errorsArray.iterator();
        while (it.hasNext()) {
            JsonObject obj = it.next().getAsJsonObject();
            JsonElement context = obj.get("context");
            JsonElement message = obj.get("message");
            JsonElement exceptionName = obj.get("exceptionName");
            Error error = Error.create(!context.isJsonNull() ? context.getAsString() : null,
                    !message.isJsonNull() ? message.getAsString() : null,
                    !exceptionName.isJsonNull() ? exceptionName.getAsString() : null);
            errors.add(error);
        }

        return errors;
    }
}
