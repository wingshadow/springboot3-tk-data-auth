package com.hawk.iot.message;

import lombok.Data;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-27 14:58
 */
@Data
public class PowerSupplyStatus {
    /**
     * AC/DC
     */
    private int type;
    /**
     * 交流电压
     */
    private Double voltageValue;
    /**
     * true开false关
     */
    private Integer dcStatus;

    public static final int TYPE_AC = 1;
    public static final int TYPE_DC = 2;

    public boolean isAC() {
        return type == TYPE_AC;
    }

    public boolean isDC() {
        return type == TYPE_DC;
    }

    @Override
    public String toString() {
        if (isAC()) {
            return "交流供电：" + voltageValue + "V";
        } else if (isDC()) {
            return "直流供电：" + (Boolean.TRUE.equals(dcStatus) ? "通电" : "断电");
        }
        return "未知供电状态";
    }

    public static PowerSupplyStatus parse(Double input) {
        PowerSupplyStatus status = new PowerSupplyStatus();
        if (input == null) return null;

        if (input.equals(-1.0)) {
            status.type = 2;
            status.dcStatus = 1;
        } else if (input.equals(-2.0)) {
            status.type = 2;
            status.dcStatus = 2;
        } else {
            status.type = 1;
            status.voltageValue = input;
        }
        return status;
    }
}
