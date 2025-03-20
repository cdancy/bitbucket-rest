package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.annotations.Documentation;
import com.cdancy.bitbucket.rest.domain.comment.LikePage;
import com.cdancy.bitbucket.rest.domain.common.RequestStatus;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import com.cdancy.bitbucket.rest.parsers.RequestStatusParser;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import jakarta.inject.Named;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/comment-likes/{jclouds.api-version}/projects")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface LikesApi {

    @Named("comments:getLikes")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/4.0.1/bitbucket-comment-likes-rest.html"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments/{commentId}/likes")
    @Fallback(BitbucketFallbacks.LikePageOnError.class)
    @GET
    LikePage getLikes(@PathParam("project") String project,
                      @PathParam("repo") String repo,
                      @PathParam("pullRequestId") int pullRequestId,
                      @PathParam("commentId") int commentId);

    @Named("comments:likeComment")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/4.0.1/bitbucket-comment-likes-rest.html"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments/{commentId}/likes")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @POST
    RequestStatus likeComment(@PathParam("project") String project,
                              @PathParam("repo") String repo,
                              @PathParam("pullRequestId") int pullRequestId,
                              @PathParam("commentId") int commentId);

    @Named("comments:unlikeComment")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/4.0.1/bitbucket-comment-likes-rest.html"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/pull-requests/{pullRequestId}/comments/{commentId}/likes")
    @Fallback(BitbucketFallbacks.RequestStatusOnError.class)
    @ResponseParser(RequestStatusParser.class)
    @DELETE
    RequestStatus unlikeComment(@PathParam("project") String project,
                              @PathParam("repo") String repo,
                              @PathParam("pullRequestId") int pullRequestId,
                              @PathParam("commentId") int commentId);

}
