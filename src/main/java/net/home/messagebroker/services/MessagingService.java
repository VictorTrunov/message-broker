package net.home.messagebroker.services;

import net.home.messagebroker.messaging.Message;
import net.home.messagebroker.messaging.Topic;
import net.home.messagebroker.messaging.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class MessagingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingService.class);

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    public void postMessage(Message message) {
        ThreadPoolExecutor executor = taskExecutor.getThreadPoolExecutor();
        Set<Topic> topics = topicService.getOrCreateTopics(message.getTopics());
        List<Callable<Void>> tasks = new ArrayList<>();
        topics.forEach(topic -> tasks.addAll(buildTasks(topic, message.getText())));
        try {
            executor.invokeAll(tasks);
            LOGGER.info("Message was sent to all topics");
        } catch (InterruptedException e) {
            LOGGER.error("Exception while posting message to topics '" + message.getTopics() + "' consumers", e);
        }
    }

    private List<Callable<Void>> buildTasks(Topic topic, String message) {
        return subscriptionService.getConsumers(topic).stream()
                .map(consumer -> buildSingleTask(topic, consumer, message))
                .collect(Collectors.toList());
    }

    private Callable<Void> buildSingleTask(Topic topic, Consumer consumer, String message) {
        return () -> {
            consumer.receiveMessage(topic.getName(), message);
            return null;
        };
    }
}
