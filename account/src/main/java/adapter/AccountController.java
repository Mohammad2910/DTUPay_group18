package adapter;

import domain.model.*;
import domain.DTUPayAccountBusinessLogic;
import domain.exception.DuplicateBankAccountException;
import domain.exception.NoSuchAccountException;
import messaging.Event;
import messaging.MessageQueue;
import port.StorageInterface;

public class AccountController {
    MessageQueue queue;
    DTUPayAccountBusinessLogic accountLogic;

    /**
     * Delegate events to handlers
     *
     * @param queue - MessageQueue
     * @param storage - StorageInterface
     */
    public AccountController(MessageQueue queue, StorageInterface storage) {
        this.queue = queue;
        accountLogic = new DTUPayAccountBusinessLogic(storage);

        // Handlers for each event that Account needs to look consume
        // Events published by Facade for Account
        queue.addHandler("CreateCustomerAccount", this::handleCreateCustomerAccountRequest);
        queue.addHandler("CreateMerchantAccount", this::handleCreateMerchantAccountRequest);
        queue.addHandler("DeleteAccount", this::handleDeleteAccountRequest);

        // Events published by Token for Account
        queue.addHandler("CustomerTokenValidated", this::handleCustomerTokenValidatedRequest);
    }

    /**
     * Consumes events of type CreateCustomerAccount and published an event in queue SupplyCustomerWithTokens
     *
     * Consumed event arguments:
     * 1. requestId
     * 2. DTUPayAccount
     * 3. errorMessage
     *
     * Successful event arguments:
     * 1. requestId
     * 2. customerId
     *
     * Failed event arguments:
     * 1. requestId
     * 2. null
     * 3. error message
     *
     * @author s212358
     * @param event - Event
     */
    public void handleCreateCustomerAccountRequest(Event event) {
        // Publish propagated error, if any
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        if (errorMessage != null) {
            this.publishPropagatedError("CustomerAccountCreateFailed", requestId, errorMessage);

            // Exit
            return;
        }

        // Create account
        DTUPayAccount account = event.getArgument(1, DTUPayAccount.class);
        try {
            accountLogic.createAccount(account);
        } catch (DuplicateBankAccountException e) {
            // Publish event with propagated error
            Event accCreationFailed = new Event("CustomerAccountCreateFailed", new Object[] {requestId, null, e.getMessage()});
            queue.publish(accCreationFailed);
        }

        // Publish event for token
        Event tokenAssign = new Event("CreateCustomerWithTokens", new Object[] {requestId, account.getId(), null});
        queue.publish(tokenAssign);

        // Publish response event for facade
        Event accCreationSucceeded = new Event("CustomerAccountCreated", new Object[] {requestId, account, null});
        queue.publish(accCreationSucceeded);
    }

    /**
     *
     * Consumes events of type CreateMerchantAccount and published an event in queue MerchantAccountCreated/MerchantAccountCreateFailed
     *
     * Consumed event arguments:
     * 1. requestId
     * 2. DTUPayAccount
     * 3. errorMessage
     *
     * Successful event arguments:
     * 1. requestId
     * 2. customerId
     *
     * Failed event arguments:
     * 1. requestId
     * 2. null
     * 3. error message
     *
     * @author s212358
     * @param event - Event
     */
    public void handleCreateMerchantAccountRequest(Event event) {
        // Publish propagated error, if any
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        if (errorMessage != null) {
            this.publishPropagatedError("MerchantAccountCreateFailed", requestId, errorMessage);

            // Exit
            return;
        }

        // Create account
        DTUPayAccount account = event.getArgument(1, DTUPayAccount.class);
        try {
            accountLogic.createAccount(account);
        } catch (DuplicateBankAccountException e) {
            // Publish event with propagated error
            Event accCreationFailed = new Event("MerchantAccountCreateFailed", new Object[] {requestId, null, e.getMessage()});
            queue.publish(accCreationFailed);
        }
        // Publish event for the facade
        Event accCreationSucceeded = new Event("MerchantAccountCreated", new Object[] {requestId, account, null});
        queue.publish(accCreationSucceeded);
    }

    /**
     * Consumes events of type DeleteAccount and published an event in queue MerchantAccountDeleted/MerchantAccountDeleteFailed
     *
     * Consumed event arguments:
     * 1. requestId
     * 2. accountId
     * 3. errorMessage
     *
     * Successful event arguments:
     * 1. requestId
     * 2. success message
     *
     * Failed event arguments:
     * 1. requestId
     * 2. null
     * 3. error message
     *
     * @author s184174
     * @param event - Event
     */
    public void handleDeleteAccountRequest(Event event) {
        // Publish propagated error, if any
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        if (errorMessage != null) {
            this.publishPropagatedError("AccountDeleteFailed", requestId, errorMessage);

            // Exit
            return;
        }

        // Delete account
        String accountId = event.getArgument(1, String.class);
        try {
            accountLogic.delete(accountId);
        } catch (NoSuchAccountException e) {
            // Publish response event for facade with propagated error message
            Event accDeleteFailed = new Event("AccountDeleteFailed", new Object[] {requestId, null, e.getMessage()});
            queue.publish(accDeleteFailed);
        }

        // Publish event for facade
        Event accDeleteSucceeded = new Event("AccountDeleted", new Object[] {requestId, "Account with id: " + accountId + " is successfully deleted", null});
        queue.publish(accDeleteSucceeded);
    }

    /**
     * Consumes events of type CustomerTokenValidated and published an event in queue BankAccountsExported/BankAccountsExportFailed
     *
     *  Consumed event arguments:
     * 1. requestId
     * 2. PaymentPayload
     * 3. errorMessage
     *
     * Successful event arguments:
     * 1. requestId
     * 2. PaymentPayload
     *
     * Failed event arguments:
     * 1. requestId
     * 2. null
     * 3. error message
     *
     * @author s184174
     * @param event - Event
     */
    public void handleCustomerTokenValidatedRequest(Event event) {
        // Publish propagated error, if any
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        if (errorMessage != null) {
            this.publishPropagatedError("BankAccountsExportFailed", requestId, errorMessage);

            // Exit
            return;
        }

        // Get account
        PaymentPayload paymentPayload = event.getArgument(1, PaymentPayload.class);
        try {
            // Set customer bank account to payment event
            DTUPayAccount customerAccount = accountLogic.get(paymentPayload.getCustomerId());
            paymentPayload.setCustomerBankAccount(customerAccount.getDtuBankAccount());

            // Set merchant bank account to payment event
            DTUPayAccount merchantAccount = accountLogic.get(paymentPayload.getMerchantId());
            paymentPayload.setMerchantBankAccount(merchantAccount.getDtuBankAccount());
        } catch (NoSuchAccountException e) {
            // Publish response event for the payment microservice
            Event accExtractedFailed = new Event("BankAccountsExportFailed", new Object[] {requestId, null, e.getMessage()});
            queue.publish(accExtractedFailed);
        }

        // Publish payment event for the payment microservice to complete the payment
        Event accExportedSucceeded = new Event("BankAccountsExported", new Object[] {requestId, paymentPayload, null});
        queue.publish(accExportedSucceeded);
    }

    /**
     * It propagates error messages sent by the consumed events.
     *
     * @param eventName - String
     * @param requestId - String
     * @param errorMessage - String
     */
    private void publishPropagatedError(String eventName, String requestId, String errorMessage) {
        // Publish event
        Event errorPropagated = new Event(eventName, new Object[] {requestId, null, errorMessage});
        queue.publish(errorPropagated);
    }
}
