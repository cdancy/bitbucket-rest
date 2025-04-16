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

import com.cdancy.bitbucket.rest.BitbucketUtils;
import com.cdancy.bitbucket.rest.domain.activities.ActivitiesPage;
import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.cdancy.bitbucket.rest.domain.branch.Branch;
import com.cdancy.bitbucket.rest.domain.branch.BranchModel;
import com.cdancy.bitbucket.rest.domain.branch.BranchModelConfiguration;
import com.cdancy.bitbucket.rest.domain.branch.BranchPage;
import com.cdancy.bitbucket.rest.domain.branch.BranchRestrictionPage;
import com.cdancy.bitbucket.rest.domain.build.StatusPage;
import com.cdancy.bitbucket.rest.domain.comment.Comments;
import com.cdancy.bitbucket.rest.domain.comment.LikePage;
import com.cdancy.bitbucket.rest.domain.comment.Task;
import com.cdancy.bitbucket.rest.domain.commit.Commit;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.common.Error;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.domain.common.Veto;
import com.cdancy.bitbucket.rest.domain.defaultreviewers.Condition;
import com.cdancy.bitbucket.rest.domain.file.FilesPage;
import com.cdancy.bitbucket.rest.domain.file.LastModified;
import com.cdancy.bitbucket.rest.domain.file.LinePage;
import com.cdancy.bitbucket.rest.domain.file.RawContent;
import com.cdancy.bitbucket.rest.domain.insights.AnnotationsResponse;
import com.cdancy.bitbucket.rest.domain.insights.InsightReport;
import com.cdancy.bitbucket.rest.domain.insights.InsightReportPage;
import com.cdancy.bitbucket.rest.domain.labels.Label;
import com.cdancy.bitbucket.rest.domain.labels.LabelsPage;
import com.cdancy.bitbucket.rest.domain.participants.Participants;
import com.cdancy.bitbucket.rest.domain.participants.Participants.Role;
import com.cdancy.bitbucket.rest.domain.participants.Participants.Status;
import com.cdancy.bitbucket.rest.domain.participants.ParticipantsPage;
import com.cdancy.bitbucket.rest.domain.postwebhooks.PostWebHook;
import com.cdancy.bitbucket.rest.domain.project.Project;
import com.cdancy.bitbucket.rest.domain.project.ProjectPage;
import com.cdancy.bitbucket.rest.domain.project.ProjectPermissionsPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.domain.pullrequest.CommentPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.MergeStatus;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequest;
import com.cdancy.bitbucket.rest.domain.pullrequest.PullRequestPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.User;
import com.cdancy.bitbucket.rest.domain.repository.Hook;
import com.cdancy.bitbucket.rest.domain.repository.HookPage;
import com.cdancy.bitbucket.rest.domain.repository.HookSettings;
import com.cdancy.bitbucket.rest.domain.repository.PermissionsPage;
import com.cdancy.bitbucket.rest.domain.repository.PullRequestSettings;
import com.cdancy.bitbucket.rest.domain.repository.Repository;
import com.cdancy.bitbucket.rest.domain.repository.RepositoryPage;
import com.cdancy.bitbucket.rest.domain.repository.WebHook;
import com.cdancy.bitbucket.rest.domain.repository.WebHookPage;
import com.cdancy.bitbucket.rest.domain.sshkey.AccessKey;
import com.cdancy.bitbucket.rest.domain.sshkey.AccessKeyPage;
import com.cdancy.bitbucket.rest.domain.sync.SyncState;
import com.cdancy.bitbucket.rest.domain.sync.SyncStatus;
import com.cdancy.bitbucket.rest.domain.tags.Tag;
import com.cdancy.bitbucket.rest.domain.tags.TagPage;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.jclouds.Fallback;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class BitbucketFallbacks {

    public static final class BranchOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {

                // if the repo is empty, and thus has no default branch, a 204 is returned with 'null' as the content
                final Boolean obj = returnValueOnCodeOrNull(throwable, true, equalTo(204));
                if (obj != null) {
                    return Branch.create(null, null, null, null, null, false, null, null);
                } else {
                    return createBranchFromErrors(getErrors(throwable.getMessage()));
                }
            }
            throw propagate(throwable);
        }
    }

    public static final class BranchModelOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createBranchModelFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class BranchModelConfigurationOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createBranchModelConfigurationFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class BranchPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createBranchPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class LikePageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createLikePageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class UserPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createUserPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class UserOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createUserFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ConditionOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createConditionFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class StatusPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createStatusPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class BranchPermissionPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createBranchPermissionPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ChangePageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createChangePageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class CommentsOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createCommentsFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class CommentPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createCommentPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class CommitPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createCommitPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class CommitOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createCommitFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class TagOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createTagFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class TaskOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createTaskFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class RepositoryOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createRepositoryFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class RepositoryPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createRepositoryPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class PermissionsPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createPermissionsPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class HookPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createHookPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class HookOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createHookFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class HookSettingsOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createHookSettingsFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class AnnotationsResponseOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createAnnotationsResponseFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class InsightReportOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createInsightReportFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class InsightReportPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createInsightReportPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ProjectOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createProjectFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ProjectPermissionsPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createProjectPermissionsPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class PullRequestSettingsOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createPullRequestSettingsFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ProjectPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createProjectPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class PullRequestOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createPullRequestFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class SyncStatusOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                // if the repo sync is disabled a 204 is returned with 'null' as the content
                final Boolean is204 = returnValueOnCodeOrNull(throwable, true, equalTo(204));
                final boolean isAvailable = is204 != null;
                final List<Error> errors = getErrors(throwable.getMessage());
                if (errors.size() > 0
                        && errors.get(0).context() != null
                        && errors.get(0).context().startsWith("Error parsing input: Cannot invoke \"org.jclouds.io.Payload.openStream()\" because the return value of \"org.jclouds.http.HttpResponse.getPayload()\" is null")) {
                    return createSyncStatusFromErrors(isAvailable, null);
                } else {
                    return createSyncStatusFromErrors(isAvailable, errors);
                }
            }
            throw propagate(throwable);
        }
    }

    public static final class SyncStateOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                final Boolean is204 = returnValueOnCodeOrNull(throwable, true, equalTo(204));
                if (is204 != null && is204.booleanValue()) {
                    return SyncState.create(null, null, "SYNCED", null, null);
                } else {
                    return createSyncStateFromErrors(getErrors(throwable.getMessage()));
                }
            }
            throw propagate(throwable);
        }
    }

    public static final class ActivitiesPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createActivitiesPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ParticipantsPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createParticipantsPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class ParticipantsOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createParticipantsFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class PullRequestPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createPullRequestPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class TagPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createTagPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class MergeStatusOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createMergeStatusFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class LinePageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createLinePageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class FilesPageOnError implements Fallback<Object> {
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createFilesPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class LastModifiedOnError implements Fallback<Object> {
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createLastModifiedFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class RawContentOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                final Error error = Error.create(throwable.getMessage(), "Failed retrieving raw content",
                        throwable.getClass().getName(), false, null);
                return RawContent.create(null, Lists.newArrayList(error));
            }
            throw propagate(throwable);
        }
    }

    public static final class RequestStatusOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                try {
                    return createRequestStatusFromErrors(getErrors(throwable.getMessage()));
                } catch (JsonSyntaxException e) {
                    final Error error = Error.create(null, throwable.getMessage(),
                            throwable.getClass().getName(), false, null);
                    final List<Error> errors = Lists.newArrayList(error);
                    return RequestStatus.create(false, errors);
                }
            }
            throw propagate(throwable);
        }
    }

    public static final class WebHookPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createWebHookPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class WebHookOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createWebHookFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class PostWebHookOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createPostWebHookFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class PostWebHookListOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createPostWebHookListFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }


    public static final class AccessKeyOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createAccessKeyFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class AccessKeyPageOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createAccessKeyPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class LabelsOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createLabelsPageFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static final class LabelByNameOnError implements Fallback<Object> {
        @Override
        public Object createOrPropagate(final Throwable throwable) throws Exception {
            if (checkNotNull(throwable, "throwable") != null) {
                return createLabelByNameFromErrors(getErrors(throwable.getMessage()));
            }
            throw propagate(throwable);
        }
    }

    public static RequestStatus createRequestStatusFromErrors(final List<Error> errors) {
        return RequestStatus.create(false, errors);
    }

    public static RawContent createRawContentFromErrors(final List<Error> errors) {
        return RawContent.create(null, errors);
    }

    public static LinePage createLinePageFromErrors(final List<Error> errors) {
        return LinePage.create(-1, -1, -1, -1, true, null, null, errors);
    }

    public static FilesPage createFilesPageFromErrors(final List<Error> errors) {
        return FilesPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static LastModified createLastModifiedFromErrors(final List<Error> errors) {
        return LastModified.create(null, null, errors);
    }

    public static Branch createBranchFromErrors(final List<Error> errors) {
        return Branch.create(null, null, null, null, null, false, null, errors);
    }

    public static BranchModel createBranchModelFromErrors(final List<Error> errors) {
        return BranchModel.create(null, null, null, errors);
    }

    public static BranchModelConfiguration createBranchModelConfigurationFromErrors(final List<Error> errors) {
        return BranchModelConfiguration.create(null, null, null, errors);
    }

    public static BranchPage createBranchPageFromErrors(final List<Error> errors) {
        return BranchPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static UserPage createUserPageFromErrors(final List<Error> errors) {
        return UserPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static LikePage createLikePageFromErrors(final List<Error> errors) {
        return LikePage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static User createUserFromErrors(final List<Error> errors) {
        return User.create(errors, null, null, -1, null, false, null, null, null, false, -1, false, false);
    }

    public static Condition createConditionFromErrors(final List<Error> errors) {
        return Condition.create(null, null, null, null, null, null, errors);
    }

    public static StatusPage createStatusPageFromErrors(final List<Error> errors) {
        return StatusPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static BranchRestrictionPage createBranchPermissionPageFromErrors(final List<Error> errors) {
        return BranchRestrictionPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static ChangePage createChangePageFromErrors(final List<Error> errors) {
        return ChangePage.create(0, 0, 0, 0, true, null, errors);
    }

    public static Comments createCommentsFromErrors(final List<Error> errors) {
        return Comments.create(null, 0, 0, null, null, 0, 0, null, null, null, null, null, null, errors);
    }

    public static CommentPage createCommentPageFromErrors(final List<Error> errors) {
        return CommentPage.create(0, 0, 0, 0, true, null, errors);
    }

    public static CommitPage createCommitPageFromErrors(final List<Error> errors) {
        return CommitPage.create(0, 0, 0, 0, true, null, errors, -1, -1);
    }

    public static Commit createCommitFromErrors(final List<Error> errors) {
        return Commit.create("-1", "-1", null, 0, null, 0, null,null, null, errors);
    }

    public static Tag createTagFromErrors(final List<Error> errors) {
        return Tag.create(null, null, null, null, null, null, errors);
    }

    public static Task createTaskFromErrors(final List<Error> errors) {
        return Task.create(null, null, -1, -1, null, null, null, errors);
    }

    public static Repository createRepositoryFromErrors(final List<Error> errors) {
        return Repository.create(null, -1, null, null,null, null, null, false, null, null, false, null, errors);
    }

    public static RepositoryPage createRepositoryPageFromErrors(final List<Error> errors) {
        return RepositoryPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static PermissionsPage createPermissionsPageFromErrors(final List<Error> errors) {
        return PermissionsPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static HookPage createHookPageFromErrors(final List<Error> errors) {
        return HookPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static Hook createHookFromErrors(final List<Error> errors) {
        return Hook.create(null, false, false, errors);
    }

    public static HookSettings createHookSettingsFromErrors(final List<Error> errors) {
        return HookSettings.create(null, errors);
    }

    public static AnnotationsResponse createAnnotationsResponseFromErrors(final List<Error> errors) {
        return AnnotationsResponse.create(0, null, errors);
    }

    public static InsightReportPage createInsightReportPageFromErrors(final List<Error> errors) {
        return InsightReportPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static InsightReport createInsightReportFromErrors(final List<Error> errors) {
        return InsightReport.create(-1, null, null, null, null, null, null, null, null, errors);
    }

    public static Project createProjectFromErrors(final List<Error> errors) {
        return Project.create(null, -1, null, null, false, null, null, errors);
    }

    public static ProjectPermissionsPage createProjectPermissionsPageFromErrors(final List<Error> errors) {
        return ProjectPermissionsPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static PullRequestSettings createPullRequestSettingsFromErrors(final List<Error> errors) {
        return PullRequestSettings.create(null, null, null, null, null, null, errors);
    }

    public static ProjectPage createProjectPageFromErrors(final List<Error> errors) {
        return ProjectPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static PullRequest createPullRequestFromErrors(final List<Error> errors) {
        return PullRequest.create(-1, -1, null, null, null,
                false, false, null, 0, 0, null,
                null, false, null, null, null,
                null, null, errors);
    }

    public static SyncState createSyncStateFromErrors(final List<Error> errors) {
        return SyncState.create(null, null, null, null, errors);
    }

    public static SyncStatus createSyncStatusFromErrors(final boolean isAvailable, final List<Error> errors) {
        return SyncStatus.create(isAvailable, false, null, null, null, null, errors);
    }

    public static ActivitiesPage createActivitiesPageFromErrors(final List<Error> errors) {
        return ActivitiesPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static ParticipantsPage createParticipantsPageFromErrors(final List<Error> errors) {
        return ParticipantsPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static Participants createParticipantsFromErrors(final List<Error> errors) {
        return Participants.create(null, null, Role.REVIEWER, false, Status.UNAPPROVED, errors);
    }

    public static PullRequestPage createPullRequestPageFromErrors(final List<Error> errors) {
        return PullRequestPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static TagPage createTagPageFromErrors(final List<Error> errors) {
        return TagPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static MergeStatus createMergeStatusFromErrors(final List<Error> errors) {
        return MergeStatus.create(false, false, null, errors);
    }

    public static WebHookPage createWebHookPageFromErrors(final List<Error> errors) {
        return WebHookPage.create(-1, -1, -1, -1, true, null, errors);
    }

    public static List<Error> createPostWebHookListFromErrors(final List<Error> errors) {
        return errors;
    }

    public static WebHook createWebHookFromErrors(final List<Error> errors) {
        return WebHook.create(null, null, -1, -1, null , null, null, false, errors);
    }

    public static PostWebHook createPostWebHookFromErrors(final List<Error> errors) {
        return PostWebHook.create(false, false, null, null,
            false, false, false, false, false, false,
            false, false,false, false ,false, null,
            null, errors);
    }

    public static AccessKey createAccessKeyFromErrors(final List<Error> errors) {
        return AccessKey.create(null, null, null, null, errors);
    }

    public static AccessKeyPage createAccessKeyPageFromErrors(final List<Error> errors) {
        return AccessKeyPage.create(0, 0, 0, 0, false, null, errors);
    }

    public static LabelsPage createLabelsPageFromErrors(final List<Error> errors) {
        return LabelsPage.create(0, 0, 0, 0, false, null, errors);
    }

    public static Label createLabelByNameFromErrors(final List<Error> errors) {
        return Label.create("", errors);
    }

    /**
     * Parse list of Error's from output.
     *
     * @param output json containing errors hash
     * @return List of Error's or empty list if none could be found
     */
    public static List<Error> getErrors(final String output) {

        final List<Error> errors = Lists.newArrayList();

        try {

            final JsonElement element = BitbucketUtils.JSON_PARSER.parse(output);
            final JsonObject object = element.getAsJsonObject();

            // in most cases Bitbucket will hand us back a list of `Error` objects
            // but in other cases we will simply be handed back the singular
            // `Error` object (i.e. not as a list).
            if (object.has("errors")) {
                final JsonArray errorsArray = object.get("errors").getAsJsonArray();
                final Iterator<JsonElement> it = errorsArray.iterator();
                while (it.hasNext()) {
                    final JsonObject obj = it.next().getAsJsonObject();
                    errors.add(getErrorFromJsonObject(obj));
                }
            } else if (object.has("context")) {
                errors.add(getErrorFromJsonObject(object));
            } else {
                throw new RuntimeException(output);
            }
        } catch (final Exception e) {
            final Error error = Error.create(output,
                    "Failed to parse output: message=" + e.getMessage(),
                    e.getClass().getName(),
                    false,
                    null);
            errors.add(error);
        }

        return errors;
    }

    /**
     * Parse a single Error object from a given JsonObject.
     *
     * @param obj the JsonObject to parse an Error from.
     * @return Error object derived from parsing the passed JsonObject.
     */
    public static Error getErrorFromJsonObject(final JsonObject obj) {
        final JsonElement context = obj.get("context");
        final JsonElement message = obj.get("message");
        final JsonElement exceptionName = obj.get("exceptionName");
        final JsonElement conflicted = obj.get("conflicted");

        final List<Veto> vetos = Lists.newArrayList();
        final JsonElement possibleVetoesArray = obj.get("vetoes");
        if (isNotJsonNull(possibleVetoesArray)) {

            final JsonArray vetoesArray = possibleVetoesArray.getAsJsonArray();
            final Iterator<JsonElement> vetoIterator = vetoesArray.iterator();
            while (vetoIterator.hasNext()) {
                final JsonObject vetoObj = vetoIterator.next().getAsJsonObject();
                final JsonElement summary = vetoObj.get("summaryMessage");
                final JsonElement detailed = vetoObj.get("detailedMessage");
                final Veto veto = Veto.create(isNotJsonNull(summary) ? summary.getAsString() : null,
                        isNotJsonNull(detailed) ? detailed.getAsString() : null);
                vetos.add(veto);
            }
        }

        return Error.create(isNotJsonNull(context) ? context.getAsString() : null,
                isNotJsonNull(message) ? message.getAsString() : null,
                isNotJsonNull(exceptionName) ? exceptionName.getAsString() : null,
                isNotJsonNull(conflicted) ? conflicted.getAsBoolean() : false, //NOPMD
                vetos);
    }

    private static boolean isNotJsonNull(final JsonElement jsonElement) {
        return jsonElement != null && !jsonElement.isJsonNull();
    }

}
