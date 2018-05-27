package net.home.messagebroker.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.jcip.annotations.Immutable;

import java.util.Set;

@Immutable
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    private final String text;

    private final Set<String> topics;

    @JsonCreator
    public Message(@JsonProperty("text") String text,
                   @JsonProperty("topics") Set<String> topics) {
        this.text = text;
        this.topics = topics;
    }

}
