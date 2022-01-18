package group18.payment.utilities.messaging;

import java.util.function.Consumer;

public interface MessageQueue {

	void publish(Event message);
	void addHandler(String eventType, Consumer<Event> handler);

}
