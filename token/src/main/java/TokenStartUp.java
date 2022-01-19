import adapters.StorageAdapter;
import adapters.TokenController;
import controller.ServiceProvider;
import domain.TokenBusinessLogic;
import messaging.implementations.RabbitMqQueue;
import storage.TokenStorage;


public class TokenStartUp {
    private StorageAdapter storageAdapter;
    public static void main(String[] args) {
        new TokenStartUp().startUp();
    }
    public void startUp(){
        System.out.println("Token startup has been initiated!");
        storageAdapter = new StorageAdapter(new TokenStorage());
        new TokenBusinessLogic(storageAdapter);
        var mq = new RabbitMqQueue("rabbitmq_container");
        new TokenController(mq, storageAdapter);
        //new ServiceProvider();
    }
}
