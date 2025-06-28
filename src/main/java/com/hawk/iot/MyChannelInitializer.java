package com.hawk.iot;

import cn.hutool.extra.spring.SpringUtil;
import com.hawk.iot.handler.ReceiveIotMsgHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-27 08:11
 */
@Component("myChannelInitializer")
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new IdleStateHandler(
                30, 0, 0));
        ReceiveIotMsgHandler receiveIotMsgHandler = SpringUtil.getBean("receiveIotMsgHandler", ReceiveIotMsgHandler.class);
        socketChannel.pipeline().addLast(receiveIotMsgHandler);
    }
}
