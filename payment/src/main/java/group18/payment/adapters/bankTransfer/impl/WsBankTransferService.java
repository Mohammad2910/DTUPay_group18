package group18.payment.adapters.bankTransfer.impl;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import group18.payment.adapters.bankTransfer.BankTransferService;

import java.math.BigDecimal;

public class WsBankTransferService implements BankTransferService {
    private final BankService bankService;

    public WsBankTransferService(BankService bankService) {
        this.bankService = bankService;
    }

    @Override
    public void transferMoneyFromTo(String merchantBankAccount, String customerBankAccount, BigDecimal amount) throws BankServiceException_Exception {
        System.out.println("---------------------------------------");
        System.out.println("transferMoneyFromTo.DEBTOR: " + customerBankAccount);
        System.out.println("transferMoneyFromTo.CREDITOR: " + merchantBankAccount);
        bankService.transferMoneyFromTo(customerBankAccount, merchantBankAccount, amount,"");
    }
}
