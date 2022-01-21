package domain.model;

import domain.model.ManagerPayment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerReport {
    List<ManagerPayment> payments;
    Integer totalPaymentsSummary;
}
