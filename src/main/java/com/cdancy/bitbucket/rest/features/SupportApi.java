package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.domain.support.SupportZipTask;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthenticationFilter.class)
@Path("/rest/troubleshooting/latest/support-zip")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface SupportApi {
    @Named("support-zip:create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/local")
    @POST
    SupportZipTask createSupportZip();
}
