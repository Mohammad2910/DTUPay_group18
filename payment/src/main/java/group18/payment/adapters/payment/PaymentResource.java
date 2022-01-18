package group18.payment.adapters.payment;


import group18.payment.adapters.payment.model.PaymentPayload;
import group18.payment.domain.PaymentService;
import group18.payment.domain.model.Payment;
import group18.payment.utilities.messaging.Event;
import group18.payment.utilities.messaging.MessageQueue;

public class PaymentResource {

    private static final String BANK_ACCOUNTS_PROVIDED = "BankAccountsProvided";
    private static final String PAYMENT_REQUESTED = "PaymentRequested";
    private static final String PAYMENT_RESPONSE_PROVIDED = "PaymentResponseProvided";
    private static final String CUSTOMER_ID_REQUESTED = "CustomerIdRequested";

    private final PaymentService paymentService;
    private final MessageQueue queue;

    public PaymentResource(MessageQueue messageQueue, PaymentService paymentService) {
        this.paymentService = paymentService;
        this.queue = messageQueue;
        messageQueue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequestedEvent);
        messageQueue.addHandler(BANK_ACCOUNTS_PROVIDED, this::handleBankAccountsProvidedEvent);
    }

    public void handlePaymentRequestedEvent(Event ev) {
        String requestId = ev.getArgument(0, String.class);
        try {
           PaymentPayload p = ev.getArgument(1, PaymentPayload.class);
           if (p.getMerchantId() == null || p.getToken() == null || p.getAmount() == null) {
               sendErrorResponse(requestId, "parameters can not be null");
               return;
           }
           Event event = new Event(CUSTOMER_ID_REQUESTED, new Object[]{requestId, p});
           queue.publish(event);
        } catch (Exception e) {
           sendErrorResponse(requestId, e.getMessage());
        }
    }

    public void handleBankAccountsProvidedEvent(Event ev) {
        String requestId = ev.getArgument(0, String.class);
        try {
            String error = ev.getArgument(2, String.class);
            if (error != null) {
                sendErrorResponse(requestId, error);
                return;
            }
            PaymentPayload p = ev.getArgument(1, PaymentPayload.class);
            Payment payment = new Payment(p.getMerchantBankAccountId(), p.getCustomerBankAccountId(), p.getAmount(), requestId);
            paymentService.transferMoney(payment);
            Event event = new Event(PAYMENT_RESPONSE_PROVIDED, new Object[]{requestId});
            queue.publish(event);
        } catch (Exception e) {
            sendErrorResponse(requestId, e.getMessage());
        }
    }

    public void sendErrorResponse(String requestId, String errorMessage) {
        Event event = new Event(PAYMENT_RESPONSE_PROVIDED,
                new Object[]{requestId, String.format("Oops! Something went wrong: '%s'", errorMessage)}
                );
        queue.publish(event);
    }


}