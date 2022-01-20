package adapters;

import domain.TokenBusinessLogic;
import domain.model.PaymentPayload;
import domain.model.TokenPayload;
import domain.ports.IStorageAdapter;
import exceptions.*;
import messaging.Event;
import messaging.MessageQueue;

/**
 * Class for handling external communication via RabbitMQ
 *
 * @Author Christian, Renjue, David
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
        //todo: make handlers for each event
        queue.addHandler("CreateCustomerWithTokens", this::handleCreateCustomerWithTokens); // Account Microservice
        queue.addHandler("CustomerRequestTokens", this::handleCustomerRequestsTokens);  // Facade Microservice
        queue.addHandler("ValidateCustomerToken", this::handleValidateCustomerToken);  // Payment Microservice
        queue.addHandler("ConsumeCustomerToken", this::handleConsumeCustomerToken);  // Validate then directly consume
        queue.addHandler("RetrieveCustomerToken", this::handleRetrieveCustomerToken);  // Facade Microservice

    }

    /**
     * Method for handling creation of a new customer with a new set of tokens
     *
     * @param event - The event for communicating whether the customer has been created or not
     */
    public void handleCreateCustomerWithTokens(Event event) {
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        this.publishPropagatedError("CreateCustomerWithTokens", requestId, errorMessage);

        try {
            tokenBusinessLogic.createNewCustomer(event.getArgument(1, String.class));
            Event customerCreated = new Event("CustomerWithTokensCreated", new Object[]{requestId, "Customer created with 6 tokens!", null});
            queue.publish(customerCreated);
        } catch (CustomerAlreadyExistsException customerAlreadyExistsException) {
            Event customerAlreadyExists = new Event("CustomerWithTokensCreateFailed", new Object[]{requestId, null, customerAlreadyExistsException.getMessage()});
            queue.publish(customerAlreadyExists);
        }
    }

    /**
     * Method for handling the customers request of a new set of tokens
     *
     * @param event - The event for communicating whether new tokens has been supplied or not
     */
    public void handleCustomerRequestsTokens(Event event) {
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        this.publishPropagatedError("CustomerTokensRequestFailed", requestId, errorMessage);

        //todo: argument expects customer id
        try {
            //todo: we need to have cid and token amount in the payload
            TokenPayload tokenPayload = event.getArgument(1, TokenPayload.class);
            tokenBusinessLogic.supplyTokens(tokenPayload.getCid(), tokenPayload.getTokenAmount());
            Event tokenSupplied = new Event("CustomerTokensRequested", new Object[]{requestId, "Customer has been served with " + tokenPayload.getTokenAmount() + "!", null});
            queue.publish(tokenSupplied);
        } catch (TokensEnoughException | TokenOutOfBoundsException tokensException) {
            Event customerHasSufficientTokens = new Event("CustomerTokensRequestFailed", new Object[]{requestId, null, tokensException.getMessage()});
            queue.publish(customerHasSufficientTokens);
        }
    }

    /**
     * Method for handling the validation of the customer's token
     *
     * @param event - The event for communicating whether the token is valid or not
     */
    public void handleValidateCustomerToken(Event event) {
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        this.publishPropagatedError("CustomerTokenValidateFailed", requestId, errorMessage);

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
    }

    /**
     * Method for handling the consumption of the customer's token
     *
     * @param event - The event for communication whether the token has been consumed or not
     */
    public void handleConsumeCustomerToken(Event event) {
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        this.publishPropagatedError("CustomerTokenConsumeFailed", requestId, errorMessage);

        try {
            // todo maybe change the consumeToken in BusinessLogic
            String cid = tokenBusinessLogic.validateCustomerFromToken(event.getArgument(1, String.class));
            String validatedToken = event.getArgument(1, String.class);  // the token has already been validated by ValidateCustomerToken
            tokenBusinessLogic.consumeToken(cid, validatedToken);
            Event tokenConsumed = new Event("CustomerTokenConsumed", new Object[]{requestId, "Token is consumed!", null});
            queue.publish(tokenConsumed);
        } catch (TokenNotValidException tokenException) {
            Event tokenNotConsumed = new Event("CustomerTokenConsumeFailed", new Object[]{requestId, "Token is not valid, and therefore not consumed!"});
            queue.publish(tokenNotConsumed);
        }
    }

    /**
     * Method for handling the get of a customer token
     * @param event - The event for communicating whether the token is retrieved or not
     */
    public void handleRetrieveCustomerToken(Event event){
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        this.publishPropagatedError("RetrieveCustomerTokenFailed", requestId, errorMessage);

        try {
            TokenPayload tokenPayload = event.getArgument(1, TokenPayload.class);
            String token = tokenBusinessLogic.getToken(tokenPayload.getCid());
            tokenPayload.setToken(token);
            Event tokenRetrieved = new Event("RetrievedCustomerToken", new Object[]{requestId, tokenPayload, null});
            queue.publish(tokenRetrieved);
        } catch (TokensNotEnoughException tokenException) {
            Event tokenNotRetrieved = new Event("RetrieveCustomerTokenFailed", new Object[]{requestId, tokenException.getMessage()});
            queue.publish(tokenNotRetrieved);
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
        // Publish propagated error, if any
        if (errorMessage != null) {
            // Publish event
            Event errorPropagated = new Event(eventName, new Object[]{requestId, null, errorMessage});
            queue.publish(errorPropagated);
        }
    }
}
