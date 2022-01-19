package adapters;

import domain.TokenBusinessLogic;
import domain.ports.IStorageAdapter;
import exceptions.CustomerAlreadyExistsException;
import exceptions.TokenNotValidException;
import exceptions.TokenOutOfBoundsException;
import exceptions.TokensEnoughException;
import messaging.Event;
import messaging.MessageQueue;

/**
 * Class for handling external communication via RabbitMQ
 * @Author Christian, Renjue, David
 */
public class TokenController {
    MessageQueue queue;
    TokenBusinessLogic tokenBusinessLogic;

    /**
     * Constructor for adding the handlers for the message queue
     * @param queue - The message queue for handling the communication
     * @param storageAdapter - The adapter for the external storage communication
     */
    public TokenController(MessageQueue queue, IStorageAdapter storageAdapter) {
        tokenBusinessLogic = new TokenBusinessLogic(storageAdapter);
        this.queue = queue;
        //todo: make handlers for each event
        queue.addHandler("CreateCustomerWithTokens", this::handleCreateCustomerWithTokens);
        queue.addHandler("CustomerRequestTokens", this::handleCustomerRequestsTokens);
        queue.addHandler("ValidateCustomerToken", this::handleValidateCustomerToken);
        queue.addHandler("ConsumeCustomerToken", this::handleConsumeCustomerToken);
    }

    /**
     * Method for handling creation of a new customer with a new set of tokens
     * @param event - The event for communicating whether the customer has been created or not
     */
    public void handleCreateCustomerWithTokens(Event event) {
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        this.publishPropagatedError("CreateCustomerWithTokens", requestId, errorMessage);

        try{
            tokenBusinessLogic.createNewCustomer(event.getArgument(1, String.class));
            Event customerCreated = new Event("CreateCustomerWithTokens", new Object[]{requestId, "Customer created with 6 tokens!"});
            queue.publish(customerCreated);
        } catch (CustomerAlreadyExistsException customerAlreadyExistsException){
            Event customerAlreadyExists = new Event("CreateCustomerWithTokens", new Object[]{requestId, null, customerAlreadyExistsException.getMessage()});
            queue.publish(customerAlreadyExists);
        }
    }


    /**
     * Method for handling the customers request of a new set of tokens
     * @param event - The event for communicating whether new tokens has been supplied or not
     */
    public void handleCustomerRequestsTokens(Event event) {
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        this.publishPropagatedError("CustomerRequestsTokens", requestId, errorMessage);

        //todo: argument expects customer id
        try{
            //todo: we need to have cid and token amount in the payload
            int dummyAmount = 5;
            tokenBusinessLogic.supplyTokens(event.getArgument(1, String.class),dummyAmount);
            Event tokenSupplied = new Event("CustomerRequestsTokens", new Object[]{requestId, "Customer has been served with " + dummyAmount + "!"});
            queue.publish(tokenSupplied);
        } catch (TokensEnoughException | TokenOutOfBoundsException tokensException){
            Event customerHasSufficientTokens = new Event("CustomerRequestsTokens", new Object[]{requestId, null, tokensException.getMessage()});
            queue.publish(customerHasSufficientTokens);
        }
    }

    /**
     * Method for handling the validation of the customer's token
     * @param event - The event for communicating whether the token is valid or not
     */
    public void handleValidateCustomerToken(Event event){
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        this.publishPropagatedError("ValidateCustomerToken", requestId, errorMessage);

        //Todo: Add customerId and token to payload and get those!
        try{
            String dummyToken = "420l33t";
            tokenBusinessLogic.validateToken(event.getArgument(1,String.class),dummyToken);
            Event tokenValidated = new Event("ValidateCustomerToken", new Object[] {requestId, "Token is valid!"});
            queue.publish(tokenValidated);
        } catch (TokenNotValidException tokenException) {
            Event tokenNotValid = new Event("ValidateCustomerToken", new Object[]{requestId,null, tokenException.getMessage()});
            queue.publish(tokenNotValid);
        }
    }

    /**
     * Method for handling the consumption of the customer's token
     * @param event - The event for communication whether the token has been consumed or not
     */
    public void handleConsumeCustomerToken(Event event){
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        this.publishPropagatedError("ConsumeCustomerToken", requestId, errorMessage);

        try{
            String dummyToken = "420l33t";
            tokenBusinessLogic.validateToken(event.getArgument(1,String.class),dummyToken);
            Event tokenConsumed = new Event("ConsumeCustomerToken", new Object[]{requestId, "Token is consumed!"});
            queue.publish(tokenConsumed);
        } catch (TokenNotValidException tokenException) {
            Event tokenNotConsumed = new Event("ConsumeCustomerToken", new Object[]{requestId, "Token is not valid, and therefore not consumed!"});
            queue.publish(tokenNotConsumed);
        }
    }

    /**
     *
     * Method for propagating error messages sent by the consumed events.
     * @param eventName - The name of the event that was consumed
     * @param requestId - The id of the event
     * @param errorMessage - the message to propagate
     */
    private void publishPropagatedError(String eventName, String requestId, String errorMessage) {
        // Publish propagated error, if any
        if (errorMessage == null) {
            // Publish event
            Event errorPropagated = new Event(eventName, new Object[] {requestId, null, errorMessage});
            queue.publish(errorPropagated);
        }
    }
}
