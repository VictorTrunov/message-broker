package net.home.messagebroker.messaging;

import lombok.Data;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
@Data
public class Topic {

    private static final Logger LOGGER = LoggerFactory.getLogger(Topic.class);

    private final String name;

}
