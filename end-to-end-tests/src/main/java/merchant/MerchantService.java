package merchant;

import domain.CustomerAccount;
import merchant.domain.MerchantAccount;

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
     * @param account
     */
    public String add(MerchantAccount account) {

        Response response  = target.request(MediaType.APPLICATION_JSON)
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

    public String delete(String id) {
        Response response = target.path(id).request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).delete();

        switch (response.getStatus()) {
            case 200:
            case 404:
                return response.readEntity(String.class);
            case 500:
                return "Internal server error";
            default:
                return "Failed due to unknown error";
        }
    }
}
