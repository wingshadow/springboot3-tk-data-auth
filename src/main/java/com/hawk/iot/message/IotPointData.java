package com.hawk.iot.message;

import com.hawk.iot.influxdb.IotPoint;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-07-01 11:26
 */
@Data
public class IotPointData implements Serializable {

    private String tid;

    private List<IotPoint> list;
}
