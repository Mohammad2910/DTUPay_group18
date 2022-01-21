import adapter.ReportController;
import adapter.StorageAdapter;
import domain.ReportBusinessLogic;
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
