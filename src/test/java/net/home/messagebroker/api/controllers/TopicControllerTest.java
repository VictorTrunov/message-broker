package net.home.messagebroker.api.controllers;

import net.home.messagebroker.MessageBrokerApplication;
import net.home.messagebroker.messaging.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MessageBrokerApplication.class)
@WebAppConfiguration
public class TopicControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    @Qualifier("testTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Set<Integer> topicIds = new HashSet<>();

    private Set<Integer> customerIds = new HashSet<>();

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        int customerCount = 40000;
        int topicCount = 3000;
        List<Callable<Void>> tasks = buildCreateCustomersTasks(customerCount, topicCount);
        taskExecutor.getThreadPoolExecutor().invokeAll(tasks);
    }

    @Test
    public void sendMessagesAndRemoveTopicsAndUnsubscribeConsumers() throws Exception {
        int maxTopicPerMessageCount = 20;
        int count = 50000;
        List<Callable<Void>> postMessageTasks = buildPostMessageTasks(count, maxTopicPerMessageCount);
        taskExecutor.getThreadPoolExecutor().invokeAll(postMessageTasks);
    }

    private List<Callable<Void>> buildCreateCustomersTasks(int customerCount, int topicCount) {
        List<Callable<Void>> tasks = new ArrayList<>(customerCount);
        Random random = new Random();
        for (int i = 0; i < customerCount; i++) {
            int topicNumber = random.nextInt(topicCount);
            topicIds.add(topicNumber);
            int customerNumber = random.nextInt(customerCount);
            customerIds.add(customerNumber);
            tasks.add(createCustomerTask(topicNumber, customerNumber));
        }
        return tasks;
    }

    private Callable<Void> createCustomerTask(int topicNumber, int customerNumber) {
        return () -> {
            mockMvc.perform(subscribeCustomer(topicNumber, customerNumber));
            return null;
        };
    }

    private MockHttpServletRequestBuilder subscribeCustomer(int topicNumber, int customerNumber) {
        return post("/subscription/topic" + topicNumber + "/customer" + customerNumber);
    }

    private List<Callable<Void>> buildPostMessageTasks(int count, int maxTopicPerMessage) {
        List<Callable<Void>> tasks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            tasks.add(buildPostMessageTask(maxTopicPerMessage));
        }
        return tasks;
    }

    private Callable<Void> buildPostMessageTask(int maxTopicPerMessage) {
        return () -> {
            mockMvc.perform(post("/topic")
                    .content(json(buildMessage(maxTopicPerMessage)))
                    .contentType(contentType)
            );
            return null;
        };
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    private Message buildMessage(int maxTopicCount) {
        return new Message(randomString(), buildRandomTopics(maxTopicCount));
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private Set<String> buildRandomTopics(int maxTopicCount) {
        Set<String> topicNames = new HashSet<>();
        Random random = new Random();
        int count = random.nextInt(maxTopicCount) + 1;
        List<Integer> list = new ArrayList<>(topicIds);
        for (int i = 0; i < count; i++) {
            topicNames.add("topic" + list.get(random.nextInt(list.size())));
        }
        return topicNames;
    }


}
