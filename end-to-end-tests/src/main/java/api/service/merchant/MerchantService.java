package api.service.merchant;

import api.model.DTUPayAccount;
import api.model.Payment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MerchantService {
    // build the client and the target
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://fm-18.compute.dtu.dk:8080/merchant");

    /**
     * Add merchant DTUPay account
     *
     * @Author David
     * @param account MerchantAccount
     */
    public String add(DTUPayAccount account) {
        Response response  = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON));

        switch (response.getStatus()) {
            case 200:
                String id = response.readEntity(DTUPayAccount.class).getId();
                account.setId(id);
                return "Successfully created customer with ID: " + id;
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
     * Delete a merchant by id
     *
     * @Author Mohammad
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
     * Create payment via DTUPay
     *
     * @Author Maria Eleni
     * @param payment Payment
     */
    public String createPayment(Payment payment) {
        Response response  = target.path("payment").request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(payment, MediaType.APPLICATION_JSON));

        switch (response.getStatus()) {
            case 200:
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
}
