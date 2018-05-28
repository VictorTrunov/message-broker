package net.home.messagebroker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TestThreadConfig {

    @Bean("testTaskExecutor")
    public ThreadPoolTaskExecutor getTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        int poolSize = Runtime.getRuntime().availableProcessors();
        taskExecutor.setCorePoolSize(poolSize);
        taskExecutor.setMaxPoolSize(10 * poolSize);
        taskExecutor.setThreadNamePrefix("test-pool-");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return  taskExecutor;

    }
}