package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.annotations.Documentation;
import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.cdancy.bitbucket.rest.fallbacks.BitbucketFallbacks;
import com.cdancy.bitbucket.rest.filters.BitbucketAuthentication;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@RequestFilters(BitbucketAuthentication.class)
@Path("/rest/api/{jclouds.api-version}/admin")
public interface AdminApi {

    @Named("admin:list-user-by-group")
    @Documentation({"https://developer.atlassian.com/static/rest/bitbucket-server/4.14.4/bitbucket-rest.html#idm46478323815824"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/groups/more-members")
    @Fallback(BitbucketFallbacks.BranchPageOnError.class)
    @GET
    UserPage listUserByGroup(@QueryParam("context") String context,
                             @Nullable @QueryParam("filter") String filter,
                             @Nullable @QueryParam("start") Integer start,
                             @Nullable @QueryParam("limit") Integer limit);
}
