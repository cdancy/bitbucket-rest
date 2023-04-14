package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.BitbucketApiMetadata;
import com.cdancy.bitbucket.rest.domain.comment.LikePage;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mock tests for the {@link CommentsApi} class.
 */
@Test(groups = "unit", testName = "LikesApiMockTest")
public class LikesApiMockTest extends BaseBitbucketMockTest {

    private final String projectKey = "PRJ";
    private final String repoKey = "my-repo";
    private final String restApiPath = "/rest/comment-likes/";
    private final int pullrequestId = 101;
    private final int pullrequestCommentId = 102;

    public void getGetLikes() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/pull-request-comment-likes.json")).setResponseCode(200));
        try (final BitbucketApi baseApi = api(server.url("/").url())) {

            final LikePage pr = baseApi.likesApi().getLikes(projectKey, repoKey, pullrequestId, pullrequestCommentId);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.values()).hasSize(1);
            assertThat(pr.values().get(0).id()).isEqualTo(103);

            assertSent(server, "GET", restApiPath + BitbucketApiMetadata.API_VERSION
                + "/projects/PRJ/repos/my-repo/pull-requests/101/comments/102/likes");
        } finally {
            server.shutdown();
        }
    }

    public void like() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.url("/").url())) {

            final RequestStatus pr = baseApi.likesApi().likeComment(projectKey, repoKey, pullrequestId, pullrequestCommentId);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.value()).isEqualTo(true);

            assertSent(server, "POST", restApiPath + BitbucketApiMetadata.API_VERSION
                + "/projects/PRJ/repos/my-repo/pull-requests/101/comments/102/likes");
        } finally {
            server.shutdown();
        }
    }

    public void unlike() throws Exception {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setResponseCode(204));
        try (final BitbucketApi baseApi = api(server.url("/").url())) {

            final RequestStatus pr = baseApi.likesApi().unlikeComment(projectKey, repoKey, pullrequestId, pullrequestCommentId);
            assertThat(pr).isNotNull();
            assertThat(pr.errors()).isEmpty();
            assertThat(pr.value()).isEqualTo(true);

            assertSent(server, "DELETE", restApiPath + BitbucketApiMetadata.API_VERSION
                + "/projects/PRJ/repos/my-repo/pull-requests/101/comments/102/likes");
        } finally {
            server.shutdown();
        }
    }
}
