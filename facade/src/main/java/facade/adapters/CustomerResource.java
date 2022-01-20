package facade.adapters;

import facade.domain.DTUPayAccount;
import messaging.Event;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/customer")
public class CustomerResource {
    FacadeController facadeController = new FacadeControllerFactory().getService();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(DTUPayAccount account) {
        // Get event
        Event event = facadeController.publishCreateCustomer(account);

        // Get error message, if any
        String error = event.getArgument(2, String.class);
        if (error == null) {
            DTUPayAccount newAccount = event.getArgument(1, DTUPayAccount.class);
            // Set object in response
            return Response.ok(newAccount).build();
        } else {
            // Set error in response
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(DTUPayAccount account){
        // Get event
        Event event = facadeController.publishDeleteAccount(account);

        // Get error message, if any
        String error = event.getArgument(2, String.class);
        if (error == null) {
            String successMsg = event.getArgument(1, String.class);
            // Set object in response
            return Response.ok(successMsg).build();
        } else {
            // Set error in response
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @GET
    @Path("/token/{cid}/{amount}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestTokens(@PathParam("cid") String customerId, @PathParam("amount") int amount){
        Event event = facadeController.handleCustomerRequestsTokens(customerId, amount);

        // Get error message, if any
        String error = event.getArgument(2, String.class);
        if (error == null) {
            String successMsg = event.getArgument(1, String.class);
            // Set object in response
            return Response.ok(successMsg).build();
        } else {
            // Set error in response
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }


}
