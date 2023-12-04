package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.annotations.Documentation;
import com.cdancy.bitbucket.rest.domain.commit.CommitPage;
import com.cdancy.bitbucket.rest.domain.pullrequest.ChangePage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface CompareApi {

    @Named("compare:changes")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/7.21.0/bitbucket-rest.html#idp257"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/compare/changes")
    @Fallback(BitbucketFallbacks.ChangePageOnError.class)
    @GET
    ChangePage changes(@PathParam("project") String project,
                       @PathParam("repo") String repo,
                       @Nullable @QueryParam("from") String fromRef,
                       @Nullable @QueryParam("to") String toRef,
                       @Nullable @QueryParam("fromRepo") String fromRepo,
                       @Nullable @QueryParam("start") Integer start,
                       @Nullable @QueryParam("limit") Integer limit);

    @Named("compare:commits")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/7.21.0/bitbucket-rest.html#idp259"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/projects/{project}/repos/{repo}/compare/commits")
    @Fallback(BitbucketFallbacks.CommitPageOnError.class)
    @GET
    CommitPage commits(@PathParam("project") String project,
                       @PathParam("repo") String repo,
                       @Nullable @QueryParam("from") String fromRef,
                       @Nullable @QueryParam("to") String toRef,
                       @Nullable @QueryParam("fromRepo") String fromRepo,
                       @Nullable @QueryParam("start") Integer start,
                       @Nullable @QueryParam("limit") Integer limit);
}
