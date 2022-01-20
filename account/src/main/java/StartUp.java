import adapter.AccountController;
import adapter.StorageAdapter;
import messaging.implementations.RabbitMqQueue;
import storage.InMemory;
import port.StorageInterface;

public class StartUp {
    public static void main(String[] args) {
        new StartUp().startUp();
    }
    private void startUp() {
        System.out.println("startup of account microservice");
        var mq = new RabbitMqQueue("rabbitmq_container");
        InMemory memory = InMemory.instance();
        StorageInterface storage = new StorageAdapter(memory);
        new AccountController(mq, storage);
    }
}
