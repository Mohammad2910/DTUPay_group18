package group18.payment.domain.model;

import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    String customerBankAccount;
    String merchantBankAccount;
    String amount;
    String requestId;
}
