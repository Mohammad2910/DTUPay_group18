package group18.payment.adapters.payment;


import group18.payment.adapters.payment.model.PaymentPayload;
import group18.payment.adapters.payment.model.ReportPayment;
import group18.payment.domain.PaymentService;
import group18.payment.domain.model.Payment;
import messaging.Event;
import messaging.MessageQueue;

/**
 * Class for handling payment requests from the client
 * and communicating with other microservices via MessageQueue
 *
 * @Author Aidana
 *
 */

public class PaymentResource {

    private static final String BANK_ACCOUNTS_EXPORTED = "BankAccountsExported";
    private static final String BANK_ACCOUNTS_EXPORT_FAILED = "BankAccountsExportFailed";
    private static final String PAYMENT_REQUESTED = "PaymentRequested";
    private static final String PAYMENT_RESPONSE_PROVIDED = "PaymentResponseProvided";
    private static final String TOKEN_VALIDATION_REQUESTED = "ValidateCustomerToken";
    private static final String TOKEN_VALIDATE_FAILED = "CustomerTokenValidateFailed";

    private static final String SAVE_PAYMENT_REQUESTED = "SavePaymentRequested";

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

    /**
     * This method handles events published by merchant Facade to do payments.
     * checks
     * @param ev - event to handle.
     */
    public void handlePaymentRequestedEvent(Event ev) {
        String requestId = ev.getArgument(0, String.class);
        try {
            PaymentPayload p = ev.getArgument(1, PaymentPayload.class);
            paymentService.checkForValidPaymentParameters(p);
            //if parameters to do payment are valid then send event to validate token
            Event event = new Event(TOKEN_VALIDATION_REQUESTED, new Object[]{requestId, p, null});
            queue.publish(event);
        } catch (Exception e) {
            //in case of any exception send response to the merchant facade with error description
            sendErrorResponse(requestId, e.getMessage());
        }
    }

    /**
     * This method handles events from the token micro in case of token validation failed.
     * Token micro puts failed scenarios to separate topic and sends event back to payments
     *
     * otherwise if token validation is successful the token micro sends event further to the account micro
     *
     * @param ev - event to handle.
     */
    public void handleTokenValidateFailed(Event ev) {
        String requestId = ev.getArgument(0, String.class);
        String errorMsg = ev.getArgument(2, String.class);
        sendErrorResponse(requestId, errorMsg);
    }

    /**
     * This method handles events from the account micro
     * at this step the payment is sent to the bank and to the report micro
     *
     * @param ev - event to handle.
     */
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
            //add to the report
            Event savePaymentEvent = new Event(SAVE_PAYMENT_REQUESTED, new Object[]{requestId, new ReportPayment(p.getMerchantId(), p.getToken(), p.getAmount(), p.getCustomerId()) , null});
            publishPaymentToReport(savePaymentEvent);
        } catch (Exception e) {
            //in case of any exception send response to the merchant facade with error description
            sendErrorResponse(requestId, e.getMessage());
        }
    }

    /**
     * This method sends error payment response to the client
     *
     * @param requestId - payment requestId
     * @param errorMessage - message to be shown to the client
     *
     */
    public void sendErrorResponse(String requestId, String errorMessage) {
        Event event = new Event(PAYMENT_RESPONSE_PROVIDED,
                new Object[]{requestId, null, String.format("Oops! Something went wrong: '%s'", errorMessage)}
                );
        queue.publish(event);
    }

    /**
     * This method sends successful payment to the Report micro
     *
     * @param ev - event
     *
     */
    public void publishPaymentToReport(Event ev) {
        try {
            queue.publish(ev);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}