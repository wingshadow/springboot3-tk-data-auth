package com.hawk.iot.kafka.config;

import jakarta.annotation.Resource;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: jp-supv-platform
 * @description:
 * @author: summer
 * @create: 2022-01-12 13:42
 */
@Configuration
@EnableKafka
public class ConsumerConfig {

    @Resource
    private ConsumerConfigProp consumerConfigProp;

    public Map<String, Object> baseConsumerProps(String groupIdOverride) {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerConfigProp.getBootstrapServers());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerConfigProp.isEnableAutoCommit());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, consumerConfigProp.getAutoCommitInterval());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumerConfigProp.getSessionTimeoutMs());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, consumerConfigProp.getMaxPollIntervalMs());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, groupIdOverride != null ? groupIdOverride : consumerConfigProp.getGroupId());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerConfigProp.getAutoOffsetReset());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        /**
         * 批量消费消息数
         */
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerConfigProp.getMaxPollRecords());
        return props;
    }

    private ConcurrentKafkaListenerContainerFactory<String, String> buildFactory(String groupIdOverride) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(baseConsumerProps(groupIdOverride)));
        factory.setConcurrency(Math.max(1, consumerConfigProp.getPartition()));
        factory.setBatchListener(true);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
    @Bean(name = "uplinkKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> uplinkKafkaListenerContainerFactory() {
        return buildFactory("jx-uplink-group");
    }

    @Bean(name = "downlinkKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> downlinkKafkaListenerContainerFactory() {
        return buildFactory("jx-downlink-group");
    }
}