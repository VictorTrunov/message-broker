package net.home.messagebroker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TestThreadConfig {

    @Bean("testTaskExecutor")
    public ThreadPoolTaskExecutor getTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        int maxPoolSize = Runtime.getRuntime().availableProcessors();
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setThreadNamePrefix("test-pool");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return  taskExecutor;

    }
}