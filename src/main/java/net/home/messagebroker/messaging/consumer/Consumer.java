package net.home.messagebroker.messaging.consumer;

import lombok.Data;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
@Data
public class Consumer {

    private final static Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    private final String name;

    public void receiveMessage(String topic, String message) {
        LOGGER.info("Consumer '{}' successfully receives message '{}' from topic '{}'", this, message, topic);
    }
}
