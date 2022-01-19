package facade.adapters;

import javax.ws.rs.*;
import facade.domain.AccountList;
import facade.domain.DTUPayAccount;
import messaging.Event;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/merchant")
public class MerchantResource {
    FacadeController facadeController = new FacadeControllerFactory().getService();
    private AccountList accountList = AccountList.getInstance();


    @POST
//    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(DTUPayAccount account){
        // Get event
        Event event = facadeController.publishCreateMerchant(account);

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
        //return facadeController.publishCreateMerchant(account);
    }

    @DELETE
    //@Path
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

}
