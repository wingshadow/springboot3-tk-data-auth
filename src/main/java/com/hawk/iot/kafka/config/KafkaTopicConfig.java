package com.hawk.iot.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: jp-supv-platform
 * @description:
 * @author: hawk
 * @create: 2022-08-24 13:48
 */
@Configuration
public class KafkaTopicConfig {

    @Resource
    private ProducerConfigProp producerConfigProp;

    @Resource
    private ConsumerConfigProp consumerConfigProp;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, producerConfigProp.getBootstrapServers());
        return new KafkaAdmin(config);
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka1.producer", name = "bootstrap-servers", matchIfMissing = false)
    public NewTopic upTopic() {
        return new NewTopic(producerConfigProp.getUpTopics(), producerConfigProp.getPartition(), producerConfigProp.getReplica());
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka1.consumer", name = "bootstrap-servers", matchIfMissing = false)
    public NewTopic downTopic() {
        return new NewTopic(consumerConfigProp.getDownTopics(), consumerConfigProp.getPartition(), consumerConfigProp.getReplica());
    }
}