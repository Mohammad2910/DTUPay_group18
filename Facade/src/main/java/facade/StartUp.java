package facade;

import facade.adapters.FacadeController;
import messaging.implementations.RabbitMqQueue;

public class StartUp {

    /*public static void main(String[] args) throws Exception {
        System.out.println("startup");
        var mq = new RabbitMqQueue("localhost");
        FacadeController f = new FacadeController(mq);
        //new CustomerResource(f);
        //new MerchantResource(f);
    }*/

    public void startUp() {
        System.out.println("startup");
        var mq = new RabbitMqQueue("localhost");
        FacadeController f = new FacadeController(mq);
    }
}
