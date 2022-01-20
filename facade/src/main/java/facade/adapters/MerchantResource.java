package facade.adapters;

import javax.ws.rs.*;
import facade.domain.AccountList;
import facade.domain.DTUPayAccount;
import facade.domain.Payment;
import messaging.implementations.RabbitMqQueue;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import messaging.Event;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Path("/merchant")
public class MerchantResource {
    FacadeController facadeController = new FacadeControllerFactory().getService();

    private AccountList accountList = AccountList.getInstance();


    private final ExecutorService threadPool = Executors.newCachedThreadPool();

/*
    public MerchantResource(FacadeController facadeController) {
        //this.facadeController = facadeController;
    }
*/

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

    /*
        @Author Aidana
     */
    @POST
    @Path("/pay")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void createPayment(Payment payment, final @Suspended AsyncResponse asyncResponse) {
        threadPool.submit(() -> {
            facadeController.publishPaymentRequested(payment)
                    .orTimeout(10, TimeUnit.SECONDS)
                    .whenComplete((result, error) -> {
                        if(error != null) {
                            asyncResponse.resume(Response.status(Response.Status.REQUEST_TIMEOUT.getStatusCode()).header("errMsg", "Sorry, we could not return you a result within 10 seconds").build());
                        } else {
                            if(result == null) {
                                asyncResponse.resume(Response.status(Response.Status.OK).build());
                            } else {
                                asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).header("errMsg", result).build());
                            }
                        }
                    });
        });
    }


}
