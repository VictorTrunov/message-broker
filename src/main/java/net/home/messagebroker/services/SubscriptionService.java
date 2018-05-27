package net.home.messagebroker.services;

import net.home.messagebroker.messaging.Topic;
import net.home.messagebroker.messaging.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;

@Service
public class SubscriptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionService.class);

    private final ConcurrentMap<Topic, Set<Consumer>> consumersOfTopic = new ConcurrentHashMap<>();

    public Set<Consumer> getConsumers(Topic topic) {
        return Collections.unmodifiableSet(consumersOfTopic.getOrDefault(requireNonNull(topic), new HashSet<>()));
    }

    public void subscribeConsumer(Topic topic, String consumerName) {
        subscribeConsumer(topic, new Consumer(requireNonNull(consumerName)));
    }

    public void subscribeConsumer(Topic topic, Consumer consumer) {
        Set<Consumer> consumers = consumersOfTopic
                .computeIfAbsent(requireNonNull(topic), t -> ConcurrentHashMap.newKeySet());
        consumers.add(requireNonNull(consumer));
        LOGGER.info("Consumer '{}' was subscribed on topic '{}'", consumer, topic);
    }

    public void unsubscribeConsumer(Topic topic, String consumerName) {
        unsubscribeConsumer(topic, new Consumer(requireNonNull(consumerName)));
    }

    public void unsubscribeConsumer(Topic topic, Consumer consumer) {
        if (consumersOfTopic.containsKey(requireNonNull(topic))) {
            boolean result = consumersOfTopic.get(topic).remove(requireNonNull(consumer));
            LOGGER.info("Consumer with name '{}' was {} from topic '{}'", consumer, result ? "unsubscribed" : "not found", topic);
        } else {
            LOGGER.info("Topic '{}' not found", topic);
        }
    }

}
