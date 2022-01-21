import adapters.StorageAdapter;
import adapters.TokenController;
import domain.TokenBusinessLogic;
import messaging.implementations.RabbitMqQueue;
import storage.TokenStorage;

/**
 *
 * @author Christian
 */
public class TokenStartUp {
    public static void main(String[] args) {
        new TokenStartUp().startUp();
    }
    public void startUp(){
        System.out.println("Token startup has been initiated!");
        StorageAdapter storageAdapter = new StorageAdapter(new TokenStorage());
        TokenBusinessLogic tokenBusinessLogic = new TokenBusinessLogic(storageAdapter);
        var mq = new RabbitMqQueue("rabbitmq_container");
        new TokenController(mq, storageAdapter);
    }
}
