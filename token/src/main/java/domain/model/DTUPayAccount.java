package domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Christian
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTUPayAccount {
    String id, name, cpr, dtuBankAccount;
}
