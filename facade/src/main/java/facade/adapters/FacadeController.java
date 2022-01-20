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
    private CompletableFuture<Event> requestedTokens;
    private CompletableFuture<Event> retrievedToken;

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
        queue.addHandler("CustomerTokensRequested", this::handleCustomerTokenRequested);
        queue.addHandler("CustomerTokensRequestFailed", this::handleCustomerTokenRequestFailed);
        queue.addHandler("RetrievedCustomerToken", this::handleRetrievedCustomerToken);
        queue.addHandler("RetrieveCustomerTokenFailed", this::handleRetrieveCustomerTokenFailed);
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
    //TODO when you do join() you block the Main thread.
    // If something wrong happens with the Account micro (micro is down, rabbitMQ down etc.)
    // you will never release the Main thread and it will be blocked (all the functionality of the Facade works in Main thread) until you restart the app or repair Account
    // so I would suggest to execute it in new thread or better in threadPool
    public Event publishCreateCustomer(DTUPayAccount account) {
        registeredCustomer = new CompletableFuture<>();
        Event createCustomerAccount = new Event("CreateCustomerAccount", new Object[] {1, account, null});
        queue.publish(createCustomerAccount);
        return registeredCustomer.join();
    }

    /**
     * Consumes the successful events for the creation customer account
     *
     * @param event - Event sent by Account
     */
    //TODO Important: should complete this future for specific customer. What if multiple customers use the app, so for which customer future completes here?
    public void handleCustomerCreated(Event event) {
        registeredCustomer.complete(event);
    }

    /**
     * Consumes the failed events for the creation customer account
     *
     * @param event - Event sent by Account
     */
    //TODO Important: should complete this future for specific customer. What if multiple customers use the app, so for which customer future completes here?
    public void handleCustomerCreateFailed(Event event) {
        registeredCustomer.complete(event);
    }

    /**
     * Publishes an event to the CreateMerchantAccount queue for Account
     *
     * @param account - DTUPayAccount sent by merchant post request
     */
    //TODO when you do join() you block the Main thread.
    // If something wrong happens with the Account micro (micro is down, rabbitMQ down etc.)
    // you will never release the Main thread (the app freezes "forever") and it will be blocked (all the functionality of the Facade works in Main thread) until you restart the app or repair Account
    // so I would suggest to execute it in new thread or better in threadPool
    public Event publishCreateMerchant(DTUPayAccount account) {
        registeredMerchant = new CompletableFuture<>();
        Event createMerchantAccount = new Event("CreateMerchantAccount", new Object[] {1, account, null});
        queue.publish(createMerchantAccount);
        return registeredMerchant.join();
    }

    //TODO Important: should complete this future for specific merchant. What if multiple merchants use the app, so for which merchant future completes here?
    public void handleMerchantCreated(Event event) {
        registeredMerchant.complete(event);
    }

    //TODO Important: should complete this future for specific merchant. What if multiple merchants use the app, so for which merchant future completes here?
    public void handleMerchantCreateFailed(Event event) {
        registeredMerchant.complete(event);
    }

    /**
     * Publishes the event for deleting a merchant account, and returns an event
     *
     * @param account - the account we want to delete
     */
    public Event publishDeleteAccount(DTUPayAccount account) {
        registeredMerchant = new CompletableFuture<>();
        Event deleteAccount = new Event("DeleteAccount", new Object[] {1, account, null});
        queue.publish(deleteAccount);
        return registeredMerchant.join();
    }

    /**
     * Consumes the failed events for the creation merchant account
     *
     * @param event - event sent by Account
     */
    public void handleDeleted(Event event) {
        registeredMerchant.complete(event);
    }

    /**
     * Consumes the failed events for the creation merchant account
     *
     * @param event - Event sent by Account
     */
    public void handleDeleteFailed(Event event) {
        registeredMerchant.complete(event);
    }

    /**
     *  Publishes an event on the CustomerRequestTokens queue for Token
     *
     * @param cid - the cid of the customer that requests new tokens
     * @param amount - the amount of tokens the customer requires
     */
    public Event handleCustomerRequestsTokens(String cid, int amount){
        TokenPayload tokenPayload = new TokenPayload(cid, null, amount);
        requestedTokens = new CompletableFuture<>();
        Event requestTokens = new Event("CustomerRequestTokens", new Object[] {1, tokenPayload, null});
        queue.publish(requestTokens);
        return requestedTokens.join();
    }

    /**
     * Consumes the successful events for the request of new tokens
     *
     * @param event - Event sent by Token
     */
    public void handleCustomerTokenRequested(Event event) {
        requestedTokens.complete(event);
    }

    /**
     * Consumes the failed events for the request of new tokens
     *
     * @param event - Event by Token
     */
    public void handleCustomerTokenRequestFailed(Event event) {
        requestedTokens.complete(event);
    }

    /**
     *  Publishes an event on the RetrieveCustomerToken queue for Token
     *
     * @param cid - the cid of the customer we want a token for
     */
    public Event handleRetrieveToken(String cid){
        TokenPayload tokenPayload = new TokenPayload(cid,null,0);
        retrievedToken = new CompletableFuture<>();
        Event retrieveToken = new Event("RetrieveCostumerToken", new Object[]{1, tokenPayload, null});
        queue.publish(retrieveToken);
        return retrievedToken.join();
    }

    /**
     * Consumes the successful events for the retrieval of a customer's token
     *
     * @param event - Event by Token
     */
    public void handleRetrievedCustomerToken(Event event){
        retrievedToken.complete(event);
    }

    /**
     * Consumes the failed events for the retrieval of a customer's token
     * @param event - Event by Token
     */
    public void handleRetrieveCustomerTokenFailed(Event event){
        retrievedToken.complete(event);
    }
}
