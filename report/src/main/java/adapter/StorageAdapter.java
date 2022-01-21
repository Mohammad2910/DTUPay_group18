package adapter;

import domain.model.Payment;
import port.StorageInterface;
import storage.Repository;

import java.util.List;

public class StorageAdapter implements StorageInterface {

    private final Repository repository;

    public StorageAdapter(Repository repository) {
        this.repository = repository;
    }


    @Override
    public void addPayment(Payment payment) {
        repository.addPayment(payment);
    }

    @Override
    public List<Payment> getPayments() {
       return repository.getPayments();
    }

    @Override
    public void cleanUpPayments() {
        repository.getPayments().clear();
    }
}
