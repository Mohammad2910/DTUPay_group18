package facade.adapters;

import javax.ws.rs.*;

import facade.StartUp;
import facade.domain.AccountList;
import facade.domain.DTUPayAccount;
import facade.domain.Payment;
import messaging.implementations.RabbitMqQueue;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Path("/merchant")
public class MerchantResource {

    public FacadeController facadeController = new FacadeController(new RabbitMqQueue("rabbitmq_container"));

    private AccountList accountList = AccountList.getInstance();

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

/*
    public MerchantResource(FacadeController facadeController) {
        //this.facadeController = facadeController;
    }
*/
    //todo: should implemented better so it works for all resources
    @GET
    public void startUpTest(){
        new StartUp().startUp(facadeController);
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void createAccount(DTUPayAccount account){
        //accountList.addAccount(account);
        System.out.println(account.getDtuBankAccount());
        facadeController.publishCreateMerchant(account);
    }

    // todo: should this be here?
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<DTUPayAccount> getlist(){
        return accountList.getAccountList();
    }

    @POST
    @Path("/pay")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void createPayment(Payment payment, final @Suspended AsyncResponse asyncResponse) {
        threadPool.submit(() -> {
            var result = facadeController.publishPaymentRequested(payment).join();
            if(result == null) {
                asyncResponse.resume(Response.status(Response.Status.OK).build());
            } else {
                asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).header("errMsg", result).build());
            }
        });
    }


}
