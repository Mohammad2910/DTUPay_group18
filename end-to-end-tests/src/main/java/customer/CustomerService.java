package customer;

import domain.CustomerAccount;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class CustomerService {

    // build the client and the target
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://fm-18.compute.dtu.dk:8080/customer");

    /**
     * Add customer DTUPay account
     *
     * @param name
     * @param cpr
     * @param bankAccount
     */
    public String add(String name, String cpr, String bankAccount) {
        CustomerAccount account = new CustomerAccount("", name, cpr, bankAccount);

        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON));

        switch (response.getStatus()) {
            case 200:
                return "Successfully created customer with ID: " + response.readEntity(CustomerAccount.class).getId();
            case 404:
                return response.readEntity(String.class);
            case 500:
                return "Internal server error";
            default:
                return "Failed due to unknown error";
        }
    }
}
