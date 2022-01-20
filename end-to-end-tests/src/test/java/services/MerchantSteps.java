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

    String message;

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
        String message = service.add(account);
        // 404
        Assertions.assertNotEquals("An account with given bank account number already exists", message);
        // 500
        Assertions.assertNotEquals("Internal server error", message);
        // default
        Assertions.assertNotEquals("Failed due to unknown error", message);
    }

    @When("a merchant's name is {string}, cpr is {string} and has a DTUBank account")
    public void aCustomerSNameIsCprIsAndHasADTUBankAccount(String name, String cpr) {
        // Create the customer object and to the bank

        account.setName(name);
        account.setCpr(cpr);

        User user = new User();
        user.setCprNumber(account.getCpr());
        user.setFirstName(account.getName());
        user.setLastName("Mister");

        try {
            // Create customer with balance
            BigDecimal bigBalance = new BigDecimal(10000);
            String customerAccountIdentifier = dtuBank.createAccountWithBalance(user, bigBalance);
            account.setDtuBankAccount(customerAccountIdentifier);
        } catch (BankServiceException_Exception bsException) {
            bsException.printStackTrace();
        }
    }

    @And("merchant is registered to DTU Pay")
    public void isRegisteredToDTUPay() {
        // Make sure that we don't get an error message
        String message = service.add(account);
        // 404
        Assertions.assertNotEquals("Account doesn't exists", message);
        // 500
        Assertions.assertNotEquals("Internal server error", message);
        // default
        Assertions.assertNotEquals("Failed due to unknown error", message);
    }

    @And("the merchant wants to delete their account")
    public void theCustomerWantsDeleteTheirAccount() {
        message = service.delete(account.getId());
    }

    @Then("the merchant's account is deleted and gets a response")
    public void theCustomerSAccountIsDeletedAndGetsFollowingMessageAccountGetId() {
        Assertions.assertEquals("Account with id: " + account.getId() + " is successfully deleted",message);
    }
}
