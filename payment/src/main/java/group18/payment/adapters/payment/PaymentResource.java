package group18.payment.adapters.payment;


import group18.payment.adapters.payment.model.PaymentPayload;
import group18.payment.domain.PaymentService;
import group18.payment.domain.model.Payment;
import messaging.Event;
import messaging.MessageQueue;

/**
 * Class for handling communication via RabbitMQ
 *
 * @Author Aidana
 */

public class PaymentResource {

    private static final String BANK_ACCOUNTS_EXPORTED = "BankAccountsExported";
    private static final String BANK_ACCOUNTS_EXPORT_FAILED = "BankAccountsExportFailed";
    private static final String PAYMENT_REQUESTED = "PaymentRequested";
    private static final String PAYMENT_RESPONSE_PROVIDED = "PaymentResponseProvided";
    private static final String TOKEN_VALIDATION_REQUESTED = "ValidateCustomerToken";
    private static final String TOKEN_VALIDATE_FAILED = "CustomerTokenValidateFailed";
    private static final String REPORT_FOR_MANAGER_REQUESTED = "PaymentsReportForManagerRequested";
    private static final String REPORT_FOR_MANAGER_PROVIDED = "PaymentsReportForManagerProvided";


    private final PaymentService paymentService;
    private final MessageQueue queue;

    public PaymentResource(MessageQueue messageQueue, PaymentService paymentService) {
        this.paymentService = paymentService;
        this.queue = messageQueue;
        messageQueue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequestedEvent);
        messageQueue.addHandler(BANK_ACCOUNTS_EXPORTED, this::handleBankAccountsProvidedEvent);
        messageQueue.addHandler(BANK_ACCOUNTS_EXPORT_FAILED, this::handleBankAccountsProvidedEvent);
        messageQueue.addHandler(TOKEN_VALIDATE_FAILED, this::handleTokenValidateFailed);

    }


    public void handlePaymentRequestedEvent(Event ev) {
        String requestId = ev.getArgument(0, String.class);
        try {
            PaymentPayload p = ev.getArgument(1, PaymentPayload.class);
            if (p.getMerchantId() == null || p.getToken() == null || p.getAmount() == null) {
                sendErrorResponse(requestId, "parameters can not be null");
                return;
            }
            Event event = new Event(TOKEN_VALIDATION_REQUESTED, new Object[]{requestId, p, null});
            queue.publish(event);
        } catch (Exception e) {
            sendErrorResponse(requestId, e.getMessage());
        }
    }


    public void handleTokenValidateFailed(Event ev) {
        String requestId = ev.getArgument(0, String.class);
        String errorMsg = ev.getArgument(2, String.class);
        sendErrorResponse(requestId, errorMsg);
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
            Payment payment = new Payment(p.getCustomerBankAccount(), p.getMerchantBankAccount(), p.getAmount(), requestId);
            //transfer money
            paymentService.transferMoney(payment);
            Event event = new Event(PAYMENT_RESPONSE_PROVIDED, new Object[]{requestId, "Successful payment!", null});
            queue.publish(event);
        } catch (Exception e) {
            sendErrorResponse(requestId, e.getMessage());
        }
    }

    public void sendErrorResponse(String requestId, String errorMessage) {
        Event event = new Event(PAYMENT_RESPONSE_PROVIDED,
                new Object[]{requestId, null, String.format("Oops! Something went wrong: '%s'", errorMessage)}
                );
        queue.publish(event);
    }


}