package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.domain.defaultReviewers.Conditions;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import org.jclouds.rest.annotations.RequestFilters;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
@Path("/rest/default-reviewers/{jclouds.api-version}/projects")
public interface DefaultReviewersApi {

    @Named("defaultReviewers:list-Conditions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{project}/repos/{repo}/conditions/")
    @GET
    List<Conditions> listConditions(@PathParam("project") String project,
                                    @PathParam("repo") String repo);
}
