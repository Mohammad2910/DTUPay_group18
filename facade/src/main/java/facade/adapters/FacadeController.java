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
    private Map<String, CompletableFuture<Event>> registeredAccounts = new HashMap<>();
    private Map<String, CompletableFuture<Event>> deletedAccounts = new HashMap<>();
    private Map<String, CompletableFuture<Event>> requestedTokens = new HashMap<>();

    Map<String, CompletableFuture<String>> initiatedPayments = new HashMap<>();
    public FacadeController(MessageQueue q) {
        queue = q;
        // todo: make handlers for each event Facade need to look at
        queue.addHandler("PaymentResponseProvided", this::handlePaymentResponseProvided);
        queue.addHandler("MerchantAccountCreated", this::handleMerchantCreated);
        queue.addHandler("MerchantAccountCreateFailed", this::handleMerchantCreated);
        queue.addHandler("AccountDeleted", this::handleDeleted);
        queue.addHandler("AccountDeleteFailed", this::handleDeleted);
        queue.addHandler("CustomerAccountCreated", this::handleCustomerCreated);
        queue.addHandler("CustomerAccountCreateFailed", this::handleCustomerCreated);
        queue.addHandler("CustomerTokensRequested", this::handleCustomerTokenRequested);
        queue.addHandler("CustomerTokensRequestFailed", this::handleCustomerTokenRequested);
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
     * Publishes an event to the CreateCustomerAccount queue for Account
     *
     * @param account - DTUPayAccount sent by customer post request
     */
    public CompletableFuture<Event> publishCreateCustomer(DTUPayAccount account) {
        String requestId = UUID.randomUUID().toString();
        registeredAccounts.put(requestId, new CompletableFuture<>());
        Event createCustomerAccount = new Event("CreateCustomerAccount", new Object[] {1, account, null});
        queue.publish(createCustomerAccount);
        return registeredAccounts.get(requestId);
    }

    /**
     * Consumes the events for the creation customer account
     *
     * @param event - Event sent by Account
     */
    //TODO Important: should complete this future for specific customer. What if multiple customers use the app, so for which customer future completes here?
    public void handleCustomerCreated(Event event) {
        String requestId = event.getArgument(0, String.class);
        registeredAccounts.get(requestId).complete(event);
        registeredAccounts.remove(requestId);
    }

    /**
     * Publishes an event to the CreateMerchantAccount queue for Account
     *
     * @param account - DTUPayAccount sent by merchant post request
     */
    public CompletableFuture<Event> publishCreateMerchant(DTUPayAccount account) {
        String requestId = UUID.randomUUID().toString();
        registeredAccounts.put(requestId, new CompletableFuture<>());
        Event createMerchantAccount = new Event("CreateMerchantAccount", new Object[] {1, account, null});
        queue.publish(createMerchantAccount);
        return registeredAccounts.get(requestId);
    }

    public void handleMerchantCreated(Event event) {
        String requestId = event.getArgument(0, String.class);
        registeredAccounts.get(requestId).complete(event);
        registeredAccounts.remove(requestId);
    }

    /**
     * Publishes the event for deleting a merchant account, and returns an event
     *
     * @param account - the account we want to delete
     */
    public CompletableFuture<Event> publishDeleteAccount(DTUPayAccount account) {
        String requestId = UUID.randomUUID().toString();
        deletedAccounts.put(requestId, new CompletableFuture<>());
        Event deleteAccount = new Event("DeleteAccount", new Object[] {requestId, account, null});
        queue.publish(deleteAccount);
        return deletedAccounts.get(requestId);
    }

    /**
     * Consumes the events for the deleting merchant account
     *
     * @param event - event sent by Account
     */
    public void handleDeleted(Event event) {
        String requestId = event.getArgument(0, String.class);
        deletedAccounts.get(requestId).complete(event);
        deletedAccounts.remove(requestId);
    }


    public CompletableFuture<Event> publishCustomerRequestsTokens(String cid, int amount){
        TokenPayload tokenPayload = new TokenPayload(cid, null, amount);
        String requestId = UUID.randomUUID().toString();
        requestedTokens.put(requestId, new CompletableFuture<>());
        Event requestTokens = new Event("CustomerRequestTokens", new Object[] {1, tokenPayload, null});
        queue.publish(requestTokens);
        return requestedTokens.get(requestId);
    }

    public void handleCustomerTokenRequested(Event event) {
        String requestId = event.getArgument(0, String.class);
        requestedTokens.get(requestId).complete(event);
        requestedTokens.remove(requestId);
    }

}
