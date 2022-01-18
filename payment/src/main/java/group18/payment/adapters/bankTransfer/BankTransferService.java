package group18.payment.adapters.bankTransfer;


import java.math.BigDecimal;

public interface BankTransferService {
    void transferMoneyFromTo(String merchantBankAccount, String customerBankAccount, BigDecimal amount) throws Exception;
}

