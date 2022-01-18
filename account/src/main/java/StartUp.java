import adapters.AccountController;
import domain.storage.InMemory;
import messaging.implementations.RabbitMqQueue;

public class StartUp {
    public static void main(String[] args) {
        new StartUp().startUp();
    }
    private void startUp() {
        System.out.println("startup of account microservice");
        var mq = new RabbitMqQueue("http://fm-18.compute.dtu.dk:8282");
        InMemory memory = InMemory.instance();
        new AccountController(mq, memory);
    }
}
