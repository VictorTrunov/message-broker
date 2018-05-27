package net.home.messagebroker.services;

import net.home.messagebroker.messaging.Topic;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final ConcurrentMap<String, Topic> topics = new ConcurrentHashMap<>();

    public Set<Topic> getTopics() {
        return Collections.unmodifiableSet(new HashSet<>(topics.values()));
    }

    public void removeTopic(Topic topic) {
        topics.remove(Objects.requireNonNull(topic).getName());
    }

    public void removeTopic(String topicName) {
        topics.remove(Objects.requireNonNull(topicName));
    }

    public Topic getOrCreateTopic(String topicName) {
        return topics.computeIfAbsent(Objects.requireNonNull(topicName), tn -> new Topic(topicName));
    }

    public Set<Topic> getOrCreateTopics(Set<String> topicsNames) {
        return topicsNames.stream()
                .map(topicName -> getOrCreateTopic(topicName))
                .collect(Collectors.toSet());
    }


}
