package group18.payment;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import group18.payment.adapters.bankTransfer.BankTransferService;
import group18.payment.adapters.bankTransfer.impl.WsBankTransferService;
import group18.payment.adapters.payment.PaymentResource;
import group18.payment.domain.PaymentService;
import group18.payment.domain.cache.PaymentsCache;
import group18.payment.domain.cache.impl.PaymentsInMemory;
import group18.payment.utilities.messaging.MessageQueue;
import group18.payment.utilities.messaging.impl.RabbitMqQueue;

public class StartUp {

    public static void main(String[] args) {
        new StartUp().startUp();
    }

    private void startUp() {
        PaymentsCache cache = PaymentsInMemory.instance();
        BankService bankService = new BankServiceService().getBankServicePort();
        BankTransferService bank = new WsBankTransferService(bankService);

        PaymentService paymentService = new PaymentService(cache, bank);
        MessageQueue messageQueue = new RabbitMqQueue();

        new PaymentResource(messageQueue, paymentService);
    }
}
