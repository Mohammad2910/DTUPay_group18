package facade.adapters;

import facade.domain.DTUPayAccount;
import facade.domain.Payment;
import facade.domain.PaymentPayload;
import facade.domain.TokenPayload;
import messaging.MessageQueue;
import messaging.Event;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FacadeController {
    MessageQueue queue;
    private Map<String, CompletableFuture<Event>> registeredAccounts = new HashMap<>();
    private Map<String, CompletableFuture<Event>> deletedAccounts = new HashMap<>();
    private Map<String, CompletableFuture<Event>> requestedTokens = new HashMap<>();
    private Map<String, CompletableFuture<Event>> requestedReports = new HashMap<>();
    Map<String, CompletableFuture<Event>> initiatedPayments = new HashMap<>();

    /**
     * @author Maria Eleni
     * @param q
     */
    public FacadeController(MessageQueue q) {
        queue = q;

        // From Payment
        queue.addHandler("PaymentResponseProvided", this::handlePaymentResponseProvided);

        // From Account
        queue.addHandler("MerchantAccountCreated", this::handleMerchantCreated);
        queue.addHandler("MerchantAccountCreateFailed", this::handleMerchantCreated);
        queue.addHandler("AccountDeleted", this::handleDeleted);
        queue.addHandler("AccountDeleteFailed", this::handleDeleted);
        queue.addHandler("CustomerAccountCreated", this::handleCustomerCreated);
        queue.addHandler("CustomerAccountCreateFailed", this::handleCustomerCreated);

        // From Tokens
        queue.addHandler("CustomerTokensRequested", this::handleCustomerTokenRequested);
        queue.addHandler("CustomerTokensRequestFailed", this::handleCustomerTokenRequested);
        queue.addHandler("CustomerTokenRetrieved", this::handleCustomerTokenRetrieved);
        queue.addHandler("CustomerTokenRetrievedFailed", this::handleCustomerTokenRetrieved);
        queue.addHandler("CustomerWithTokensCreated", this::handleCustomerWithTokensCreated);
        queue.addHandler("CustomerWithTokensCreateFailed", this::handleCustomerWithTokensCreated);

        // From Report
        queue.addHandler("ManagerReportProvided", this::handleReportProvided);
        queue.addHandler("MerchantReportProvided", this::handleReportProvided);
        queue.addHandler("CustomerReportProvided", this::handleReportProvided);
    }

    /**
     * Consumes an event for the payment completion
     *
     * @author David
     * @param event - Event
     */
    public void handlePaymentResponseProvided(Event event) {
        var requestId = event.getArgument(0, String.class);
        initiatedPayments.get(requestId).complete(event);
        initiatedPayments.remove(requestId);
    }

    /**
     * Publishes an event to the PaymentRequested queue for the Payment
     *
     * @author Christian
     * @param payment - Payment
     * @return CompletableFuture
     */
    public CompletableFuture<Event> publishPaymentRequested(Payment payment) {
        String requestId = UUID.randomUUID().toString();
        PaymentPayload p = new PaymentPayload(payment.getMid(), payment.getToken(), payment.getAmount());
        Event paymentRequestedEvent = new Event("PaymentRequested", new Object[] {requestId, p});
        initiatedPayments.put(requestId, new CompletableFuture<>());
        queue.publish(paymentRequestedEvent);
        return initiatedPayments.get(requestId);
    }

    /**
     * Publishes an event to the ManagerReportRequested queue for the Report
     *
     * @author Renjue
     * @return CompletableFuture
     */
    public CompletableFuture<Event> publishPaymentsReportForManagerEvent() {
        String requestId = UUID.randomUUID().toString();
        Event event = new Event("ManagerReportRequested", new Object[] {requestId});
        requestedReports.put(requestId, new CompletableFuture<>());
        queue.publish(event);
        return requestedReports.get(requestId);
    }

    /**
     * Publishes an event to the MerchantReportRequested queue for the Report
     *
     * @author Aidana
     * @return CompletableFuture
     */
    public CompletableFuture<Event> publishPaymentsReportForMerchantEvent(String merchantId) {
        String requestId = UUID.randomUUID().toString();
        Event event = new Event("MerchantReportRequested", new Object[] {requestId, merchantId});
        requestedReports.put(requestId, new CompletableFuture<>());
        queue.publish(event);
        return requestedReports.get(requestId);
    }

    /**
     * Publishes an event to the CustomerReportRequested queue for the Report
     *
     * @author Mohammad
     * @return CompletableFuture
     */
    public CompletableFuture<Event> publishPaymentsReportForCustomerEvent(String customerId) {
        String requestId = UUID.randomUUID().toString();
        Event event = new Event("CustomerReportRequested", new Object[] {requestId, customerId});
        requestedReports.put(requestId, new CompletableFuture<>());
        queue.publish(event);
        return requestedReports.get(requestId);
    }

    /**
     * Consumes the events for the report creation
     *
     * @author Maria Eleni
     * @param event - Event sent by Report
     */
    public void handleReportProvided(Event event) {
        String requestId = event.getArgument(0, String.class);
        requestedReports.get(requestId).complete(event);
        requestedReports.remove(requestId);
    }

    /**
     * Publishes an event to the CreateCustomerAccount queue for Account
     *
     * @author David
     * @param account - DTUPayAccount sent by customer post request
     */
    public CompletableFuture<Event> publishCreateCustomer(DTUPayAccount account) {
        String requestId = UUID.randomUUID().toString();
        registeredAccounts.put(requestId, new CompletableFuture<>());
        Event createCustomerAccount = new Event("CreateCustomerAccount", new Object[] {requestId, account, null});
        queue.publish(createCustomerAccount);
        return registeredAccounts.get(requestId);
    }

    /**
     * Consumes the events for the creation customer account
     *
     * @author Christian
     * @param event - Event sent by Account
     */
    public void handleCustomerCreated(Event event) {
        String requestId = event.getArgument(0, String.class);

        // Check if account failed and complete completable
        String error = event.getArgument(2, String.class);
        if (error != null) {
            registeredAccounts.get(requestId).complete(event);
            registeredAccounts.remove(requestId);
        }
    }

    /**
     * Consumes the events for the token supply of the newly created account
     *
     * @author Renjue
     * @param event - Event sent by Token
     */
    public void handleCustomerWithTokensCreated(Event event) {
        String requestId = event.getArgument(0, String.class);
        registeredAccounts.get(requestId).complete(event);
        registeredAccounts.remove(requestId);
    }

    /**
     * Publishes an event to the CreateMerchantAccount queue for Account
     *
     * @author Aidana
     * @param account - DTUPayAccount sent by merchant post request
     */
    public CompletableFuture<Event> publishCreateMerchant(DTUPayAccount account) {
        String requestId = UUID.randomUUID().toString();
        registeredAccounts.put(requestId, new CompletableFuture<>());
        Event createMerchantAccount = new Event("CreateMerchantAccount", new Object[] {requestId, account, null});
        queue.publish(createMerchantAccount);
        return registeredAccounts.get(requestId);
    }

    /**
     * Consumes an event for the creation merchant account
     *
     * @author Mohammad
     * @param event - event by Account
     */
    public void handleMerchantCreated(Event event) {
        String requestId = event.getArgument(0, String.class);
        registeredAccounts.get(requestId).complete(event);
        registeredAccounts.remove(requestId);
    }

    /**
     * Publishes the event for deleting a merchant account, and returns an event
     *
     * @author Maria Eleni
     * @param customerId - the account we want to delete
     */
    public CompletableFuture<Event> publishDeleteAccount(String customerId) {
        String requestId = UUID.randomUUID().toString();
        deletedAccounts.put(requestId, new CompletableFuture<>());
        Event deleteAccount = new Event("DeleteAccount", new Object[] {requestId, customerId, null});
        queue.publish(deleteAccount);
        return deletedAccounts.get(requestId);
    }

    /**
     * Consumes the events for the deleting merchant account
     *
     * @author David
     * @param event - event sent by Account
     */
    public void handleDeleted(Event event) {
        String requestId = event.getArgument(0, String.class);
        deletedAccounts.get(requestId).complete(event);
        deletedAccounts.remove(requestId);
    }

    /**
     *  Publishes an event on the CustomerRequestTokens queue for Token
     *
     * @author Christian
     * @param cid - the cid of the customer that requests new tokens
     * @param amount - the amount of tokens the customer requires
     */
    public CompletableFuture<Event> publishCustomerRequestsTokens(String cid, int amount){
        TokenPayload tokenPayload = new TokenPayload(cid, null, null, amount);
        String requestId = UUID.randomUUID().toString();
        requestedTokens.put(requestId, new CompletableFuture<>());
        Event requestTokens = new Event("CustomerRequestTokens", new Object[] {requestId, tokenPayload, null});
        queue.publish(requestTokens);
        return requestedTokens.get(requestId);
    }

    /**
     * Consumes the successful events for the request of new tokens
     *
     * @author Renjue
     * @param event - Event sent by Token
     */
    public void handleCustomerTokenRequested(Event event) {
        String requestId = event.getArgument(0, String.class);
        requestedTokens.get(requestId).complete(event);
        requestedTokens.remove(requestId);
    }

    /**
     *  Publishes an event on the CustomerRequestTokens queue for Token
     *
     * @author Aidana
     * @param cid - the cid of the customer that requests new tokens
     */
    public CompletableFuture<Event> publishRetrieveCustomerTokens(String cid){
        String requestId = UUID.randomUUID().toString();
        requestedTokens.put(requestId, new CompletableFuture<>());
        TokenPayload tokenPayload = new TokenPayload(cid, null, null, 0);
        Event requestTokens = new Event("RetrieveCustomerTokens", new Object[] {requestId, tokenPayload, null});
        queue.publish(requestTokens);
        return requestedTokens.get(requestId);
    }

    /**
     * Consumes and event for the token retrieval
     *
     * @author Mohammad
     * @param event - Event sent from Token
     */
    public void handleCustomerTokenRetrieved(Event event) {
        String requestId = event.getArgument(0, String.class);
        requestedTokens.get(requestId).complete(event);
        requestedTokens.remove(requestId);
    }
}
