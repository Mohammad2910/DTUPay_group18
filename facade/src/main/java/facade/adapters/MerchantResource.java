package facade.adapters;

import javax.ws.rs.*;

import facade.domain.AccountList;
import facade.domain.DTUPayAccount;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/merchant")
public class MerchantResource {
    FacadeController facadeController = new FacadeControllerFactory().getService();

    private AccountList accountList = AccountList.getInstance();

    /*
    //todo: should implemented better so it works for all resources
    @GET
    public void startUpTest(){
        new StartUp().startUp();
    }*/

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DTUPayAccount createAccount(DTUPayAccount account){
        System.out.println("Account is created !!!!!!");
        return facadeController.publishCreateMerchant(account);
    }

    // todo: should this be here?
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<DTUPayAccount> getlist(){
        return accountList.getAccountList();
    }
}
