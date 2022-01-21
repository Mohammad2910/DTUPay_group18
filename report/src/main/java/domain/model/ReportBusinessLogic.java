package domain.model;

import adapter.CustomerPayment;
import adapter.ManagerPayment;
import adapter.ManagerReport;
import adapter.MerchantPayment;
import port.StorageInterface;


import java.util.ArrayList;
import java.util.List;

public class ReportBusinessLogic {

    private final StorageInterface storage;

    public ReportBusinessLogic(StorageInterface storage) {
        this.storage = storage;
    }

    public ManagerReport getManagerReport() {
        List<Payment> payments = storage.getPayments();
        List<ManagerPayment> managerPayments = new ArrayList<>(payments.size());
        Integer sum = 0;
        for (Payment p: payments) {
           sum += Integer.getInteger(p.amount);
           managerPayments.add(new ManagerPayment(p.token, p.merchantId, p.amount));
        }
        return new ManagerReport(managerPayments, sum);
    }

    public List<MerchantPayment> getMerchantReport(String merchantId) {
        List<Payment> payments = storage.getPayments();
        List<MerchantPayment> merchantPayments = new ArrayList<>(payments.size());
        for (Payment p: payments) {
            if(p.merchantId.equals(merchantId)) {
                merchantPayments.add(new MerchantPayment(p.token, p.amount));
            }
        }
        return merchantPayments;
    }

    public List<CustomerPayment> getCustomerReport(String customerId) {
        List<Payment> payments = storage.getPayments();
        List<CustomerPayment> customerPayments = new ArrayList<>(payments.size());
        for (Payment p: payments) {
            if(p.customerId.equals(customerId)) {
                customerPayments.add(new CustomerPayment(p.token, p.merchantId, p.amount));
            }
        }
        return customerPayments;
    }

    public void cleanUpPayments() {
        storage.getPayments().clear();
    }

    public void addPayment(Payment p) {
        storage.addPayment(p);
    }
}
