package com.hawk.iot.handler;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.hawk.iot.cache.ChannelCache;
import com.hawk.iot.influxdb.InfluxWriterService;
import com.hawk.iot.influxdb.IotPoint;
import com.hawk.iot.kafka.BizHandler;
import com.hawk.iot.message.DownCommand;
import com.hawk.iot.message.IotPointData;
import com.hawk.iot.message.ReportData;
import com.hawk.utils.iot.HexUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-29 09:55
 */
@Slf4j
@Component
public class ReportHandler implements BizHandler {

    @Resource
    private InfluxWriterService influxWriterService;
    @Override
    public void handleUplink(String msg) {
        // 反序列化成业务对象（举例）
//        ReportData reportData = JSONUtil.toBean(msg,ReportData.class);
        IotPointData pointData = JSONUtil.toBean(msg,IotPointData.class);
        log.info("处理上行数据: tid={}, 数据={}", pointData.getTid(), JSONUtil.toJsonStr(pointData));
        // TODO: 业务处理：存库、更新缓存等
        // TODO: reportData 转换为 iotPoint
        influxWriterService.addPoint(pointData.getList());
    }

    @Override
    public void handleDownlink(String message) {
        DownCommand cmd = JSONUtil.toBean(message,DownCommand.class);
        SocketChannel channel =  ChannelCache.getInstance().get(cmd.getTid());
        if (channel != null && channel.isActive()) {
            // TODO 命令编码
            byte[] bytes = HexUtil.decode(cmd.getCmd());
            channel.writeAndFlush(Unpooled.wrappedBuffer(bytes));
        } else {
            log.warn("设备 [{}] 不在线，下行指令发送失败", cmd.getTid());
        }
    }
}
