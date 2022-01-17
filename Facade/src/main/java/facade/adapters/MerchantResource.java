package facade.adapters;

import javax.ws.rs.*;

import facade.StartUp;
import facade.domain.AccountList;
import facade.domain.DTUPayAccount;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/merchant")
public class MerchantResource {
    //public FacadeController facadeController;

    private AccountList accountList = AccountList.getInstance();
/*
    public MerchantResource(FacadeController facadeController) {
        //this.facadeController = facadeController;
    }
*/
    @GET
    public void startUpTest(){
        new StartUp().startUp();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void createAccount(DTUPayAccount account){
        accountList.addAccount(account);
        //facadeController.publishCreateCustomer(account);
    }
    // todo: should this be here?
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<DTUPayAccount> getlist(){return accountList.getAccountList();}
}
