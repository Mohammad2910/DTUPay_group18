package services;

import api.model.DTUPayAccount;
import api.service.customer.CustomerService;
import dtu.ws.fastmoney.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import java.math.BigDecimal;
import java.util.List;

public class CustomerSteps {
    BankService dtuBank = new BankServiceService().getBankServicePort();
    DTUPayAccount account = new DTUPayAccount();
    CustomerService service = new CustomerService();
    String message;

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
        user.setLastName("group-18");

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
        // 408
        Assertions.assertNotEquals("Request Timeout", message);
        // 500
        Assertions.assertNotEquals("Internal server error", message);
        // default
        Assertions.assertNotEquals("Failed due to unknown error", message);

    }

    @And("Cleanup")
    public void cleanup() {
        //  System.out.println("Running: tearDown");
        // BankService dtuBank = new BankServiceService().getBankServicePort();
        try {
            List<AccountInfo> list = dtuBank.getAccounts();
            for (AccountInfo a : list) {
//                System.out.println(a.getAccountId());
//                System.out.println(a.getUser().getCprNumber());
//                System.out.println(a.getUser().getFirstName());
//                System.out.println(a.getUser().getLastName());
                if ((a.getUser().getLastName().equals("group-18"))) {
                    dtuBank.retireAccount(a.getAccountId());
                }
            }
        } catch (Exception bsException) {
            System.out.println(bsException.getMessage());
        }
    }


    @When("a customer's name is {string}, cpr is {string} and has a DTUBank account")
    public void aCustomerSNameIsCprIsAndHasADTUBankAccount(String name, String cpr) {
        // Create the customer object and to the bank

        account.setName(name);
        account.setCpr(cpr);

        User user = new User();
        user.setCprNumber(account.getCpr());
        user.setFirstName(account.getName());
        user.setLastName("group-18");

        try {
            // Create customer with balance
            BigDecimal bigBalance = new BigDecimal(10000);
            String customerAccountIdentifier = dtuBank.createAccountWithBalance(user, bigBalance);
            account.setDtuBankAccount(customerAccountIdentifier);
        } catch (BankServiceException_Exception bsException) {
            bsException.printStackTrace();
        }
    }

    @And("customer is registered to DTU Pay")
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

    @And("the customer wants to delete their account")
    public void theCustomerWantsDeleteTheirAccount() {
        message = service.delete(account.getId());
    }

    @Then("the customer's account is deleted and gets a response")
    public void theCustomerSAccountIsDeletedAndGetsFollowingMessageAccountGetId() {
        Assertions.assertEquals("Account with id: " + account.getId() + " is successfully deleted",message);
    }

    @When("somethingg")
    public void somethingg() {
        try {
            List<AccountInfo> list = dtuBank.getAccounts();
            for (AccountInfo a : list) {
//                System.out.println(a.getAccountId());
//                System.out.println(a.getUser().getCprNumber());
//                System.out.println(a.getUser().getFirstName());
//                System.out.println(a.getUser().getLastName());
                if ((a.getUser().getLastName().equals("group-18") || (a.getUser().getLastName().equals("Tester")) || (a.getUser().getLastName().equals("Mister")))) {
                    dtuBank.retireAccount(a.getAccountId());
                }
            }
        } catch (Exception bsException) {
            System.out.println(bsException.getMessage());
        }

//        String customerAccountIdentifier ="";
//        account.setName("Customer");
//        account.setCpr("123456-1234");
//
//        User user = new User();
//        user.setCprNumber(account.getCpr());
//        user.setFirstName(account.getName());
//        user.setLastName("Tester");
//        try {
//            // Create customer with balance
//            BigDecimal bigBalance = new BigDecimal(10000);
//            customerAccountIdentifier = dtuBank.createAccountWithBalance(user, bigBalance);
//            account.setDtuBankAccount(customerAccountIdentifier);
//        } catch (BankServiceException_Exception bsException) {
//            bsException.printStackTrace();
//        }
//        System.out.println(customerAccountIdentifier);
    }
}
