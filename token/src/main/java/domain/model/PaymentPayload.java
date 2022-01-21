package domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Christian
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPayload {
    String merchantId, customerId, token, merchantBankAccountId, customerBankAccountId, amount;
}
