package org.aguibert.liberty;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped // needed bean-defining anno for app servers that only support MP Rest Client 1.0
@RegisterRestClient
@Path("/mock-passthrough")
public interface ExternalRestServiceClient {

    @GET
    @Path("/person/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Person getPerson(@PathParam("personId") long id);

}
