package facade.adapters;

import messaging.implementations.RabbitMqQueue;

/**
 * @author Mohammad
 */
public class FacadeControllerFactory {
    static FacadeController service = null;
    public FacadeController getService(){
        if(service != null) {
            return service;
        }
        var mq = new RabbitMqQueue("rabbitmq_container");
        service = new FacadeController(mq);
        return service;
    }
}
