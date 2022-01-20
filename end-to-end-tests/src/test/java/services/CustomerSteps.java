package services;

import customer.CustomerService;
import domain.CustomerAccount;
import dtu.ws.fastmoney.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class CustomerSteps {
    BankService dtuBank = new BankServiceService().getBankServicePort();

    CustomerAccount account = new CustomerAccount();
    CustomerService service = new CustomerService();

    @When("A customer wants to register to DTU Pay with name {string}")
    public void aCustomerWantsToRegisterToDTUPayWithName(String name) {
        account.setName(name);
    }

    @And("customer cpr {string}")
    public void cpr(String cpr) {
        account.setCpr(cpr);
    }

    @And("a customer DTUBank account")
    public void aDTUBankAccount() {
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


    @Then("the customer is added on DTUPay")
    public void itIsAddedOnTheAccountList() {
        String message = service.add(account);
        // 404
        Assertions.assertNotEquals("An account with given bank account number already exists", message);
        // 500
        Assertions.assertNotEquals("Internal server error", message);
        // default
        Assertions.assertNotEquals("Failed due to unknown error", message);

    }

    @And("Cleanup")
    public void cleanup() {
        System.out.println("Running: tearDown");
        //BankService dtuBank = new BankServiceService().getBankServicePort();
        try {
            List<AccountInfo> list = dtuBank.getAccounts();
            for (AccountInfo a : list) {
//                System.out.println(a.getAccountId());
//                System.out.println(a.getUser().getCprNumber());
//                System.out.println(a.getUser().getFirstName());
//                System.out.println(a.getUser().getLastName());
                if ((a.getUser().getCprNumber().equals("123456-1234"))) {
                    dtuBank.retireAccount(a.getAccountId());
                }
            }
        } catch (Exception bsException) {
            System.out.println(bsException.getMessage());
        }
    }


    @When("a customer's name is {string}, cpr is {string} and has a DTUBank account")
    public void aCustomerSNameIsCprIsAndHasADTUBankAccount(String name, String cpr) {
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

    @And("is registered to DTU Pay")
    public void isRegisteredToDTUPay() {
        String message = service.add(account);
        // 404
        Assertions.assertNotEquals("An account with given bank account number already exists", message);
        // 500
        Assertions.assertNotEquals("Internal server error", message);
        // default
        Assertions.assertNotEquals("Failed due to unknown error", message);
    }

    @And("the customer wants delete their account")
    public void theCustomerWantsDeleteTheirAccount() {
        String deleteMsg = service.delete(account);
    }

    @Then("the customer's account is deleted and gets following message {string} + account.getId\\() + {string}")
    public void theCustomerSAccountIsDeletedAndGetsFollowingMessageAccountGetId(String arg0, String arg1) {
        //Assertions.assertEquals("Account with id:"+  +" is successfully deleted",arg0 + account.getId() + arg1);
    }
}
