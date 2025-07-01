package com.hawk.iot.kafka.config;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: jp-supv-platform
 * @description:
 * @author: summer
 * @create: 2022-01-12 13:40
 */
@EnableKafka
@Configuration
public class ProducerConfig {

    @Resource
    private ProducerConfigProp producerConfigProp;

    @Bean
    public Map<String, Object> producerConfigMap() {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerConfigProp.getBootstrapServers());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, producerConfigProp.getRetries());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG, producerConfigProp.getBatchSize());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG, producerConfigProp.getBufferMemory());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        /**
         * 即所有副本都同步到数据时send方法才返回, 以此来完全判断数据是否发送成功, 理论上来讲数据不会丢失.
         */
        props.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, producerConfigProp.getAcks());
        return props;
    }

    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigMap());
    }

    @Bean(name = "kafkaTemplateLocal")
    public KafkaTemplate<String, String> kafkaTemplateLocal() {
        return new KafkaTemplate<String, String>(producerFactory());
    }
}