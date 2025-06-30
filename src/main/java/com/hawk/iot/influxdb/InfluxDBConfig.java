package com.hawk.iot.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-30 17:23
 */
@Configuration
public class InfluxDBConfig {

    @Bean
    public InfluxDB influxDB(
            @Value("${iot.influxdb.url}") String url,
            @Value("${iot.influxdb.username}") String username,
            @Value("${iot.influxdb.password}") String password,
            @Value("${iot.influxdb.database}") String database) {

        InfluxDB influxDB = InfluxDBFactory.connect(url, username, password);
        influxDB.setDatabase(database);
        // 启用异步批量写入（也可关闭）
        influxDB.enableBatch();
        return influxDB;
    }
}

