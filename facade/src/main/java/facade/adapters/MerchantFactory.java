package facade.adapters;

import messaging.implementations.RabbitMqQueue;

public class MerchantFactory {
    static FacadeController service = null;

    public FacadeController getService(){

        if(service != null) {
            return service;
        }

        var mq = new RabbitMqQueue("rabbitmq__container");
        service = new FacadeController(mq);
        return service;
    }
}