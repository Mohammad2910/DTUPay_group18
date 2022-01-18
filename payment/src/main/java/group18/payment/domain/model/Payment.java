package group18.payment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    String customerBankAccount;
    String merchantBankAccount;
    String amount;
    String requestId;
}
