package port;

import domain.model.Payment;
import java.util.List;

public interface StorageInterface {

    void addPayment(Payment payment);

    List<Payment> getPayments();

    void cleanUpPayments();

}
