package adapters;

import domain.TokenBusinessLogic;
import domain.ports.IStorageAdapter;
import exceptions.TokenNotValidException;
import exceptions.TokenOutOfBoundsException;
import exceptions.TokensEnoughException;
import messaging.Event;
import messaging.MessageQueue;

public class TokenController {
    MessageQueue queue;
    TokenBusinessLogic tokenBusinessLogic;

    public TokenController(MessageQueue queue, IStorageAdapter storageAdapter) {
        tokenBusinessLogic = new TokenBusinessLogic(storageAdapter);
        this.queue = queue;
        //todo: make handlers for each event
        queue.addHandler("CreateCustomerWithTokens", this::handleCreateCustomerWithTokens);
        queue.addHandler("CustomerRequestTokens", this::handleCustomerRequestsTokens);
        queue.addHandler("ValidateCustomerToken", this::handleValidateCustomerToken);
        queue.addHandler("ConsumeCustomerToken", this::handleConsumeCustomerToken);
    }

    public void handleCreateCustomerWithTokens(Event event) {
        String requestId = event.getArgument(0, String.class);
        String errorMessage = event.getArgument(2, String.class);
        this.publishPropagatedError("CreateCustomerWithTokens", requestId, errorMessage);

        //todo: argument expects customer id
        try{
            //todo: Make new exception CustomerAlreadyExistsException
            tokenBusinessLogic.createNewCustomer(event.getArgument(1, String.class));
            Event customerCreated = new Event("CreateCustomerWithTokens", new Object[]{requestId, "Customer created with 6 tokens!"});
            queue.publish(customerCreated);
        } catch (Exception someKindOfExceptionxD){
            Event customerAlreadyExists = new Event("CreateCustomerWithTokens", new Object[]{requestId, null, someKindOfExceptionxD.getMessage()});
            queue.publish(customerAlreadyExists);
        }
    }

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
     * It propagates error messages sent by the consumed events.
     *
     * @param eventName
     * @param requestId
     * @param errorMessage
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
