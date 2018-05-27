package net.home.messagebroker.api.controllers;

import net.home.messagebroker.messaging.Topic;
import net.home.messagebroker.messaging.consumer.Consumer;
import net.home.messagebroker.services.SubscriptionService;
import net.home.messagebroker.services.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/subscription/{topic}")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private TopicService topicService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionController.class);

    @GetMapping(produces = "application/json")
    public Set<Consumer> getSubscribers(@PathVariable(name = "topic") String topicName) {
        LOGGER.info("List all consumer of topicName '{}'", topicName);
        Topic topic = topicService.getOrCreateTopic(topicName);
        return subscriptionService.getConsumers(topic);
    }

    @DeleteMapping("/{consumer}")
    public void unsubscribe(@PathVariable(name = "topic") String topic,
                            @PathVariable(name = "consumer") String consumer) {
        subscriptionService.unsubscribeConsumer(topicService.getOrCreateTopic(topic), consumer);
        LOGGER.info("Consumer '{}' unsubscribed from topic '{}'", consumer, topic);
    }


    @PostMapping("/{consumer}")
    public void subscribe(@PathVariable(name = "topic") String topic,
                          @PathVariable(name = "consumer") String consumer) {
        subscriptionService.subscribeConsumer(topicService.getOrCreateTopic(topic), consumer);
        LOGGER.info("Consumer '{}' was subscribed on topic '{}'", consumer, topic);
    }

}
