package com.hawk.iot.redis;

import cn.hutool.json.JSONUtil;
import com.hawk.iot.message.DownCommand;
import com.hawk.iot.message.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-28 07:30
 */
@Component
public class RedisMessagePublisher {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void publishUplink(ReportData data)  {
        String json = JSONUtil.toJsonStr(data);
        redisTemplate.convertAndSend("iot:uplink", json);
    }

    public void publishDownlink(DownCommand cmd) {
        String json = JSONUtil.toJsonStr(cmd);
        redisTemplate.convertAndSend("iot:downlink", json);
    }
}

