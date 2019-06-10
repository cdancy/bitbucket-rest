package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.domain.support.SupportZip;
import com.cdancy.bitbucket.rest.domain.support.SupportZipDetails;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
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
    SupportZipDetails createSupportZip();

    @Named("support-zip:status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/status/task/{taskId}")
    @GET
    SupportZip getSupportZip(@PathParam("taskId") String taskId);
}
