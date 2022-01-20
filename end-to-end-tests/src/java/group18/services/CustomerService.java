package group18.services;

import group18.domain.DTUPayAccount;
import group18.domain.Payment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class CustomerService {

    // build the client and the target
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://localhost:8080/customer");

    /**
     * Add customer DTUPay account
     *
     * @param name
     * @param cpr
     * @param bankAccount
     */
    public void add(String name, String cpr, String bankAccount) {
        DTUPayAccount account = new DTUPayAccount("", name, cpr, bankAccount);

        // actually no response at current stage
        Response response  = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON));
    }
}
