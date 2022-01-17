package adapters;

import javax.ws.rs.*;

import domain.AccountList;
import domain.DTUPayAccount;
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
