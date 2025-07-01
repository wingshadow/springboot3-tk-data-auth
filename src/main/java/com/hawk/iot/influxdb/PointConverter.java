package com.hawk.iot.influxdb;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-30 17:39
 */
@Slf4j
public class PointConverter {
    public static Point toInfluxPoint(IotPoint iotPoint) {
        Point.Builder builder = Point.measurement(iotPoint.getMeasurement())
                .time(iotPoint.getTimestamp(), TimeUnit.MILLISECONDS);

        if (iotPoint.getTags() != null) {
            iotPoint.getTags().forEach(builder::tag);
        }
        if (iotPoint.getFields() != null) {
            iotPoint.getFields().forEach((key, val) -> safeAddField(builder, key, val));
        }

        return builder.build();
    }

    public static List<Point> toInfluxPoints(List<IotPoint> iotPoints) {
        return iotPoints.stream()
                .map(PointConverter::toInfluxPoint)
                .collect(Collectors.toList());
    }


    public static void safeAddField(Point.Builder builder, String key, Object value) {
        if (value instanceof Integer) {
            builder.addField(key, (Integer) ((Integer) value).intValue());
        } else if (value instanceof Long) {
            builder.addField(key, (Long) ((Long) value).longValue());
        } else if (value instanceof Double) {
            builder.addField(key, (Double) ((Double) value).doubleValue());
        } else if (value instanceof String) {
            builder.addField(key, (String) value);
        } else if (value instanceof BigDecimal) {
            builder.addField(key, ((BigDecimal) value).doubleValue());
        } else if (value != null) {
            builder.addField(key, value.toString());
        }
    }

    public static void safeAddField2(Point.Builder builder, String fieldName, Object value) {
        if (value == null) {
            return;
        }
        try {
            if (value instanceof Number) {
                builder.addField(fieldName, (Number) value);
            } else {
                String str = value.toString().trim();
                if (str.isEmpty()) {
                    return;
                }

                if (str.contains(".")) {
                    builder.addField(fieldName, Double.parseDouble(str));
                } else {
                    builder.addField(fieldName, Long.parseLong(str));
                }
            }
        } catch (Exception e) {
            // 无法转换的直接跳过或日志记录
            log.warn("字段 [{}] 类型转换失败：{}", fieldName, value);
        }
    }


}
