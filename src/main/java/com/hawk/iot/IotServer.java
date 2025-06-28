package com.hawk.iot;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-27 08:11
 */
@Slf4j
@Component
public class IotServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    @Resource(name = "myChannelInitializer")
    private MyChannelInitializer myChannelInitializer;

    public IotServer() {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
    }

    private void start() {
        try {
            log.info("启动IotServer 监听端口6012");
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(myChannelInitializer);

            // 初始化服务端可连接队列数
            b.option(ChannelOption.SO_BACKLOG, 1024);
            // 允许长连接
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
            b.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024));
            // 通过NoDelay禁用Nagle,允许小数据即时传输
            b.childOption(ChannelOption.TCP_NODELAY, true);
            // 允许重复使用本地地址和端口
            b.childOption(ChannelOption.SO_REUSEADDR, true);

            ChannelFuture f = b.bind(6012).sync();
            serverChannel = f.channel();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @PostConstruct
    public void doStart() {
        start();
    }
    @PreDestroy
    public void doStop() {
        stop();
    }

    private void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        serverChannel = null;
        workerGroup = null;
        bossGroup = null;
    }
}
