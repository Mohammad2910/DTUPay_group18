package facade.adapters;

import facade.domain.AccountList;
import facade.domain.DTUPayAccount;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/customer")
public class CustomerResource {

    FacadeController facadeController = new FacadeControllerFactory().getService();

    private AccountList accountList = AccountList.getInstance();

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DTUPayAccount createAccount(DTUPayAccount account){
        return facadeController.publishCreateCustomer(account);
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<DTUPayAccount> getlist(){return accountList.getAccountList();}
}
