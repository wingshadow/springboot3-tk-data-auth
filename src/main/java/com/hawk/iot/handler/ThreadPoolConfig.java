package com.hawk.iot.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-28 06:57
 */
@Configuration
public class ThreadPoolConfig {
    @Bean("iotExecutor")
    public Executor iotExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("iot-exec-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * Kafka 消费消息的处理和批量累积写入 InfluxDB 的场景中，批量提交操作本质是：
     * 周期性执行批量写入操作
     * 防止并发冲突、保证写入顺序和数据一致性
     * 减少写入压力（避免同时大量写入）
     * 单线程调度池能保证：
     * 同一时间只有一个批量写入操作在执行
     * 避免多线程写入导致的状态混乱或数据竞争
     * 代码更简单，不用额外同步
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler singleThreadTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 单线程
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("my-single-scheduled-thread-");
        scheduler.setDaemon(false);
        scheduler.initialize();
        return scheduler;
    }
}
