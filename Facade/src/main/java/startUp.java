import adapters.FacadeController;
import messaging.implementations.RabbitMqQueue;

public class startUp {
    public static void main(String[] args) throws Exception {
        System.out.println("startup");
        var mq = new RabbitMqQueue("localhost");
        FacadeController f = new FacadeController(mq);
        //new CustomerResource(f);
        //new MerchantResource(f);
    }
}
