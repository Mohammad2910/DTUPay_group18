package facade.adapters;

import facade.domain.DTUPayAccount;
import facade.domain.Payment;
import messaging.MessageQueue;
import messaging.Event;

import javax.ws.rs.Path;
import java.util.concurrent.CompletableFuture;

public class FacadeController {

    MessageQueue queue;
    private CompletableFuture<Event> registeredMerchant;
    private CompletableFuture<Event> registeredCustomer;

    public FacadeController(MessageQueue q) {
        queue = q;
        // todo: make handlers for each event Facade need to look at
        queue.addHandler("PaymentRequested", this::handlePaymentRequest);
        queue.addHandler("MerchantAccountCreated", this::handleMerchantCreated);
        queue.addHandler("MerchantAccountCreateFailed", this::handleMerchantCreateFailed);
        queue.addHandler("AccountDeleted", this::handleDeleted);
        queue.addHandler("AccountDeleteFailed", this::handleDeleteFailed);
        queue.addHandler("CustomerAccountCreated", this::handleCustomerCreated);
        queue.addHandler("CustomerAccountCreateFailed", this::handleCustomerCreateFailed);
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
    public Event publishCreateCustomer(DTUPayAccount account) {
        registeredCustomer = new CompletableFuture<>();
        Event createCustomerAccount = new Event("CreateCustomerAccount", new Object[] {1, account, null});
        queue.publish(createCustomerAccount);
        return registeredCustomer.join();
    }

    public void handleCustomerCreated(Event event) {
        registeredCustomer.complete(event);
    }

    public void handleCustomerCreateFailed(Event event) {
        registeredCustomer.complete(event);
    }

    /**
     * Publishes an event to the CreateMerchantAccount queue
     *
     * @param account
     */
    public Event publishCreateMerchant(DTUPayAccount account) {
        registeredMerchant = new CompletableFuture<>();
        Event createMerchantAccount = new Event("CreateMerchantAccount", new Object[] {1, account, null});
        queue.publish(createMerchantAccount);
        return registeredMerchant.join();
    }

    public void handleMerchantCreated(Event event) {
        registeredMerchant.complete(event);
    }
    public void handleMerchantCreateFailed(Event event) {
        registeredMerchant.complete(event);
    }


    /**
     * Publishes the event for deleting a merchant account, and returns an event
     * @param account - the account we want to delete
     */
    public Event publishDeleteAccount(DTUPayAccount account) {
        registeredMerchant = new CompletableFuture<>();
        Event deleteAccount = new Event("DeleteAccount", new Object[] {1, account, null});
        queue.publish(deleteAccount);
        return registeredMerchant.join();
    }

    public void handleDeleted(Event event) {
        registeredMerchant.complete(event);
    }
    public void handleDeleteFailed(Event event) {
        registeredMerchant.complete(event);
    }



}
