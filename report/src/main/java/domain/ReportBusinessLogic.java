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

    public ManagerReport getManagerReport() {
        List<Payment> payments = storage.getPayments();
        List<ManagerPayment> managerPayments = new ArrayList<>(payments.size());
        Integer sum = 0;
        for (Payment p: payments) {
           sum += Integer.getInteger(p.getAmount());
           managerPayments.add(new ManagerPayment(p.getToken(), p.getMerchantId(), p.getAmount()));
        }
        return new ManagerReport(managerPayments, sum);
    }

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

    public void cleanUpPayments() {
        storage.getPayments().clear();
    }

    public void addPayment(Payment p) {
        storage.addPayment(p);
    }
}
