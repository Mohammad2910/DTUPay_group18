import  adapters.AccountController;
import adapters.storage.InMemory;
import messaging.implementations.RabbitMqQueue;
import port.StorageInterface;

public class StartUp {
    public static void main(String[] args) {
        new StartUp().startUp();
    }
    private void startUp() {
        System.out.println("startup of account microservice");
        var mq = new RabbitMqQueue("rabbitmq_container");
        StorageInterface memory = InMemory.instance();
        new AccountController(mq, memory);
    }
}
