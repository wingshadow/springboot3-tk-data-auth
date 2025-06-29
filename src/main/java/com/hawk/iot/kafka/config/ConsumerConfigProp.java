package com.hawk.iot.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: jp-supv-platform
 * @description:
 * @author: summer
 * @create: 2022-01-12 11:49
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.kafka1.consumer")
public class ConsumerConfigProp {

    private String bootstrapServers;

    private boolean enableAutoCommit;

    private int autoCommitInterval;

    private String groupId;

    private String autoOffsetReset;

    private int maxPollRecords;

    private String keySerializer;

    private String valueSerializer;

    private int partition;

    private String jaas;

    private int blockQueueMaxSize;

    private int sessionTimeoutMs;

    private int maxPollIntervalMs;

    private String upTopics;

    private String downTopics;

    private short replica;

}