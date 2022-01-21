package domain;

import domain.model.CustomerPayment;
import domain.model.ManagerPayment;
import domain.model.ManagerReport;
import domain.model.MerchantPayment;
import domain.model.Payment;
import port.StorageInterface;
import java.util.ArrayList;
import java.util.List;

public class ReportBusinessLogic {
    private final StorageInterface storage;
    public ReportBusinessLogic(StorageInterface storage) {
        this.storage = storage;
    }

    /**
     * Puts a payment into storage
     *
     * @param payment Payment
     */
    public void addPayment(Payment payment) {
        storage.addPayment(payment);
    }

    /**
     * Retrieves the Manager Report
     * Contains:
     * - Total amount of money
     * - All payments
     *
     * @return ManagerReport
     */
    public ManagerReport getManagerReport() {
        List<Payment> payments = storage.getPayments();
        List<ManagerPayment> managerPayments = new ArrayList<>(payments.size());
        Integer sum = 0;
        for (Payment p: payments) {
           sum += Integer.parseInt(p.getAmount());
           managerPayments.add(new ManagerPayment(p.getToken(), p.getMerchantId(), p.getAmount()));
        }
        return new ManagerReport(managerPayments, sum);
    }

    /**
     * Retrieves the list of Merchant Payments
     *
     * @param merchantId String
     * @return List <MerchantPayment>
     */
    public List<MerchantPayment> getMerchantReport(String merchantId) {
        List<Payment> payments = storage.getPayments();
        List<MerchantPayment> merchantPayments = new ArrayList<>(payments.size());
        for (Payment p: payments) {
            if(p.getMerchantId().equals(merchantId)) {
                merchantPayments.add(new MerchantPayment(p.getToken(), p.getAmount()));
            }
        }
        return merchantPayments;
    }

    /**
     * Retrieves the list of Customer Payments
     *
     * @param customerId String
     * @return List <CustomerPayment>
     */
    public List<CustomerPayment> getCustomerReport(String customerId) {
        List<Payment> payments = storage.getPayments();
        List<CustomerPayment> customerPayments = new ArrayList<>(payments.size());
        for (Payment p: payments) {
            if(p.getCustomerId().equals(customerId)) {
                customerPayments.add(new CustomerPayment(p.getToken(), p.getMerchantId(), p.getAmount()));
            }
        }
        return customerPayments;
    }

    /**
     * Cleans up storage
     */
    public void cleanUpPayments() {
        storage.getPayments().clear();
    }
}
