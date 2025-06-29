package com.hawk.iot.kafka;

/**
 * @program: springboot3-tk-data-auth
 * @description: 业务处理
 * @author: zhb
 * @create: 2025-06-29 09:54
 */
public interface BizHandler {
    void handleUplink(String message);

    void handleDownlink(String message);
}
