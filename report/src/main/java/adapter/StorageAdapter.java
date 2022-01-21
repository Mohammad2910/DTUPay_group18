package adapter;

import domain.model.Payment;
import port.StorageInterface;
import storage.RepositoryInterface;
import java.util.List;

public class StorageAdapter implements StorageInterface {
    private final RepositoryInterface repository;
    public StorageAdapter(RepositoryInterface repository) {
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
