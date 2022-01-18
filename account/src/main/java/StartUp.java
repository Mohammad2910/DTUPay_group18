import adapters.AccountController;
import domain.storage.InMemory;
import messaging.implementations.RabbitMqQueue;

public class StartUp {
// test comment
    public void startUp() {
        System.out.println("startup of account microservice");
        var mq = new RabbitMqQueue("localhost");
        InMemory memory = InMemory.instance();
        new AccountController(mq, memory);
    }
}
