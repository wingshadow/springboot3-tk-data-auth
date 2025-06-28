package com.hawk.iot.common;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-27 13:42
 */
public class ProtocolType {
    public static final int INVALID = -1;
    public static final int UNKNOWN = 0;
    public static final int HEARTBEAT = 1;
    public static final int DATA_REPORT = 2;

    // 心跳包消息head标识
    public static final int HEART_HEAD = 0x0F;
    // 上报数据消息head标识
    public static final int REPORT_HEAD = 0x23;
}
