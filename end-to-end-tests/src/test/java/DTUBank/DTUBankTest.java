package DTUBank;

import dtu.ws.fastmoney.AccountInfo;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;

class DTUBankTest {
//    IBankWrapper bank = new BankWrapper(new BankServiceService());

//    InMemoryStorage memory = InMemoryStorage.instance();
//    PaymentRegistration service = new PaymentRegistration(bank);

//    @Test
//    public void createPaymentTest () {
//        BankService dtuBank = new BankServiceService().getBankServicePort();
//
//        // ===== Setup bank accounts ===== //
//        // Init customer
//        User customer = new User();
//        customer.setCprNumber("c-cpr");
//        customer.setFirstName("c-fn");
//        customer.setLastName("c-ln");
//
//        // Init merchant
//        User merchant = new User();
//        merchant.setCprNumber("m-cpr");
//        merchant.setFirstName("m-fn");
//        merchant.setLastName("m-ln");
//        String amount = "1000";
//        String customerAccountIdentifier = "";
//        String merchantAccountIdentifier = "";
//        try {
//            //Getting customer with balance
//            BigDecimal bigBalance = new BigDecimal(amount);
//            customerAccountIdentifier = dtuBank.createAccountWithBalance(customer, bigBalance);
//            merchantAccountIdentifier = dtuBank.createAccountWithBalance(merchant, bigBalance);
//
//        } catch (BankServiceException_Exception bsException) {
//            bsException.printStackTrace();
//        }
//
//        //===== Register merchant and customer =====//
//        // Register customer
//        String token = "token";
//        Customer customerModel = new Customer();
//        customerModel.setCustomerId("cid");
//        customerModel.setName("Nemo");
//        customerModel.setBankAccountId(customerAccountIdentifier);
//        memory.addCustomerToStorage(token, customerModel);
//
//        // Register merchant
//        Merchant merchantModel = new Merchant();
//        merchantModel.setMerchantId("mid");
//        merchantModel.setName("Joe");
//        merchantModel.setBankAccountId(merchantAccountIdentifier);
//        memory.addMerchantToStorage(merchantModel);
//
//        //===== Set token to customer =====//
//        // Invoke createPayment
//        Payment newPayment = new Payment();
//        newPayment.setToken(token);
//        newPayment.setMerchantId(merchantModel.getMerchantId());
//        newPayment.setAmount(amount);
//        try {
//            service.createPayment(newPayment);
//        } catch (Exception e) {
//            // Print error
//            System.out.println(e.getMessage());
//        }
//
//       // Assert
//        assertEquals(1, memory.getPayments().size());
//    }

    /**
     * Runs automatically after all test inside its class
     *
     * Tears down all mock bank accounts created
     */
//    @AfterAll
//    public static void tearDown() {
//        System.out.println("Running: tearDown");
//        BankService dtuBank = new BankServiceService().getBankServicePort();
//        try {
//            List<AccountInfo> list = dtuBank.getAccounts();
//            for (AccountInfo a : list) {
////                System.out.println(a.getAccountId());
////                System.out.println(a.getUser().getCprNumber());
////                System.out.println(a.getUser().getFirstName());
////                System.out.println(a.getUser().getLastName());
//                if ((a.getUser().getCprNumber().equals("c-cpr")) || (a.getUser().getCprNumber().equals("m-cpr"))) {
//                    dtuBank.retireAccount(a.getAccountId());
//                }
//            }
//        } catch (Exception bsException) {
//            System.out.println(bsException.getMessage());
//        }
//    }


    /**
     * Test dtu bank connection
     */
    @Test
    public void dtuBankConnectionTest() {
        BankService dtuBank = new BankServiceService().getBankServicePort();
        List<AccountInfo> list = dtuBank.getAccounts();
        for (AccountInfo a : list) {
            System.out.println(a.getAccountId());
            System.out.println(a.getUser().getCprNumber());
            System.out.println(a.getUser().getFirstName());
            System.out.println(a.getUser().getLastName());
        }
        Assert.assertNotNull(list);
    }
}





