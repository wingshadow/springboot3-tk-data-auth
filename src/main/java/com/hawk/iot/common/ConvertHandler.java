package com.hawk.iot.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hawk.iot.message.PowerSupplyStatus;
import com.hawk.iot.message.ReportData;
import com.hawk.utils.StringUtils;
import com.hawk.utils.iot.IotUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-27 14:02
 */
public class ConvertHandler {

    public static ReportData convert(String data){
        List<String> msglist = StringUtils.extractBlocks(data,"##");
        if(CollUtil.isNotEmpty(msglist)){
            String msg = msglist.get(0);
            String msgLengthStr =  StringUtils.substring(msg,0,3);
            String body = StringUtils.substring(msg,4,msg.length()-1);

            Map<String, String> map = IotUtils.parseKeyValueString(body);

            ReportData reportData = new ReportData();

            // 字符串字段
            reportData.setQn(map.get("QN"));
            reportData.setTid(map.get("TID"));
            reportData.setVer(map.get("VER"));
            reportData.setDn(map.get("DT"));

            // List 字段
            reportData.setCps(IotUtils.safeParseIntegerList(map, "CPS"));
            reportData.setCns(IotUtils.safeParseIntegerList(map, "CNS"));
            reportData.setMn(IotUtils.safeParseIntegerList(map, "MN"));
            reportData.setRelay(IotUtils.safeParseIntegerList(map, "RELAY"));
            reportData.setChv(IotUtils.safeParseDoubleList(map, "CHV"));
            reportData.setCha(IotUtils.safeParseDoubleList(map, "CHA"));
            reportData.setChp(IotUtils.safeParseDoubleList(map, "POWER"));
            reportData.setChk(IotUtils.safeParseDoubleList(map, "ELEC"));

            // 交流/直流供电
            String v = map.get("V");
            if (StringUtils.isNotBlank(v)) {
                reportData.setVol(PowerSupplyStatus.parse(Double.valueOf(v)));
            }

            // Double 类型字段
            reportData.setAmpere(IotUtils.safeParseDouble(map, "A"));
            reportData.setHumidity(IotUtils.safeParseDouble(map, "H"));
            reportData.setTemperature(IotUtils.safeParseDouble(map, "T"));
            reportData.setApower(IotUtils.safeParseDouble(map, "APOWER"));
            reportData.setAkw(IotUtils.safeParseDouble(map, "AKW"));

            // Integer 类型字段
            reportData.setDoorStatus(IotUtils.safeParseInteger(map, "DS"));
            reportData.setPosture(IotUtils.safeParseInteger(map, "P"));
            reportData.setSurge(IotUtils.safeParseInteger(map, "SPD"));
            reportData.setPa(IotUtils.safeParseInteger(map, "PA"));
            reportData.setLes(IotUtils.safeParseInteger(map, "LES"));
            reportData.setOv(IotUtils.safeParseInteger(map, "OV"));
            reportData.setOcps(IotUtils.safeParseInteger(map, "OCPS"));
            reportData.setBat(IotUtils.safeParseInteger(map, "BAT"));
            reportData.setSim(IotUtils.safeParseInteger(map, "SIM"));
            reportData.setWater(IotUtils.safeParseInteger(map, "WATER"));
            reportData.setMiu(IotUtils.safeParseInteger(map, "MIU"));

            return reportData;
        }
        return null;
    }

    public static void main(String[] args) {
        String msg = "##0163QN=20200730160101008;TID=123456;VER=11;CP=&&DT=20200730160101;CPS=1,0,2;CNS=1,0,2,1,1,0;MN=1,1;V=225;A=235;H=50.5;T=-20.5;DS=1;P=50;SPD=1;PA=0;FES=0;LES=1;LA=200;HES=1&&be##";
        String msg2= "##0427QN=-0000001-00000001;TID=660968;VER=11;CP=&&DT=20250108103624;CPS=1;CNS=1,1,1,1,1,1;MN=1,2;V=227.39;A=34.77;H=8.31;T=30.12;DS=1;P=100;SPD=0;PA=1;LES=1;OV=0;OCPS=0;BAT=0;SIM=1;POWER=1,2,3,4,4,6,5,4;WATER=1;APOWER=7.32;AKW=0.01;RELAY=2,1,1,1,1,1,1,1;CHV=227.39,227.39,227.39,227.39,227.39,227.39,227.39,227.39;CHA=34.75,0.03,0.15,0.11,0.21,0.24,0.44,1.27;ELEC=0.01,0.00,0.00,0.00,0.00,0.00,0.00,0.00;ur=0&&34##";
        ConvertHandler.convert(msg2);
    }
}
