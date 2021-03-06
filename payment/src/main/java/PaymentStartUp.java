import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import group18.payment.adapters.bankTransfer.BankTransferService;
import group18.payment.adapters.bankTransfer.impl.WsBankTransferService;
import group18.payment.adapters.payment.PaymentResource;
import group18.payment.domain.PaymentService;
import messaging.implementations.RabbitMqQueue;

public class PaymentStartUp {

    public static void main(String[] args) {
        new PaymentStartUp().startUp();
    }

    private void startUp() {

        BankService bankService = new BankServiceService().getBankServicePort();
        BankTransferService bank = new WsBankTransferService(bankService);

        PaymentService paymentService = new PaymentService(bank);
        var messageQueue = new RabbitMqQueue("rabbitmq_container");

        new PaymentResource(messageQueue, paymentService);
    }
}
