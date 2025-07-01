package com.hawk.system.service;

import com.hawk.App;
import com.hawk.iot.builder.CommandBuilder;
import com.hawk.iot.message.DownCommand;
import com.hawk.iot.redis.RedisMessagePublisher;
import com.hawk.iot.redis.RedisPubSubConfig;
import com.hawk.utils.iot.HexUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-28 16:08
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class})
public class RedisTest {

    @Resource
    private RedisMessagePublisher publisher;

    @Test
    public void test(){
        List<String> list = new ArrayList<>();
        list.add("all##test1.fnwlw.net:6102");
        list.add("8157.fnwlw.net:6101");

        CommandBuilder builder = new CommandBuilder();
        String str = builder.buildServerConfigFrame(660968,list);
        DownCommand downCommand = new DownCommand();
        downCommand.setCmd(str);
        downCommand.setTid("660968");
        publisher.publishDownlink(downCommand);
    }

}
