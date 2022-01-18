package facade;

import facade.adapters.FacadeController;
import facade.adapters.MerchantResource;
import messaging.implementations.RabbitMqQueue;

public class StartUp {

    /*public static void main(String[] args) throws Exception {
        System.out.println("startup");
        var mq = new RabbitMqQueue("localhost");
        FacadeController f = new FacadeController(mq);
        //new CustomerResource(f);
        //new MerchantResource(f);
    }*/

    public void startUp(FacadeController facadeController) {
        System.out.println("startup");
        var mq = new RabbitMqQueue("rabbitmq_container");
        facadeController = new FacadeController(mq);
    }
}
