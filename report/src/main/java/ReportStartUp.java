//import dtu.ws.fastmoney.BankService;
//import dtu.ws.fastmoney.BankServiceService;
//import group18.payment.adapters.bankTransfer.BankTransferService;
//import group18.payment.adapters.bankTransfer.impl.WsBankTransferService;
//import group18.payment.adapters.payment.PaymentResource;
//import group18.payment.domain.PaymentService;
//import group18.payment.domain.cache.PaymentsCache;
//import group18.payment.domain.cache.impl.PaymentsInMemory;
//import messaging.implementations.RabbitMqQueue;

import adapter.ReportController;
import adapter.StorageAdapter;
import domain.model.ReportBusinessLogic;
import messaging.implementations.RabbitMqQueue;
import port.StorageInterface;
import storage.InMemory;

public class ReportStartUp {

    public static void main(String[] args) {
        new ReportStartUp().startUp();
    }
    private void startUp() {
        System.out.println("startup of reports microservice");
        var mq = new RabbitMqQueue("rabbitmq_container");
        InMemory memory = InMemory.instance();
        StorageInterface storage = new StorageAdapter(memory);
        ReportBusinessLogic reportBusinessLogic = new ReportBusinessLogic(storage);
        new ReportController(mq, reportBusinessLogic);
    }
}
