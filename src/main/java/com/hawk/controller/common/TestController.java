package com.hawk.controller.common;

import cn.hutool.json.JSONUtil;
import com.hawk.framework.web.resp.R;
import com.hawk.iot.builder.CommandBuilder;
import com.hawk.iot.kafka.client.MsgProducer;
import com.hawk.iot.message.DownCommand;
import com.hawk.iot.redis.RedisMessagePublisher;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-28 16:34
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private RedisMessagePublisher publisher;

    @Resource
    private MsgProducer producer;

    @GetMapping("/send")
    public R<Void> send() {
        List<String> list = new ArrayList<>();
        list.add("all##test1.fnwlw.net:6102");
        list.add("8157.fnwlw.net:6101");

        CommandBuilder builder = new CommandBuilder();
        String str = CommandBuilder.buildServerConfigFrame(660968, list);
        DownCommand downCommand = new DownCommand();
        downCommand.setCmd(str);
        downCommand.setTid("660968");
//        publisher.publishDownlink(downCommand);
        producer.sendLocal("iot-down-cmd-queue", JSONUtil.toJsonStr(downCommand));
        return R.ok();
    }
}
