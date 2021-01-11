package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.annotations.Documentation;
import com.cdancy.bitbucket.rest.domain.labels.LabelsPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest")
public interface LabelsApi {

    @Named("labels:list")
    @Documentation({"https://docs.atlassian.com/bitbucket-server/rest/6.0.0/bitbucket-rest.html#idp86"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/{jclouds.api-version}/labels")
    @Fallback(BitbucketFallbacks.LabelsOnError.class)
    @GET
    LabelsPage list(@Nullable @QueryParam("prefix") String prefix);
}
