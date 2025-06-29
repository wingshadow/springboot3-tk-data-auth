package com.hawk.iot.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.hawk.iot.cache.ChannelCache;
import com.hawk.iot.common.ConvertHandler;
import com.hawk.iot.common.ProtocolType;
import com.hawk.iot.kafka.client.MsgProducer;
import com.hawk.iot.message.ReportData;
import com.hawk.iot.redis.RedisMessagePublisher;
import com.hawk.utils.StringUtils;
import com.hawk.utils.iot.HexUtil;
import com.hawk.utils.iot.IotUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-27 08:17
 */
@Slf4j
@Component("receiveIotMsgHandler")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReceiveIotMsgHandler extends ChannelInboundHandlerAdapter implements InitializingBean {

    @Autowired
    @Qualifier("iotExecutor")
    private Executor iotExecutor;

    @Value("${spring.kafka1.producer.upTopics}")
    private String topic;

    @Autowired
    private MsgProducer producer;
    @Autowired
    private RedisMessagePublisher publisher;

    /**
     * 当空闲时间到达IdleStateHandler设置空闲时间,触发该事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleEvent && idleEvent.state() == IdleState.READER_IDLE) {
            // 执行close触发channelInactive
            ctx.close();
            // todo 更新设备在线状态
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (!(msg instanceof ByteBuf)) {
            log.warn("收到非 ByteBuf 类型消息，忽略");
            return;
        }

        ByteBuf in = (ByteBuf) msg;
        try {
            int length = in.readableBytes();
            if (length == 0) {
                log.warn("收到空消息");
                return;
            }

            byte[] bytes = new byte[length];
            in.readBytes(bytes);

            // 十六进制打印 & 字符串打印
            log.info("收到数据（Hex）: {}", HexUtil.encode(bytes));
            log.info("收到数据（字符串）: {}", new String(bytes, StandardCharsets.UTF_8));

            // TODO: 判断心跳包和数据包，处理业务逻辑，比如解析协议或转发到业务线程池等
            // TODO: 解析数据获取终端设备序列号,socketChannel写入缓存
            if (IotUtils.checkCmdHead(bytes) == ProtocolType.HEARTBEAT) {
                // 心跳消息,保持长连接,不进行处理
                log.info("Received heartbeat from {}", ctx.channel().remoteAddress());
                return;
            } else if (IotUtils.checkCmdHead(bytes) == ProtocolType.DATA_REPORT) {
                // 解析数据获取终端设备序列号,socketChannel写入缓存
                handleReportData(ctx, bytes);
            }


        } catch (Exception e) {
            log.error("读取数据异常", e);
        } finally {
            in.release(); // 防止内存泄漏
        }
    }

    private void handleReportData(ChannelHandlerContext ctx, byte[] bytes) {
        String data = new String(bytes, StandardCharsets.UTF_8);
        ReportData reportData = ConvertHandler.convert(data);

        if (ObjectUtil.isNull(reportData)) {
            log.warn("Failed to parse report data: {}", data);
            return;
        }
        // 添加socket缓存
        String tid = reportData.getTid();
        SocketChannel channel = (SocketChannel) ctx.channel();
        if (!ChannelCache.getInstance().isOnline(tid)) {
            // 缓存不存在socket说明第一次上线,更新设备状态为在线
            ChannelCache.getInstance().add(tid, channel);
        }


        log.info("Mapped TID [{}] to Channel [{}]", tid, channel.id().asShortText());
        log.info("data:{}", JSONUtil.toJsonStr(reportData));
        producer.sendLocal(topic, JSONUtil.toJsonStr(reportData));
//        publisher.publishUplink(reportData);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelCache.getInstance().remove((SocketChannel) ctx.channel());
        log.info("连接关闭，清除缓存：{}", ctx.channel().remoteAddress());
        // TODO 更新设备状态信息下线
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Channel错误: {}", cause.getLocalizedMessage());
        log.error(cause.getMessage(), cause);
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
