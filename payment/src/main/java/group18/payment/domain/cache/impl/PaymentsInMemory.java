package group18.payment.domain.cache.impl;


import group18.payment.domain.cache.PaymentsCache;
import group18.payment.domain.model.Payment;

import java.util.ArrayList;
import java.util.List;


public class PaymentsInMemory implements PaymentsCache {

    private static List<Payment> payments = new ArrayList<Payment>();

    private static PaymentsInMemory instance;

    private PaymentsInMemory() {}

    public static PaymentsInMemory instance() {
        if(instance == null) {
            instance = new PaymentsInMemory();
        }
        return instance;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void addPayment(Payment p) {
        payments.add(p);
    }




}


