package facade.adapters;

import facade.domain.DTUPayAccount;
import facade.domain.Payment;
import messaging.MessageQueue;
import messaging.Event;

import javax.ws.rs.Path;
import java.util.concurrent.CompletableFuture;

public class FacadeController {

    MessageQueue queue;
    private CompletableFuture<DTUPayAccount> registeredMerchant;
    private CompletableFuture<DTUPayAccount> registeredCustomer;

    public FacadeController(MessageQueue q) {
        queue = q;
        // todo: make handlers for each event Facade need to look at
        queue.addHandler("PaymentRequested", this::handlePaymentRequest);
        queue.addHandler("MerchantAccountCreated", this::handleMerchantCreated);
        queue.addHandler("MerchantAccountCreateFailed", this::handleMerchantCreateFailed);
        queue.addHandler("CustomerAccountCreated", this::handleCustomerCreated);
    }

    public void handlePaymentRequest(Event event) {
        var p = event.getArgument(0, Payment.class);
        // Todo: this should be implemented such that we send an "checkToken" event
        Event paymentRequest = new Event("InitiatePayment", new Object[] {p});
        queue.publish(paymentRequest);
    }

    /**
     * Publishes an event to the CreateCustomerAccount queue
     *
     * @param account
     */
    public void publishCreateCustomer(DTUPayAccount account) {
        Event createCustomerAccount = new Event("CreateCustomerAccount", new Object[] {account});
        queue.publish(createCustomerAccount);
    }

    /**
     * Publishes an event to the CreateMerchantAccount queue
     *
     * @param account
     */
    public void publishCreateMerchant(DTUPayAccount account) {
        registeredMerchant = new CompletableFuture<>();
        Event createMerchantAccount = new Event("CreateMerchantAccount", new Object[] {1, account, null});
        queue.publish(createMerchantAccount);
        //return registeredMerchant.join();
    }

    public void handleMerchantCreated(Event event) {
        String requestId = event.getArgument(0, String.class);
        String merchantId = event.getArgument(1, String.class);
        System.out.println("HELLLOOOO MERCHANT WITH ID " + merchantId);
    }
    public void handleMerchantCreateFailed(Event event) {
        var a = event.getArgument(0, DTUPayAccount.class);
    }
    public void handleCustomerCreated(Event event) {
        var a = event.getArgument(0, DTUPayAccount.class);
    }
}
