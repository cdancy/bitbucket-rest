package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.domain.support.SupportZipStatus;
import com.cdancy.bitbucket.rest.domain.support.SupportZipDetails;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthenticationFilter;
import org.jclouds.rest.annotations.RequestFilters;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    SupportZipStatus getSupportZipStatus(@PathParam("taskId") String taskId);
}
