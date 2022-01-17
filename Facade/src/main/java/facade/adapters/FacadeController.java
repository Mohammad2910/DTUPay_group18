package facade.adapters;

import facade.domain.DTUPayAccount;
import facade.domain.Payment;
import messaging.MessageQueue;
import messaging.Event;

public class FacadeController {

    MessageQueue queue;

    public FacadeController(MessageQueue q) {
        queue = q;
        // todo: make handlers for each event Facade need to look at
        queue.addHandler("PaymentRequested", this::handlePaymentRequest);
        queue.addHandler("Test", this::handlePaymentRequest);
        queue.publish(new Event("Test", new Object[] {}));
    }

    public void handlePaymentRequest(Event event) {
        var p = event.getArgument(0, Payment.class);
        // Todo: this should be implemented such that we send an "checkToken" event
        Event paymentRequest = new Event("InitiatePayment", new Object[] {p});
        queue.publish(paymentRequest);
    }

    /**
     * Publishes an event to the CreateCustomerAccount queue
     *
     * @param account
     */
    public void publishCreateCustomer(DTUPayAccount account) {
        Event createCustomerAccount = new Event("CreateCustomerAccount", new Object[] {account});
        queue.publish(createCustomerAccount);
    }

    /**
     * Publishes an event to the CreateMerchantAccount queue
     *
     * @param account
     */
    public void publishCreateMerchant(DTUPayAccount account) {
        Event createMerchantAccount = new Event("CreateMerchantAccount", new Object[] {account});
        queue.publish(createMerchantAccount);
    }
}
