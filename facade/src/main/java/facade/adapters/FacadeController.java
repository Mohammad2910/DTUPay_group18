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
    Map<String, CompletableFuture<String>> initiatedPayments = new HashMap<>();
    public FacadeController(MessageQueue q) {
        queue = q;
        // todo: make handlers for each event Facade need to look at
        queue.addHandler("PaymentResponseProvided", this::handlePaymentResponseProvided);
        queue.addHandler("MerchantAccountCreated", this::handleMerchantCreated);
        queue.addHandler("CustomerAccountCreated", this::handleCustomerCreated);
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


    public void handleMerchantCreated(Event event) {
        var a = event.getArgument(0, DTUPayAccount.class);
    }
    public void handleCustomerCreated(Event event) {
        var a = event.getArgument(0, DTUPayAccount.class);
    }
}
