package com.hawk.iot.influxdb;

import org.influxdb.dto.Point;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-30 17:39
 */
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
            builder.addField(key, (Integer) value);
        } else if (value instanceof Long) {
            builder.addField(key, (Long) value);
        } else if (value instanceof Float) {
            builder.addField(key, (Float) value);
        } else if (value instanceof Double) {
            builder.addField(key, (Double) value);
        } else if (value instanceof Boolean) {
            builder.addField(key, (Boolean) value);
        } else if (value instanceof String) {
            builder.addField(key, (String) value);
        } else if (value != null) {
            builder.addField(key, value.toString());
        }
    }

}
