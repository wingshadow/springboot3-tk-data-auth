package com.hawk.iot.redis;

import cn.hutool.json.JSONUtil;
import com.hawk.iot.cache.ChannelCache;
import com.hawk.iot.message.DownCommand;
import com.hawk.iot.message.ReportData;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-28 07:21
 */
@Slf4j
@Component
public class RedisUplinkDownlinkListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String body = new String(message.getBody(), StandardCharsets.UTF_8);

        log.info("收到频道 [{}] 消息：{}", channel, body);

        try {
            switch (channel) {
                case "iot:uplink":
                    handleUplink(body);
                    break;
                case "iot:downlink":
                    handleDownlink(body);
                    break;
                default:
                    log.warn("未知频道消息: {}", channel);
            }
        } catch (Exception e) {
            log.error("处理消息异常，频道: {}, 内容: {}", channel, body, e);
        }
    }

    private void handleUplink(String json) throws Exception {
        // 反序列化成业务对象（举例）
        ReportData reportData = JSONUtil.toBean(json,ReportData.class);
        log.info("处理上行数据: tid={}, 数据={}", reportData.getTid(), reportData);
        // TODO: 业务处理：存库、更新缓存等
    }

    private void handleDownlink(String json) throws Exception {
        DownCommand cmd = JSONUtil.toBean(json,DownCommand.class);
        log.info("处理下行指令: tid={}, cmd={}", cmd.getTid(), cmd.getCmd());

        SocketChannel channel =  ChannelCache.getInstance().get(cmd.getTid());
        if (channel != null && channel.isActive()) {
            // TODO 命令编码
            byte[] bytes = new byte[]{};
            channel.writeAndFlush(Unpooled.wrappedBuffer(bytes));
        } else {
            log.warn("设备 [{}] 不在线，下行指令发送失败", cmd.getTid());
        }
    }
}

