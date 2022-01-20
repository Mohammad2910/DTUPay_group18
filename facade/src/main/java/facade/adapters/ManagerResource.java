package facade.adapters;

import facade.domain.Payment;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/manager")
public class ManagerResource {

    @POST
    @Path("report/payments")
    @Produces(MediaType.APPLICATION_JSON)
    public void getlist(@Suspended AsyncResponse asyncResponse) {

    }
}