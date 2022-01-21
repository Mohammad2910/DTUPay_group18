package domain;

import java.util.List;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import domain.model.*;
import storage.InMemory;
import port.StorageInterface;
import adapter.StorageAdapter;

class ReportBusinessLogicTest {
    InMemory memory = InMemory.instance();
    StorageInterface storage = new StorageAdapter(memory);
    ReportBusinessLogic businessLogic = new ReportBusinessLogic(storage);

    /**
     * Test Add payment
     */
    @Test
    public void testAddPayment() {
        // Add payment to storage
        Payment payment = new Payment("token", "1000", "merchantId", "customerId");
        List<Payment> payments = storage.getPayments();
        assertEquals(payments.size(), 0);
        businessLogic.addPayment(payment);

        // Check payment added in storage
        payments = storage.getPayments();
        assertEquals(payments.size(), 1);
        Payment newPayment = payments.get(0);
        assertEquals(payment, newPayment);
    }

    /**
     * Test Get Manager Report
     */
    @Test
    public void testGetManagerReport() {
        // Add payment to storage
        Payment payment = new Payment("token", "1000", "merchantId", "customerId");
        List<Payment> payments = storage.getPayments();
        assertEquals(payments.size(), 0);
        businessLogic.addPayment(payment);

        // Assert manager payment attributes
        ManagerReport report = businessLogic.getManagerReport();
        assertEquals(payments.size(), 1);
        ManagerPayment reportPayment = report.getPayments().get(0);
        assertEquals(payment.getToken(), reportPayment.getToken());
        assertEquals(payment.getMerchantId(), reportPayment.getMerchantId());
        assertEquals(payment.getAmount(), reportPayment.getAmount());

        // Assert total payment summary
        businessLogic.addPayment(new Payment("token", "1", "merchantId", "customerId"));
        report = businessLogic.getManagerReport();
        int reportTotalPaymentsSummary = report.getSum();
        assertEquals(1001, reportTotalPaymentsSummary);
    }

    /**
     * Test Get Merchant Report
     */
    @Test
    public void testGetMerchantReport() {
        // Add payment to storage
        Payment payment = new Payment("token", "1000", "merchantId", "customerId");
        List<Payment> payments = storage.getPayments();
        assertEquals(payments.size(), 0);
        businessLogic.addPayment(payment);

        // Assert merchant payment attributes
        List<MerchantPayment> report = businessLogic.getMerchantReport(payment.getMerchantId());
        assertEquals(payments.size(), 1);
        MerchantPayment reportPayment = report.get(0);
        assertEquals(payment.getToken(), reportPayment.getToken());
        assertEquals(payment.getAmount(), reportPayment.getAmount());
    }

    /**
     * Test Get Customer Report
     */
    @Test
    public void testGetCustomerReport() {
        // Add payment to storage
        Payment payment = new Payment("token", "1000", "merchantId", "customerId");
        List<Payment> payments = storage.getPayments();
        assertEquals(payments.size(), 0);
        businessLogic.addPayment(payment);

        // Assert customer payment attributes
        List<CustomerPayment> report = businessLogic.getCustomerReport(payment.getCustomerId());
        assertEquals(payments.size(), 1);
        CustomerPayment reportPayment = report.get(0);
        assertEquals(payment.getToken(), reportPayment.getToken());
        assertEquals(payment.getMerchantId(), reportPayment.getMerchantId());
        assertEquals(payment.getAmount(), reportPayment.getAmount());
    }

    /**
     * Cleans up memory for independent test functions
     */
    @AfterEach
    private void tearDown() {
        businessLogic.cleanUpPayments();
    }
}



