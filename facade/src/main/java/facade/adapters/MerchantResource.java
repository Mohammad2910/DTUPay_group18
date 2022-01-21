package facade.adapters;

import javax.ws.rs.*;
import facade.domain.DTUPayAccount;
import facade.domain.Payment;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Path("/merchant")
public class MerchantResource {
    FacadeController facadeController = new FacadeControllerFactory().getService();
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    /**
     * @author Maria Eleni
     */
    public void createAccount(DTUPayAccount account, final @Suspended AsyncResponse asyncResponse) {
        threadPool.submit(() -> {
            facadeController.publishCreateMerchant(account)
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
                            } else {
                                // Set error in response
                                asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
                            }
                        }
                    });
        });
    }

    @DELETE
    @Path("/{mid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * @author Mohammad
     */
    public void deleteAccount(@PathParam("mid") String merchantId, final @Suspended AsyncResponse asyncResponse) {
        // Get event
        threadPool.submit(() -> {
            facadeController.publishDeleteAccount(merchantId)
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
    @Path("/payment")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    /**
     * @author Aidana
     */
    public void createPayment(Payment payment, final @Suspended AsyncResponse asyncResponse) {
        threadPool.submit(() -> {
            facadeController.publishPaymentRequested(payment)
                    .orTimeout(10, TimeUnit.SECONDS)
                    .whenComplete((event, timeoutErr) -> {
                        if (timeoutErr != null) {
                            asyncResponse.resume(Response.status(Response.Status.REQUEST_TIMEOUT.getStatusCode()).header("errMsg", "Sorry, we could not return you a result within 10 seconds").build());
                        } else {
                            if (event != null) {
                                // Get error message, if any
                                String error = event.getArgument(2, String.class);
                                if (error == null) {
                                    // Set object in response
                                    String successMsg = event.getArgument(1, String.class);
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
    @Path("/report/{mid}")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * @author Maria Eleni
     */
    public void getlist(@PathParam("mid") String mid, @Suspended AsyncResponse asyncResponse) {
        threadPool.submit(() -> {
            facadeController.publishPaymentsReportForMerchantEvent(mid)
                    .orTimeout(10, TimeUnit.SECONDS)
                    .whenComplete((event, timeoutErr) -> {
                        if (timeoutErr != null) {
                            asyncResponse.resume(Response.status(Response.Status.REQUEST_TIMEOUT.getStatusCode()).header("errMsg", "Sorry, we could not return you a result within 10 seconds").build());
                        } else {
                            // Get error message, if any
                            String error = event.getArgument(2, String.class);
                            if (error == null) {
                                // Set object in response
                                var report = event.getArgument(1, ArrayList.class);
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
