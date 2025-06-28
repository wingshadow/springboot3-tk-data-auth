package com.hawk.iot.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-28 07:20
 */
@Configuration
public class RedisPubSubConfig {
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory,
                                                                       RedisUplinkDownlinkListener listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        ChannelTopic downTopic = new ChannelTopic("iot:downlink");
        ChannelTopic upTopic = new ChannelTopic("iot:uplink");


        container.addMessageListener(listener, upTopic);
        container.addMessageListener(listener, downTopic);

        return container;
    }
}
