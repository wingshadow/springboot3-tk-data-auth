package com.hawk.iot.influxdb;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-30 17:25
 */
@Slf4j
@Service
public class InfluxWriterService {

    @Resource
    private InfluxDB influxDB;

    @Value("${iot.influxdb.database}")
    private String database;

    @Value("${iot.influxdb.retention-policy}")
    private String retentionPolicy;

    @Resource
    private ThreadPoolTaskScheduler singleThreadTaskScheduler;

    private static final int BATCH_SIZE = 500;
    private static final long MAX_WAIT_MS = 5000;

    private final Queue<Point> pointQueue = new ConcurrentLinkedQueue<>();


    public InfluxWriterService() {
        // 延时每隔5秒执行一次
        singleThreadTaskScheduler.scheduleAtFixedRate(this::flush, MAX_WAIT_MS);
    }

    public void writeBatch(List<IotPoint> dataList) {
        BatchPoints batchPoints = BatchPoints
                .database(database)
                .retentionPolicy(retentionPolicy)
                .consistency(InfluxDB.ConsistencyLevel.ONE)
                .build();

        for (IotPoint data : dataList) {
            Point point = PointConverter.toInfluxPoint(data);
            batchPoints.point(point);
        }

        influxDB.write(batchPoints);
    }

    public void addPoint(Point point) {
        pointQueue.add(point);
        if (pointQueue.size() >= BATCH_SIZE) {
            flush();
        }
    }

    private synchronized void flush() {
        if (pointQueue.isEmpty()) {
            return;
        }

        BatchPoints batchPoints = BatchPoints.database("iot_db")
                .retentionPolicy("autogen")
                .consistency(InfluxDB.ConsistencyLevel.ONE)
                .build();

        int count = 0;
        while (count < BATCH_SIZE && !pointQueue.isEmpty()) {
            Point p = pointQueue.poll();
            if (p != null) {
                batchPoints.point(p);
                count++;
            }
        }
        if (count > 0) {
            influxDB.write(batchPoints);
            log.info("批量写入 {} 条数据到 InfluxDB", count);
        }
    }
}

