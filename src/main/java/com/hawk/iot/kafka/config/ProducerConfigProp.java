package com.hawk.iot.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: jp-supv-platform
 * @description:
 * @author: summer
 * @create: 2022-01-12 11:43
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.kafka1.producer")
public class ProducerConfigProp {

    private String bootstrapServers;

    private int retries;

    private int batchSize;

    private int bufferMemory;

    private String keySerializer;

    private String valueSerializer;

    private String acks;

    private String upTopics;

    private String downTopics;

    private int partition;

    private short replica;
}