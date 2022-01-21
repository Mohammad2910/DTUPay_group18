package storage;

import domain.model.Payment;

import java.util.List;

public interface Repository {
    void addPayment(Payment payment);

    List<Payment> getPayments();

    void cleanUpPayments();


}
