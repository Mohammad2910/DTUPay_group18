package facade.adapters;

import javax.ws.rs.*;

import facade.StartUp;
import facade.domain.AccountList;
import facade.domain.DTUPayAccount;
import messaging.implementations.RabbitMqQueue;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/merchant")
public class MerchantResource {
    public FacadeController facadeController = new FacadeController(new RabbitMqQueue("rabbitmq_container"));

    private AccountList accountList = AccountList.getInstance();
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
}
