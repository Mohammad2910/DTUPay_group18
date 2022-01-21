package services;

import api.model.DTUPayAccount;
import api.model.Payment;
import api.service.customer.CustomerService;
import api.service.merchant.MerchantService;
import dtu.ws.fastmoney.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import java.math.BigDecimal;

public class PaymentSteps {
    BankService dtuBank = new BankServiceService().getBankServicePort();
    CustomerService customerService = new CustomerService();
    MerchantService merchantService = new MerchantService();
    DTUPayAccount customerAccount = new DTUPayAccount();
    DTUPayAccount merchantAccount = new DTUPayAccount();
    String message = "";

    @Given("a customer with a bank account with balance {int}")
    public void aCustomerWithABankAccountWithBalance(int balance){
        //Getting customer with balance
        User customer = new User();
        customer.setCprNumber("c-cpr");
        customerAccount.setCpr(customer.getCprNumber());
        customer.setFirstName("c-fn");
        customerAccount.setName(customer.getFirstName());
        customer.setLastName("c-ln");

        try {
            BigDecimal bigBalance = BigDecimal.valueOf(balance);
            String customerAccountIdentifier = dtuBank.createAccountWithBalance(customer, bigBalance);
            customerAccount.setDtuBankAccount(customerAccountIdentifier);
        } catch (BankServiceException_Exception bsException){
            bsException.printStackTrace();
        }

        // Assert account is created
        Assert.assertNotNull(customerAccount.getDtuBankAccount());
    }

    @And("that the customer is registered with DTU Pay")
    public void thatTheCustomerIsRegisteredWithDTUPay() {
        // Invoke customer registration to DTUPay
        String message = customerService.add(customerAccount);

        // Check for error messages
        // 404
        Assertions.assertNotEquals("Account doesn't exists", message);
        // 408
        Assertions.assertNotEquals("Request Timeout", message);
        // 500
        Assertions.assertNotEquals("Internal server error", message);
        // default
        Assertions.assertNotEquals("Failed due to unknown error", message);
    }

    @Given("a merchant with a bank account with balance {int}")
    public void aMerchantWithABankAccountWithBalance(int balance) {
        User merchant = new User();
        merchant.setCprNumber("m-cpr");
        merchantAccount.setCpr(merchant.getCprNumber());
        merchant.setFirstName("m-fn");
        merchantAccount.setName(merchant.getFirstName());
        merchant.setLastName("m-ln");

        try {
            BigDecimal bigBalance = BigDecimal.valueOf(balance);
            String merchantAccountIdentifier = dtuBank.createAccountWithBalance(merchant, bigBalance);
            merchantAccount.setDtuBankAccount(merchantAccountIdentifier);
        } catch (BankServiceException_Exception bsException){
            bsException.printStackTrace();
        }

        // Assert account is created
        Assert.assertNotNull(merchantAccount.getDtuBankAccount());
    }

    @And("that the merchant is registered with DTU Pay")
    public void thatTheMerchantIsRegisteredWithDTUPay() {
        // Invoke merchant registration to DTUPay
        String message = merchantService.add(merchantAccount);

        // Check for error messages
        // 404
        Assertions.assertNotEquals("Account doesn't exists", message);
        // 408
        Assertions.assertNotEquals("Request Timeout", message);
        // 500
        Assertions.assertNotEquals("Internal server error", message);
        // default
        Assertions.assertNotEquals("Failed due to unknown error", message);
    }

    @When("the merchant initiates a payment for {string} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(String amount) {
        // Set payment
        Payment payment = new Payment("", merchantAccount.getId(), amount);

        // Get customer token
        System.out.println("customer id");
        System.out.println(customerAccount.getId());
        String[] tokens = customerService.getTokens(customerAccount.getId());
        System.out.println(tokens != null);
        if (tokens != null) {
            for (String token: tokens) {
                System.out.println("token set");
                System.out.println(token);
                if (token != null) {
                    payment.setToken(token);

                    break;
                }
            }
        }

        System.out.println("selected payment token");
        System.out.println(payment.getToken());

        // Invoke payment
        message = merchantService.createPayment(payment);

        System.out.println("customer");
        System.out.println(customerAccount.getId());
        System.out.println("merchant");
        System.out.println(merchantAccount.getId());
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        // Check for error messages
        // 400
        Assertions.assertNotEquals("Oops! Something went wrong: 'No customer has that token!", message);
        // 408
        Assertions.assertNotEquals("Request Timeout", message);
        // 500
        Assertions.assertNotEquals("Internal server error", message);
        // default
        Assertions.assertNotEquals("Failed due to unknown error", message);
    }

    @And("the balance of the customer at the bank is {int} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(int balance) {
        try {
            BigDecimal bigBalance = BigDecimal.valueOf(balance);
            Account customerDTUBankAccount = dtuBank.getAccount(customerAccount.getDtuBankAccount());
            Assertions.assertEquals(bigBalance, customerDTUBankAccount.getBalance());
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @And("the balance of the merchant at the bank is {int} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(int balance) {
        try {
            BigDecimal bigBalance = BigDecimal.valueOf(balance);
            Account merchantDTUBankAccount = dtuBank.getAccount(merchantAccount.getDtuBankAccount());
            Assertions.assertEquals(bigBalance, merchantDTUBankAccount.getBalance());
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This step is supposed to be called from all scenarios
     * that produce accounts, as their last step.
     */
    @When("cleanup Accounts")
    public void cleanup() {
        // Delete customer account
        try {
            // Remove registration from simple pay
            customerService.delete(customerAccount.getId());

            // Delete from dtu bank
            dtuBank.retireAccount(customerAccount.getDtuBankAccount());
        } catch (BankServiceException_Exception bsException) {
            // Assert that account is deleted
            Assert.assertEquals("Account does not exist", bsException.getMessage());
        }

        // Delete merchant account
        try {
            // Remove registration from simple pay
            merchantService.delete(merchantAccount.getId());

            // Delete from dtu bank
            dtuBank.retireAccount(merchantAccount.getDtuBankAccount());
        } catch (BankServiceException_Exception bsException) {
            // Assert that account is deleted
            Assert.assertEquals("Account does not exist", bsException.getMessage());
        }
    }
}

