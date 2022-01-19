package adapters;

import domain.TokenBusinessLogic;
import domain.ports.IStorageAdapter;
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

    public void handleCreateCustomerWithToken(Event event) {
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
