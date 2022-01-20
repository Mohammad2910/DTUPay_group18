package customer.adapters;

import customer.ports.IBankWrapper;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;

public class BankWrapper implements IBankWrapper {

    private final BankService bank;

    // TODO: Replace class object to the interface
    public BankWrapper(BankServiceService bankService) {
        this.bank = bankService.getBankServicePort();
    }


}
