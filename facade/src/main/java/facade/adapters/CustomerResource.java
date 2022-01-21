package facade.adapters;

import facade.domain.DTUPayAccount;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Path("/customer")
public class CustomerResource {
    FacadeController facadeController = new FacadeControllerFactory().getService();

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void createAccount(DTUPayAccount account, @Suspended AsyncResponse asyncResponse) {
        threadPool.submit(() -> {
            facadeController.publishCreateCustomer(account)
                    .orTimeout(10, TimeUnit.SECONDS)
                    .whenComplete((event, timeoutErr) -> {
                        if (timeoutErr != null) {
                            asyncResponse.resume(Response.status(Response.Status.REQUEST_TIMEOUT.getStatusCode()).header("errMsg", "Sorry, we could not return you a result within 10 seconds").build());
                        } else {
                            // Get error message, if any
                                String error = event.getArgument(2, String.class);
                                if (error == null) {
                                    DTUPayAccount newAccount = event.getArgument(1, DTUPayAccount.class);
                                    // Set object in response
                                    asyncResponse.resume(Response.ok(newAccount).build());
                                    System.out.println("createAccount.RESUMED with OK");
                                } else {
                                    // Set error in response
                                    asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
                                }
                        }
                    });
        });
    }

    @DELETE
    @Path("/{cid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteAccount(@PathParam("cid") String customerId, final @Suspended AsyncResponse asyncResponse) {
        threadPool.submit(() -> {
            facadeController.publishDeleteAccount(customerId)
                    .orTimeout(10, TimeUnit.SECONDS)
                    .whenComplete((event, timeoutErr) -> {
                        if (timeoutErr != null) {
                            asyncResponse.resume(Response.status(Response.Status.REQUEST_TIMEOUT.getStatusCode()).header("errMsg", "Sorry, we could not return you a result within 10 seconds").build());
                        } else {
                            if (event != null) {
                                // Get error message, if any
                                String error = event.getArgument(2, String.class);
                                if (error == null) {
                                    String successMsg = event.getArgument(1, String.class);
                                    // Set object in response
                                    asyncResponse.resume(Response.ok(successMsg).build());
                                } else {
                                    // Set error in response
                                    asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
                                }
                            }
                        }
                    });
        });
    }

    @POST
    @Path("/token/{cid}/{amount}")
    @Produces(MediaType.APPLICATION_JSON)
    public void requestTokens(@PathParam("cid") String customerId, @PathParam("amount") int amount, @Suspended AsyncResponse asyncResponse){
        threadPool.submit(() -> {
            facadeController.publishCustomerRequestsTokens(customerId, amount)
                    .orTimeout(10, TimeUnit.SECONDS)
                    .whenComplete((event, timeoutErr) -> {
                        if (timeoutErr != null) {
                            asyncResponse.resume(Response.status(Response.Status.REQUEST_TIMEOUT.getStatusCode()).header("errMsg", "Sorry, we could not return you a result within 10 seconds").build());
                        } else {
                            if (event != null) {
                                // Get error message, if any
                                String error = event.getArgument(2, String.class);
                                if (error == null) {
                                    // Return a String[] of Tokens
                                    TokenPayload tokenPayload = event.getArgument(1, TokenPayload.class);

                                    // Set object in response
                                    asyncResponse.resume(Response.ok(tokenPayload.getTokens()).build());
                                } else {
                                    // Set error in response
                                    asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
                                }
                            }
                        }
                    });
        });
    }

    @GET
    @Path("/token/{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public void retrieveTokens(@PathParam("cid") String customerId, @Suspended AsyncResponse asyncResponse){
        threadPool.submit(() -> {
            facadeController.publishRetrieveCustomerTokens(customerId)
                    .orTimeout(10, TimeUnit.SECONDS)
                    .whenComplete((event, timeoutErr) -> {
                        if (timeoutErr != null) {
                            asyncResponse.resume(Response.status(Response.Status.REQUEST_TIMEOUT.getStatusCode()).header("errMsg", "Sorry, we could not return you a result within 10 seconds").build());
                        } else {
                            if (event != null) {
                                // Get error message, if any
                                String error = event.getArgument(2, String.class);
                                if (error == null) {
                                    // Return a String[] of Tokens
                                    TokenPayload tokenPayload = event.getArgument(1, TokenPayload.class);

                                    // Set object in response
                                    asyncResponse.resume(Response.ok(tokenPayload.getTokens()).build());
                                } else {
                                    // Set error in response
                                    asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
                                }
                            }
                        }
                    });
        });
    }


    @POST
    @Path("report/{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public void getlist(@PathParam("cid") String cid, @Suspended AsyncResponse asyncResponse) {
        threadPool.submit(() -> {
            facadeController.publishPaymentsReportForCustomerEvent(cid)
                    .orTimeout(10, TimeUnit.SECONDS)
                    .whenComplete((event, timeoutErr) -> {
                        if (timeoutErr != null) {
                            asyncResponse.resume(Response.status(Response.Status.REQUEST_TIMEOUT.getStatusCode()).header("errMsg", "Sorry, we could not return you a result within 10 seconds").build());
                        } else {
                            // Get error message, if any
                            String error = event.getArgument(2, String.class);
                            if (error == null) {
                                var report = event.getArgument(1, ArrayList.class);
                                // Set object in response
                                asyncResponse.resume(Response.ok(report).build());
                            } else {
                                // Set error in response
                                asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
                            }
                        }
                    });
        });
    }
}
