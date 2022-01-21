package group18.payment.adapters.bankTransfer;


import java.math.BigDecimal;


/**
 * This interface is used to hide which bank service is used to transfer money (it can be ws, rest etc.)
 * @Author Aidana
 *
 */

public interface BankTransferService {
    void transferMoneyFromTo(String merchantBankAccount, String customerBankAccount, BigDecimal amount) throws Exception;
}

