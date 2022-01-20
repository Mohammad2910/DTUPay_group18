package services;

import group18.domain.Payment;
import group18.services.PaymentService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.ArrayList;

public class PaymentSteps {

    PaymentService paymentService = new PaymentService();
    String token, mid, amount;
    
    @When("We add a payment with token {string}")
    public void weAddAPaymentWithToken(String token) {
        this.token = token;
    }

    @And("merchant id {string}")
    public void merchantId(String mid) {
        this.mid = mid;
    }

    @And("amount {string} kr")
    public void amountKr(String amount) {
        this.amount = amount;
    }

    @Then("It is added on the payment list")
    public void itIsAddedOnThePaymentList() {
        try {
            // TODO: Add a clean up for the payments created by the tests
            // Add
            paymentService.add(token, mid, amount);

            // Get the list to see if it is added
            ArrayList<Payment> payments = paymentService.getList();
            Assert.assertNotNull(payments);

            // Assert payment attributes
            for (Payment p : payments) {
                Assert.assertEquals(this.token, p.getToken());
                Assert.assertEquals(this.mid, p.getMid());
                Assert.assertEquals(this.amount, p.getAmount());
            }
        } catch (Exception e) {
            // For debugging
            System.out.println(e.getMessage());
        }
    }
}

