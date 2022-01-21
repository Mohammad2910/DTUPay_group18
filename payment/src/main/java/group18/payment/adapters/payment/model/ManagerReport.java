package group18.payment.adapters.payment.model;

import group18.payment.domain.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerReport {
    List<Payment> payments;
    Integer totalPaymentsSummary;
}
