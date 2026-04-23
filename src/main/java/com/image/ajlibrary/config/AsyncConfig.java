package com.image.ajlibrary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * CO2 - Thread Pool Configuration (Multithreading).
 *
 * Defines a named ThreadPoolTaskExecutor used by @Async methods
 * throughout the application. This makes async work visible and
 * configurable rather than relying on SimpleAsyncTaskExecutor.
 *
 * Thread pool parameters are externalised to application.properties
 * so they can be tuned per environment without code changes.
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Value("${library.async.core-pool-size:5}")
    private int corePoolSize;

    @Value("${library.async.max-pool-size:10}")
    private int maxPoolSize;

    @Value("${library.async.queue-capacity:25}")
    private int queueCapacity;

    @Value("${library.async.thread-name-prefix:library-async-pool-}")
    private String threadNamePrefix;

    /**
     * Primary async executor used when @Async has no explicit executor name.
     * Named "libraryAsyncExecutor" so services can reference it explicitly.
     */
    @Bean(name = "libraryAsyncExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);       // always-alive threads
        executor.setMaxPoolSize(maxPoolSize);          // burst capacity
        executor.setQueueCapacity(queueCapacity);      // task queue before rejection
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
