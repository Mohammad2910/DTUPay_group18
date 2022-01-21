package storage;

import domain.model.Payment;

import java.util.List;

public interface RepositoryInterface {
    void addPayment(Payment payment);

    List<Payment> getPayments();

    void cleanUpPayments();
}
