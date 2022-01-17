package facade.adapters;

import facade.domain.AccountList;
import facade.domain.DTUPayAccount;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/customer")
public class CustomerResource {

    //FacadeController f;
    private AccountList accountList = AccountList.getInstance();

/*
    public CustomerResource(FacadeController f) {
        //this.f = f;
    }*/

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void createAccount(DTUPayAccount account){
        accountList.addAccount(account);
        //f.publishCreateCustomer(account);
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<DTUPayAccount> getlist(){return accountList.getAccountList();}
}
