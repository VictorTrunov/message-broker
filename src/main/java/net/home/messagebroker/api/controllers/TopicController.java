package net.home.messagebroker.api.controllers;

import net.home.messagebroker.messaging.Message;
import net.home.messagebroker.messaging.Topic;
import net.home.messagebroker.services.MessagingService;
import net.home.messagebroker.services.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/topic")
public class TopicController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicController.class);
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private TopicService topicService;

    @PostMapping(consumes = "application/json")
    public void postMessage(@RequestBody Message message) {
        Set<String> targetTopics = message.getTopics();
        LOGGER.info("Try to post message '{}' on topics [{}]", message.getText(), targetTopics);
        messagingService.postMessage(message);
        LOGGER.info("Message posted on topics [{}]", targetTopics);
    }

    @GetMapping(produces = "application/json")
    public Set<Topic> getTopics() {
        LOGGER.info("List all topics");
        return topicService.getTopics();
    }

    @DeleteMapping("/{topic}")
    public void removeTopic(@PathVariable String topicName) {
        LOGGER.info("Try to remove topic '{}'", topicName);
        topicService.removeTopic(topicName);
        LOGGER.info("Topic '{}' removed", topicName);
    }

}
