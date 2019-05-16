package org.aguibert.liberty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/with-passthrough")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonServiceWithPassthrough {

    @Inject
    ExternalRestServiceClient externalService;

    @GET
    @Path("/{personId}")
    public Person getPersonFromExternalService(@PathParam("personId") long id) {
        return externalService.getPerson(id);
    }

}
