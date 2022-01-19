package facade.adapters;

import facade.domain.DTUPayAccount;
import facade.domain.Payment;
import messaging.MessageQueue;
import messaging.Event;

import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FacadeController {

    MessageQueue queue;
    private CompletableFuture<Event> registeredMerchant;
    private CompletableFuture<Event> registeredCustomer;

    Map<String, CompletableFuture<String>> initiatedPayments = new HashMap<>();
    public FacadeController(MessageQueue q) {
        queue = q;
        // todo: make handlers for each event Facade need to look at
        queue.addHandler("PaymentResponseProvided", this::handlePaymentResponseProvided);
        queue.addHandler("MerchantAccountCreated", this::handleMerchantCreated);
        queue.addHandler("MerchantAccountCreateFailed", this::handleMerchantCreateFailed);
        queue.addHandler("AccountDeleted", this::handleDeleted);
        queue.addHandler("AccountDeleteFailed", this::handleDeleteFailed);
        queue.addHandler("CustomerAccountCreated", this::handleCustomerCreated);
        queue.addHandler("CustomerAccountCreateFailed", this::handleCustomerCreateFailed);
    }

    public void handlePaymentResponseProvided(Event event) {
        var result = event.getArgument(1, String.class);
        var requestId = event.getArgument(0, String.class);
        initiatedPayments.get(requestId).complete(result);
        initiatedPayments.remove(requestId);
    }

    public CompletableFuture<String> publishPaymentRequested(Payment payment) {
        String requestId = UUID.randomUUID().toString();
        PaymentPayload p = new PaymentPayload(payment.getMid(), payment.getToken(), payment.getAmount());
        Event paymentRequestedEvent = new Event("PaymentRequested", new Object[] {requestId, p});
        initiatedPayments.put(requestId, new CompletableFuture<>());
        queue.publish(paymentRequestedEvent);
        return initiatedPayments.get(requestId);
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
