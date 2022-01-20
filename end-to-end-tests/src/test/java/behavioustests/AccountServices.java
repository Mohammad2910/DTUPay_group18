package behavioustests;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

public class AccountServices {

    Client client = ClientBuilder.newClient();
    WebTarget r = client.target("http://localhost:8080/");

    public DTUPayAccount registerCustomer(DTUPayAccount account) {
        var response = r.path("customer").request().post(Entity.json(account), DTUPayAccount.class);
        return response;
    }
}
