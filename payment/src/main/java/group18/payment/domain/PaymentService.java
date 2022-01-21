package group18.payment.domain;

import group18.payment.adapters.bankTransfer.BankTransferService;
import group18.payment.adapters.payment.model.PaymentPayload;
import group18.payment.domain.model.Payment;
import group18.payment.utils.StringUtils;

import java.math.BigDecimal;

/**
 * Class for payment business logic
 *
 * @Author Aidana
 *
 */
public class PaymentService {

    private final BankTransferService bank;

    public PaymentService(BankTransferService bank) {
        this.bank = bank;
    }

    /**
     * This method sends transfer request to the bank
     * @param p - Payment, which has parameters to transfer money
     */
    public void transferMoney(Payment p) throws Exception {
        BigDecimal amountBD = p.getAmount() != null ? new BigDecimal(p.getAmount()) : null;
        if (amountBD != null && amountBD.intValue() <= 0) {
            throw new PaymentValidationException("amount can not be negative or null");
        }
        bank.transferMoneyFromTo(p.getMerchantBankAccount(), p.getCustomerBankAccount(), amountBD);
    }

    public void checkForValidPaymentParameters(PaymentPayload p) throws PaymentValidationException {
        if (StringUtils.AnyNullOrEmpty(p.getMerchantId(), p.getToken(), p.getAmount())) {
            throw new PaymentValidationException("parameters can not be null");
        }
    }



}


