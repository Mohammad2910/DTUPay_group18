package api.service.customer;

import api.model.DTUPayAccount;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class CustomerService {

    // build the client and the target
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://fm-18.compute.dtu.dk:8080/customer");

    /**
     * Add customer DTUPay account
     *
     * @param account CustomerAccount
     */
    public String add(DTUPayAccount account) {
        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON));

        switch (response.getStatus()) {
            case 200:
                account.setId(response.readEntity(DTUPayAccount.class).getId());
                return "Successfully created customer with ID: " + account.getId();
            case 404:
            case 400:
                return response.readEntity(String.class);
            case 408:
                return "Request Timeout";
            case 500:
                return "Internal server error";
            default:
                return "Failed due to unknown error";
        }
    }

    /**
     * Delete a customer by id
     *
     * @param id String
     * @return String
     */
    public String delete(String id) {
        Response response = target.path(id).request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).delete();

        switch (response.getStatus()) {
            case 200:
            case 202:
            case 404:
            case 400:
                return response.readEntity(String.class);
            case 408:
                return "Request Timeout";
            case 500:
                return "Internal server error";
            default:
                return "Failed due to unknown error";
        }
    }

    /**
     * Get all customer tokens
     *
     * @param id String
     * @return String
     */
    public String[] getTokens(String id) {
        Response response = target.path("token").path(id)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).get();

        System.out.println(response.readEntity(String[].class));
        switch (response.getStatus()) {
            case 200:
            case 202:
            case 400:
                return response.readEntity(String[].class);
            case 408:
            case 500:
            default:
                return null;
        }
    }
}
