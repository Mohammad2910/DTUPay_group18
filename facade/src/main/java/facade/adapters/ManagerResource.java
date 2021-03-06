package facade.adapters;

import facade.domain.ManagerReport;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Path("/manager")
public class ManagerResource {
    FacadeController facadeController = new FacadeControllerFactory().getService();
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * @author Aidana
     */
    @POST
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public void getlist(@Suspended AsyncResponse asyncResponse) {
        threadPool.submit(() -> {
            facadeController.publishPaymentsReportForManagerEvent()
                    .orTimeout(10, TimeUnit.SECONDS)
                    .whenComplete((event, timeoutErr) -> {
                        if (timeoutErr != null) {
                            asyncResponse.resume(Response.status(Response.Status.REQUEST_TIMEOUT.getStatusCode()).header("errMsg", "Sorry, we could not return you a result within 10 seconds").build());
                        } else {
                            // Get error message, if any
                            String error = event.getArgument(2, String.class);
                            if (error == null) {
                                // Set object in response
                                ManagerReport report = event.getArgument(1, ManagerReport.class);
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