package services;

import dtu.ws.fastmoney.*;
import group18.domain.DTUPayAccount;
import customer.CustomerService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

public class CustomerSteps {
    BankService dtuBank = new BankServiceService().getBankServicePort();
    DTUPayAccount account = new DTUPayAccount();
    CustomerService service = new CustomerService();

    @When("A customer wants to register to DTU Pay with name {string}")
    public void weRegisterCustomerWithName(String name) {
        account.setName(name);
    }

    @And("cpr {string}")
    public void cpr(String cpr) {
        account.setCpr(cpr);
    }

    @And("a DTUBank account")
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

    @Then("It is added on the account list")
    public void itIsAddedOnTheAccountList() {
        service.add(account.getName(),account.getCpr(), account.getDtuBankAccount());
    }

    @And("Cleanup")
    public void tearDown() {
        System.out.println("Running: tearDown");
        //BankService dtuBank = new BankServiceService().getBankServicePort();
        try {
            List<AccountInfo> list = dtuBank.getAccounts();
            for (AccountInfo a : list) {
//                System.out.println(a.getAccountId());
//                System.out.println(a.getUser().getCprNumber());
//                System.out.println(a.getUser().getFirstName());
//                System.out.println(a.getUser().getLastName());
                if ((a.getUser().getCprNumber().equals("c-cpr")) || (a.getUser().getCprNumber().equals("m-cpr"))) {
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
