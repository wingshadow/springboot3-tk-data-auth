package com.hawk.iot.cache;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放终端SocketChannel
 *
 * @author hawk
 * @version 1.0.0 2016年12月23日 上午10:33:05
 */
@Slf4j
public class ChannelCache {

    private static volatile ChannelCache instance;
    private final Map<String, SocketChannel> channelMap = new ConcurrentHashMap<>();
    private final Map<SocketChannel, String> reverseMap = new ConcurrentHashMap<>();
    private final Map<String, String> tidChannelMap = new ConcurrentHashMap<>();

    private ChannelCache() {
    }

    public static ChannelCache getInstance() {
        if (instance == null) {
            synchronized (ChannelCache.class) {
                if (instance == null) {
                    instance = new ChannelCache();
                }
            }
        }
        return instance;
    }

    public void add(String tid, SocketChannel channel) {
        if (tid != null && channel != null) {
            String channelId = channel.id().asLongText();
            channelMap.put(channelId, channel);
            reverseMap.put(channel, channelId);
            tidChannelMap.put(tid, channelId);
        }
    }

    public SocketChannel get(String tid) {
        String channelId = tidChannelMap.get(tid);
        SocketChannel channel = channelMap.get(channelId);
        return channel;
    }

    public String get(SocketChannel socketChannel) {
        return reverseMap.get(socketChannel);
    }

    public void remove(SocketChannel socketChannel) {
        String channelId = reverseMap.remove(socketChannel);
        channelMap.remove(channelId);
        tidChannelMap.entrySet().removeIf(e -> e.getValue().equals(channelId));
        reverseMap.entrySet().removeIf(e -> e.getValue().equals(channelId));
    }

    public void remove(String channelId) {
        channelMap.remove(channelId);
        tidChannelMap.entrySet().removeIf(e -> e.getValue().equals(channelId));
        reverseMap.entrySet().removeIf(e -> e.getValue().equals(channelId));
    }

    public boolean isOnline(String tid) {
        String channelId = tidChannelMap.get(tid);
        if (StrUtil.isBlank(channelId)) {
            return false;
        }
        SocketChannel channel = channelMap.get(channelId);
        return channel != null && channel.isActive();
    }
}

