package group18.payment.domain.cache;


import group18.payment.domain.model.Payment;

import java.util.List;

public interface PaymentsCache {

     List<Payment> getPayments();

     void addPayment(Payment p);
}
