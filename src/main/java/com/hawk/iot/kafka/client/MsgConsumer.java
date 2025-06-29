package com.hawk.iot.kafka.client;

import com.hawk.iot.kafka.BizHandler;
import jakarta.annotation.Resource;
import jakarta.persistence.Access;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: jp-supv-platform
 * @description:
 * @author: summer
 * @create: 2022-01-12 13:50
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "spring.kafka1.consumer", name = "bootstrap-servers", matchIfMissing = false)
public class MsgConsumer {
    @Resource
    private BizHandler handler;

    @KafkaListener(
            topics = "${spring.kafka1.consumer.upTopics}",
            containerFactory = "uplinkKafkaListenerContainerFactory",
            clientIdPrefix = "consumer",
            groupId = "iot-record-up"
    )
    public void processUpMsg(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
        if (records == null || records.isEmpty()) {
            return;
        }

        for (ConsumerRecord<?, ?> record : records) {
            try {
                log.info("【上行】topic={}, value={}", record.topic(), record.value());
                handler.handleUplink(record.value().toString());
            } catch (Exception e) {
                log.error("处理上行消息失败: topic={}, value={}, error={}", record.topic(), record.value(), e.getMessage(), e);
                // TODO: 可选记录失败消息到死信队列或重试机制
            }
        }

        ack.acknowledge();
    }

    /**
     * 下行消息处理器
     */
    @KafkaListener(
            topics = "${spring.kafka1.consumer.downTopics}",
            containerFactory = "downlinkKafkaListenerContainerFactory",
            clientIdPrefix = "consumer",
            groupId = "iot-record-down"
    )
    public void processDownMsg(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
        if (records == null || records.isEmpty()) {
            return;
        }

        for (ConsumerRecord<?, ?> record : records) {
            try {
                log.info("【下行】topic={}, value={}", record.topic(), record.value());
                handler.handleDownlink(record.value().toString());
            } catch (Exception e) {
                log.error("处理下行消息失败: topic={}, value={}, error={}", record.topic(), record.value(), e.getMessage(), e);
                // TODO: 可选记录失败消息到死信队列或报警系统
            }
        }

        ack.acknowledge();
    }
}