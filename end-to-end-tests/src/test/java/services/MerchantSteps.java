package services;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import merchant.MerchantService;
import merchant.domain.MerchantAccount;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;

public class MerchantSteps {
    BankService dtuBank = new BankServiceService().getBankServicePort();

    MerchantAccount account = new MerchantAccount();
    MerchantService service = new MerchantService();

    @When("A merchant wants to register to DTU Pay with name {string}")
    public void aMerchantWantsToRegisterToDTUPayWithName(String name) {
        account.setName(name);
    }


    @And("merchant cpr {string}")
    public void merchantCpr(String cpr) {
        account.setCpr(cpr);
    }

    @And("a merchant DTUBank account")
    public void aMerchantDTUBankAccount() {
        User user = new User();
        user.setCprNumber(account.getCpr());
        user.setFirstName(account.getName());
        user.setLastName("Mister");

        try {
            // Create customer with balance
            BigDecimal bigBalance = new BigDecimal(10000);
            String merchantAccountIdentifier = dtuBank.createAccountWithBalance(user, bigBalance);
            account.setDtuBankAccount(merchantAccountIdentifier);
        } catch (BankServiceException_Exception bsException) {
            bsException.printStackTrace();
        }
    }

    @Then("the merchant is added on DTU Pay")
    public void theMerchantIsAddedOnDTUPay() {
        String message = service.add(account.getName(),account.getCpr(), account.getDtuBankAccount());
        // 404
        Assertions.assertNotEquals("An account with given bank account number already exists", message);
        // 500
        Assertions.assertNotEquals("Internal server error", message);
        // default
        Assertions.assertNotEquals("Failed due to unknown error", message);
    }
}
