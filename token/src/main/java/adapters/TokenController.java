package adapters;

import domain.TokenBusinessLogic;
import domain.model.DTUPayAccount;
import domain.model.PaymentPayload;
import domain.model.TokenPayload;
import domain.model.TokenSet;
import domain.ports.IStorageAdapter;
import exceptions.*;
import messaging.Event;
import messaging.MessageQueue;

/**
 * Class for handling external communication via RabbitMQ
 *
 * @Author Christian
 */
public class TokenController {
    MessageQueue queue;
    TokenBusinessLogic tokenBusinessLogic;

    /**
     * Constructor for adding the handlers for the message queue
     *
     * @param queue          - The message queue for handling the communication
     * @param storageAdapter - The adapter for the external storage communication
     */
    public TokenController(MessageQueue queue, IStorageAdapter storageAdapter) {
        tokenBusinessLogic = new TokenBusinessLogic(storageAdapter);
        this.queue = queue;
        queue.addHandler("CreateCustomerWithTokens", this::handleCreateCustomerWithTokens); // Account Microservice
        queue.addHandler("CustomerRequestTokens", this::handleCustomerRequestsTokens);  // Facade Microservice
        queue.addHandler("ValidateCustomerToken", this::handleValidateCustomerToken);  // Payment Microservice
        queue.addHandler("RetrieveCustomerTokens", this::handleRetrieveCustomerTokens);  // Facade Microservice
    }

    /**
     * Method for handling creation of a new customer with a new set of tokens
     *
     * @param event - The event for communicating whether the customer has been created or not
     */
    public void handleCreateCustomerWithTokens(Event event) {
        String requestId = event.getArgument(0, String.class);
        try {
            String errorMessage = event.getArgument(2, String.class);
            if (errorMessage != null) {
                this.publishPropagatedError("CustomerWithTokensCreateFailed", requestId, errorMessage);

                // Exit
                return;
            }

            try {
                DTUPayAccount account = event.getArgument(1, DTUPayAccount.class);
                tokenBusinessLogic.createNewCustomer(account.getId());
                Event customerCreated = new Event("CustomerWithTokensCreated", new Object[]{requestId, account, null});
                queue.publish(customerCreated);
            } catch (CustomerAlreadyExistsException customerAlreadyExistsException) {
                Event customerAlreadyExists = new Event("CustomerWithTokensCreateFailed", new Object[]{requestId, null, customerAlreadyExistsException.getMessage()});
                queue.publish(customerAlreadyExists);
            }
        } catch (Exception e) {
            this.publishPropagatedError("CustomerWithTokensCreateFailed", requestId, e.getMessage());
        }
    }

    /**
     * Method for handling the customers request of a new set of tokens
     *
     * @param event - The event for communicating whether new tokens has been supplied or not
     */
    public void handleCustomerRequestsTokens(Event event) {
        String requestId = event.getArgument(0, String.class);
        try {
            String errorMessage = event.getArgument(2, String.class);
            if (errorMessage != null) {
                this.publishPropagatedError("CustomerTokensRequestFailed", requestId, errorMessage);

                // Exit
                return;
            }

            try {
                TokenPayload tokenPayload = event.getArgument(1, TokenPayload.class);
                tokenBusinessLogic.supplyTokens(tokenPayload.getCid(), tokenPayload.getTokenAmount());

                // Get newly created tokens for customer
                String[] tokenSet = tokenBusinessLogic.getTokens(tokenPayload.getCid());

                // Set tokens to payload
                tokenPayload.setTokens(tokenSet);
                Event tokenSupplied = new Event("CustomerTokensRequested", new Object[]{requestId, tokenPayload, null});
                queue.publish(tokenSupplied);
            } catch (TokensEnoughException | TokenOutOfBoundsException | TokensNotEnoughException tokensException) {
                Event customerHasSufficientTokens = new Event("CustomerTokensRequestFailed", new Object[]{requestId, null, tokensException.getMessage()});
                queue.publish(customerHasSufficientTokens);
            }
        } catch (Exception e) {
            this.publishPropagatedError("CustomerTokensRequestFailed", requestId, e.getMessage());
        }
    }

    /**
     * Method for handling the validation of the customer's token
     *
     * @param event - The event for communicating whether the token is valid or not
     */
    public void handleValidateCustomerToken(Event event) {
        String requestId = event.getArgument(0, String.class);
        try {
            String errorMessage = event.getArgument(2, String.class);
            if (errorMessage != null) {
                this.publishPropagatedError("CustomerTokenValidateFailed", requestId, errorMessage);

                // Exit
                return;
            }

            try {
                PaymentPayload paymentPayload = event.getArgument(1, PaymentPayload.class);
                String cid = tokenBusinessLogic.validateCustomerFromToken(paymentPayload.getToken());
                paymentPayload.setCustomerId(cid);
                Event tokenValidated = new Event("CustomerTokenValidated", new Object[]{requestId, paymentPayload, null});
                queue.publish(tokenValidated);
            } catch (TokenNotValidException tokenException) {
                Event tokenNotValid = new Event("CustomerTokenValidateFailed", new Object[]{requestId, null, tokenException.getMessage()});
                queue.publish(tokenNotValid);
            }
        } catch (Exception e) {
            this.publishPropagatedError("CustomerTokenValidateFailed", requestId, e.getMessage());
        }
    }

    /**
     * Method for handling the get of a customers tokens
     *
     * @param event - The event for communicating whether the token is retrieved or not
     */
    public void handleRetrieveCustomerTokens(Event event){
        String requestId = event.getArgument(0, String.class);
        try {
            String errorMessage = event.getArgument(2, String.class);
            if (errorMessage != null) {
                this.publishPropagatedError("CustomerTokenRetrievedFailed", requestId, errorMessage);

                // Exit
                return;
            }

            try {
                TokenPayload tokenPayload = event.getArgument(1, TokenPayload.class);
                String[] tokens = tokenBusinessLogic.getTokens(tokenPayload.getCid());
                tokenPayload.setTokens(tokens);
                Event tokenRetrieved = new Event("CustomerTokenRetrieved", new Object[]{requestId, tokenPayload, null});
                queue.publish(tokenRetrieved);
            } catch (TokensNotEnoughException tokenException) {
                Event tokenNotRetrieved = new Event("CustomerTokenRetrievedFailed", new Object[]{requestId, tokenException.getMessage()});
                queue.publish(tokenNotRetrieved);
            }
        } catch (Exception e) {
            this.publishPropagatedError("CustomerTokenRetrievedFailed", requestId, e.getMessage());
        }
    }

    /**
     * Method for propagating error messages sent by the consumed events.
     *
     * @param eventName    - The name of the event that was consumed
     * @param requestId    - The id of the event
     * @param errorMessage - the message to propagate
     */
    private void publishPropagatedError(String eventName, String requestId, String errorMessage) {
        // Publish event
        Event errorPropagated = new Event(eventName, new Object[]{requestId, null, errorMessage});
        queue.publish(errorPropagated);
    }
}
