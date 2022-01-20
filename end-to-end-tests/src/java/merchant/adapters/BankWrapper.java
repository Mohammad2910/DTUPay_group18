package merchant.adapters;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import merchant.ports.IBankWrapper;

public class BankWrapper implements IBankWrapper {

    private final BankService bank;

    // TODO: Replace class object to the interface
    public BankWrapper(BankServiceService bankService) {
        this.bank = bankService.getBankServicePort();
    }


}
