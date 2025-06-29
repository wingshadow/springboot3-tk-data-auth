package com.hawk.iot.kafka.client;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @program: jp-supv-platform
 * @description:
 * @author: summer
 * @create: 2022-01-12 13:45
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "spring.kafka1.producer", name = "bootstrap-servers", matchIfMissing = true)
public class MsgProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplateLocal;

    public void sendLocal(String topic, String msg) {
        kafkaTemplateLocal.send(topic, msg);
    }
}