package com.hawk.iot.influxdb;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-30 17:29
 */
@Data
public class IotPoint {
    /**
     * 表名
     */
    private String measurement;
    /**
     * 时间戳，毫秒
     */
    private long timestamp;
    /**
     * 标签
     */
    private Map<String, String> tags = new HashMap<>();
    /**
     * 字段
     */
    private Map<String, Object> fields = new HashMap<>();
}
