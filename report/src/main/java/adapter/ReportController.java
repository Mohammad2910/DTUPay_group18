package adapter;

import domain.ReportBusinessLogic;
import domain.model.Payment;
import messaging.Event;
import messaging.MessageQueue;

public class ReportController {
    MessageQueue queue;
    ReportBusinessLogic reportBusinessLogic;

    // Consumed events
    private static final String SAVE_PAYMENT_REQUESTED = "SavePaymentRequested";
    private static final String MANAGER_REPORT_REQUESTED = "ManagerReportRequested";
    private static final String MERCHANT_REPORT_REQUESTED = "MerchantReportRequested";
    private static final String CUSTOMER_REPORT_REQUESTED = "CustomerReportRequested";

    // Published events
    private static final String MANAGER_REPORT_PROVIDED = "ManagerReportProvided";
    private static final String MERCHANT_REPORT_PROVIDED= "MerchantReportProvided";
    private static final String CUSTOMER_REPORT_PROVIDED = "CustomerReportProvided";

    public ReportController(MessageQueue queue, ReportBusinessLogic reportBusinessLogic) {
        this.queue = queue;
        this.reportBusinessLogic = reportBusinessLogic;
        queue.addHandler(SAVE_PAYMENT_REQUESTED, this::handleSavePaymentRequestedEvent);
        queue.addHandler(MANAGER_REPORT_REQUESTED, this::handleManagerReportRequestedEvent);
        queue.addHandler(MERCHANT_REPORT_REQUESTED, this::handleMerchantReportRequestedEvent);
        queue.addHandler(CUSTOMER_REPORT_REQUESTED, this::handleCustomerReportRequestedEvent);
    }

    public void handleSavePaymentRequestedEvent(Event event) {
        Payment p = event.getArgument(1, Payment.class);
        reportBusinessLogic.addPayment(p);
    }

    public void handleManagerReportRequestedEvent(Event ev) {
        var requestId = ev.getArgument(0, String.class);
        try {
            var report = reportBusinessLogic.getManagerReport();
            Event event = new Event(MANAGER_REPORT_PROVIDED, new Object[]{requestId, report, null});
            queue.publish(event);
        } catch (Exception e) {
            Event event = new Event(MANAGER_REPORT_PROVIDED, new Object[]{requestId, null, e.getMessage()});
            System.out.println(e.getMessage());
            queue.publish(event);
        }
    }

    public void handleMerchantReportRequestedEvent(Event ev) {
        var requestId = ev.getArgument(0, String.class);
        var mid = ev.getArgument(1, String.class);
        try {
            var list = reportBusinessLogic.getMerchantReport(mid);
            Event event = new Event(MERCHANT_REPORT_PROVIDED, new Object[]{requestId, list, null});
            queue.publish(event);
        } catch (Exception e) {
            Event event = new Event(MERCHANT_REPORT_PROVIDED, new Object[]{requestId, null, e.getMessage()});
            queue.publish(event);
        }
    }

    public void handleCustomerReportRequestedEvent(Event ev) {
        var requestId = ev.getArgument(0, String.class);
        var cid = ev.getArgument(1, String.class);
        try {
            var list = reportBusinessLogic.getCustomerReport(cid);
            Event event = new Event(CUSTOMER_REPORT_PROVIDED, new Object[]{requestId, list, null});
            queue.publish(event);
        } catch (Exception e) {
            Event event = new Event(CUSTOMER_REPORT_PROVIDED, new Object[]{requestId, null, e.getMessage()});
            queue.publish(event);
        }
    }








}
