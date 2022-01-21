package storage;

import domain.model.Payment;
import java.util.ArrayList;
import java.util.List;

public class InMemory implements RepositoryInterface {
    private final List<Payment> payments = new ArrayList<>();
    private static InMemory instance;

    private InMemory() {}

    public static InMemory instance() {
        if(instance == null) {
            instance = new InMemory();
        }
        return instance;
    }

    @Override
    public void addPayment(Payment payment) {
       payments.add(payment);
    }

    @Override
    public List<Payment> getPayments() {
       return payments;
    }

    @Override
    public void cleanUpPayments() {
        payments.clear();
    }

}


