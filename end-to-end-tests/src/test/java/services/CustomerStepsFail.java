package services;

import dtu.ws.fastmoney.*;
import customer.domain.CustomerAccount;
import customer.CustomerService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

public class CustomerStepsFail {
    BankService dtuBank = new BankServiceService().getBankServicePort();
    CustomerAccount account = new CustomerAccount();
    CustomerService service = new CustomerService();

    @When("A custome wants to register to DTU Pay with name {string}")
    public void aCustomerWantsToRegisterToDTUPayWithName(String name) {
        account.setName(name);
    }

    @And("cp {string}")
    public void cpr(String cpr) {
        account.setCpr(cpr);
    }

    @And("a DTUBan account")
    public void aDTUBankAccount() {
        User customer = new User();
        customer.setCprNumber(account.getCpr());
        customer.setFirstName(account.getName());
        customer.setLastName("Mister");

        try {
            // Create customer with balance
            BigDecimal bigBalance = new BigDecimal(10000);
            String customerAccountIdentifier = dtuBank.createAccountWithBalance(customer, bigBalance);
            account.setDtuBankAccount(customerAccountIdentifier);
        } catch (BankServiceException_Exception bsException) {
            bsException.printStackTrace();
        }
    }

    @Then("It is adde on the account list")
    public void itIsAddedOnTheAccountList() {
        service.add(account.getName(),account.getCpr(), account.getDtuBankAccount());
    }

    @And("Cleanu")
    public void tearDown() {
        System.out.println("Running: tearDown");
        //BankService dtuBank = new BankServiceService().getBankServicePort();
        try {
            List<AccountInfo> list = dtuBank.getAccounts();
            for (AccountInfo a : list) {
//                System.out.println(a.getAccountId());
                System.out.println(a.getUser().getCprNumber());
                System.out.println(a.getUser().getFirstName());
//                System.out.println(a.getUser().getLastName());
                if ((a.getUser().getCprNumber().equals("123456-1234"))) {
                    dtuBank.retireAccount(a.getAccountId());
                }
            }
        } catch (Exception bsException) {
            System.out.println(bsException.getMessage());
        }
    }

    @When("A merchant wants to register to DTU Pay with name {string}")
    public void aMerchantWantsToRegisterToDTUPayWithName(String name) {
        account.setName(name);
    }
}
