package com.hawk.iot.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hawk.iot.influxdb.IotPoint;
import com.hawk.iot.message.IotPointData;
import com.hawk.iot.message.PowerSupplyStatus;
import com.hawk.iot.message.ReportData;
import com.hawk.utils.StringUtils;
import com.hawk.utils.iot.IotUtils;

import java.util.*;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-27 14:02
 */
public class ConvertHandler {

    public static ReportData convertReportData(String data) {
        List<String> msglist = StringUtils.extractBlocks(data, "##");
        if (CollUtil.isNotEmpty(msglist)) {
            String msg = msglist.get(0);
            String msgLengthStr = StringUtils.substring(msg, 0, 3);
            String body = StringUtils.substring(msg, 4, msg.length() - 1);

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

    public static IotPointData convertIotPoint(String data) {
        List<String> msglist = StringUtils.extractBlocks(data, "##");
        if (CollUtil.isNotEmpty(msglist)) {
            IotPointData pointData = new IotPointData();
            String msg = msglist.get(0);
            String msgLengthStr = StringUtils.substring(msg, 0, 3);
            String body = StringUtils.substring(msg, 4, msg.length() - 1);

            Map<String, String> map = IotUtils.parseKeyValueString(body);


            List<IotPoint> pointList = new ArrayList<>();
            List<Integer> cpslist = IotUtils.safeParseIntegerList(map, "CPS");
            if (CollUtil.isNotEmpty(cpslist)) {
                for (int i = 0; i < cpslist.size(); i++) {
                    IotPoint cpsPint = new IotPoint();
                    cpsPint.setMeasurement("fnwl");
                    cpsPint.setTimestamp(System.currentTimeMillis() / 1000);
                    cpsPint.getTags().put("name", map.get("TID") + "_CPS_" + i);
                    cpsPint.getFields().put("value", cpslist.get(i));
                    pointList.add(cpsPint);
                }
            }

            List<Integer> cnsList = IotUtils.safeParseIntegerList(map, "CNS");
            if (CollUtil.isNotEmpty(cnsList)) {
                for (int i = 0; i < cnsList.size(); i++) {
                    IotPoint point = new IotPoint();
                    point.setMeasurement("fnwl");
                    point.setTimestamp(System.currentTimeMillis() / 1000);
                    point.getTags().put("name", map.get("TID") + "_CNS_" + i);
                    point.getFields().put("value", cnsList.get(i));
                    pointList.add(point);
                }
            }
            List<Integer> mnList = IotUtils.safeParseIntegerList(map, "MN");
            if (CollUtil.isNotEmpty(mnList)) {
                for (int i = 0; i < mnList.size(); i++) {
                    IotPoint point = new IotPoint();
                    point.setMeasurement("fnwl");
                    point.setTimestamp(System.currentTimeMillis() / 1000);
                    point.getTags().put("name", map.get("TID") + "_MN_" + i);
                    point.getFields().put("value", mnList.get(i));
                    pointList.add(point);
                }
            }

            List<Integer> relayList = IotUtils.safeParseIntegerList(map, "RELAY");
            if (CollUtil.isNotEmpty(relayList)) {
                for (int i = 0; i < relayList.size(); i++) {
                    IotPoint point = new IotPoint();
                    point.setMeasurement("fnwl");
                    point.setTimestamp(System.currentTimeMillis() / 1000);
                    point.getTags().put("name", map.get("TID") + "_RELAY_" + i);
                    point.getFields().put("value", relayList.get(i));
                    pointList.add(point);
                }
            }

            List<Double> chvList = IotUtils.safeParseDoubleList(map, "CHV");
            if (CollUtil.isNotEmpty(chvList)) {
                for (int i = 0; i < chvList.size(); i++) {
                    IotPoint point = new IotPoint();
                    point.setMeasurement("fnwl");
                    point.setTimestamp(System.currentTimeMillis() / 1000);
                    point.getTags().put("name", map.get("TID") + "_CHV_" + i);
                    point.getFields().put("value", chvList.get(i));
                    pointList.add(point);
                }
            }

            List<Double> chaList = IotUtils.safeParseDoubleList(map, "CHA");
            if (CollUtil.isNotEmpty(chaList)) {
                for (int i = 0; i < chaList.size(); i++) {
                    IotPoint point = new IotPoint();
                    point.setMeasurement("fnwl");
                    point.setTimestamp(System.currentTimeMillis() / 1000);
                    point.getTags().put("name", map.get("TID") + "_CHA_" + i);
                    point.getFields().put("value", chaList.get(i));
                    pointList.add(point);
                }
            }

            List<Double> pwerList = IotUtils.safeParseDoubleList(map, "POWER");
            if (CollUtil.isNotEmpty(pwerList)) {
                for (int i = 0; i < pwerList.size(); i++) {
                    IotPoint point = new IotPoint();
                    point.setMeasurement("fnwl");
                    point.setTimestamp(System.currentTimeMillis() / 1000);
                    point.getTags().put("name", map.get("TID") + "_POWER_" + i);
                    point.getFields().put("value", pwerList.get(i));
                    pointList.add(point);
                }
            }

            List<Double> elecList = IotUtils.safeParseDoubleList(map, "ELEC");
            if (CollUtil.isNotEmpty(elecList)) {
                for (int i = 0; i < elecList.size(); i++) {
                    IotPoint point = new IotPoint();
                    point.setMeasurement("fnwl");
                    point.setTimestamp(System.currentTimeMillis() / 1000);
                    point.getTags().put("name", map.get("TID") + "_ELEC_" + i);
                    point.getFields().put("value", elecList.get(i));
                    pointList.add(point);
                }
            }


            // 交流/直流供电
            if(ObjectUtil.isNotNull(IotUtils.safeParseDouble(map, "V"))){
                IotPoint vPoint = new IotPoint();
                vPoint.setMeasurement("fnwl");
                vPoint.setTimestamp(System.currentTimeMillis() / 1000);
                vPoint.getTags().put("name", map.get("TID") + "_V");
                vPoint.getFields().put("value", IotUtils.safeParseDouble(map, "V"));
                pointList.add(vPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseDouble(map, "A"))){
                IotPoint aPoint = new IotPoint();
                aPoint.setMeasurement("fnwl");
                aPoint.setTimestamp(System.currentTimeMillis() / 1000);
                aPoint.getTags().put("name", map.get("TID") + "_A");
                aPoint.getFields().put("value", IotUtils.safeParseDouble(map, "A"));
                pointList.add(aPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseDouble(map, "H"))){
                IotPoint hPoint = new IotPoint();
                hPoint.setMeasurement("fnwl");
                hPoint.setTimestamp(System.currentTimeMillis() / 1000);
                hPoint.getTags().put("name", map.get("TID") + "_H");
                hPoint.getFields().put("value", IotUtils.safeParseDouble(map, "H"));
                pointList.add(hPoint);
            }

            if(ObjectUtil.isNotNull( IotUtils.safeParseDouble(map, "T"))) {
                IotPoint tPoint = new IotPoint();
                tPoint.setMeasurement("fnwl");
                tPoint.setTimestamp(System.currentTimeMillis() / 1000);
                tPoint.getTags().put("name", map.get("TID") + "_T");
                tPoint.getFields().put("value", IotUtils.safeParseDouble(map, "T"));
                pointList.add(tPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseDouble(map, "APOWER"))) {
                IotPoint apowerPoint = new IotPoint();
                apowerPoint.setMeasurement("fnwl");
                apowerPoint.setTimestamp(System.currentTimeMillis() / 1000);
                apowerPoint.getTags().put("name", map.get("TID") + "_APOWER");
                apowerPoint.getFields().put("value", IotUtils.safeParseDouble(map, "APOWER"));
                pointList.add(apowerPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseDouble(map, "AKW"))) {
                IotPoint akwPoint = new IotPoint();
                akwPoint.setMeasurement("fnwl");
                akwPoint.setTimestamp(System.currentTimeMillis() / 1000);
                akwPoint.getTags().put("name", map.get("TID") + "_AKW");
                akwPoint.getFields().put("value", IotUtils.safeParseDouble(map, "AKW"));
                pointList.add(akwPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "DS"))) {
                IotPoint dsPoint = new IotPoint();
                dsPoint.setMeasurement("fnwl");
                dsPoint.setTimestamp(System.currentTimeMillis() / 1000);
                dsPoint.getTags().put("name", map.get("TID") + "_DS");
                dsPoint.getFields().put("value", IotUtils.safeParseInteger(map, "DS"));
                pointList.add(dsPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "P"))) {
                IotPoint pPoint = new IotPoint();
                pPoint.setMeasurement("fnwl");
                pPoint.setTimestamp(System.currentTimeMillis() / 1000);
                pPoint.getTags().put("name", map.get("TID") + "_P");
                pPoint.getFields().put("value", IotUtils.safeParseInteger(map, "P"));
                pointList.add(pPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "SPD"))) {
                IotPoint spdPoint = new IotPoint();
                spdPoint.setMeasurement("fnwl");
                spdPoint.setTimestamp(System.currentTimeMillis() / 1000);
                spdPoint.getTags().put("name", map.get("TID") + "_SPD");
                spdPoint.getFields().put("value", IotUtils.safeParseInteger(map, "SPD"));
                pointList.add(spdPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "PA"))) {
                IotPoint paPoint = new IotPoint();
                paPoint.setMeasurement("fnwl");
                paPoint.setTimestamp(System.currentTimeMillis() / 1000);
                paPoint.getTags().put("name", map.get("TID") + "_PA");
                paPoint.getFields().put("value", IotUtils.safeParseInteger(map, "PA"));
                pointList.add(paPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "LES"))) {
                IotPoint lesPoint = new IotPoint();
                lesPoint.setMeasurement("fnwl");
                lesPoint.setTimestamp(System.currentTimeMillis() / 1000);
                lesPoint.getTags().put("name", map.get("TID") + "_LES");
                lesPoint.getFields().put("value", IotUtils.safeParseInteger(map, "LES"));
                pointList.add(lesPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "OV"))) {
                IotPoint ovPoint = new IotPoint();
                ovPoint.setMeasurement("fnwl");
                ovPoint.setTimestamp(System.currentTimeMillis() / 1000);
                ovPoint.getTags().put("name", map.get("TID") + "_OV");
                ovPoint.getFields().put("value", IotUtils.safeParseInteger(map, "OV"));
                pointList.add(ovPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "OCPS"))) {
                IotPoint ocpsPoint = new IotPoint();
                ocpsPoint.setMeasurement("fnwl");
                ocpsPoint.setTimestamp(System.currentTimeMillis() / 1000);
                ocpsPoint.getTags().put("name", map.get("TID") + "_OCPS");
                ocpsPoint.getFields().put("value", IotUtils.safeParseInteger(map, "OCPS"));
                pointList.add(ocpsPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "BAT"))) {
                IotPoint batPoint = new IotPoint();
                batPoint.setMeasurement("fnwl");
                batPoint.setTimestamp(System.currentTimeMillis() / 1000);
                batPoint.getTags().put("name", map.get("TID") + "_BAT");
                batPoint.getFields().put("value", IotUtils.safeParseInteger(map, "BAT"));
                pointList.add(batPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "SIM"))) {
                IotPoint simPoint = new IotPoint();
                simPoint.setMeasurement("fnwl");
                simPoint.setTimestamp(System.currentTimeMillis() / 1000);
                simPoint.getTags().put("name", map.get("TID") + "_SIM");
                simPoint.getFields().put("value", IotUtils.safeParseInteger(map, "SIM"));
                pointList.add(simPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "WATER"))) {
                IotPoint waterPoint = new IotPoint();
                waterPoint.setMeasurement("fnwl");
                waterPoint.setTimestamp(System.currentTimeMillis() / 1000);
                waterPoint.getTags().put("name", map.get("TID") + "_WATER");
                waterPoint.getFields().put("value", IotUtils.safeParseInteger(map, "WATER"));
                pointList.add(waterPoint);
            }

            if(ObjectUtil.isNotNull(IotUtils.safeParseInteger(map, "MIU"))) {
                IotPoint minuPoint = new IotPoint();
                minuPoint.setMeasurement("fnwl");
                minuPoint.setTimestamp(System.currentTimeMillis() / 1000);
                minuPoint.getTags().put("name", map.get("TID") + "_MIU");
                minuPoint.getFields().put("value", IotUtils.safeParseInteger(map, "MIU"));
                pointList.add(minuPoint);
            }

            pointData.setList(pointList);
            pointData.setTid(map.get("TID"));
            return pointData;
        }
        return null;
    }


    public static void main(String[] args) {
        String msg = "##0163QN=20200730160101008;TID=123456;VER=11;CP=&&DT=20200730160101;CPS=1,0,2;CNS=1,0,2,1,1,0;MN=1,1;V=225;A=235;H=50.5;T=-20.5;DS=1;P=50;SPD=1;PA=0;FES=0;LES=1;LA=200;HES=1&&be##";
        String msg2 = "##0427QN=-0000001-00000001;TID=660968;VER=11;CP=&&DT=20250108103624;CPS=1;CNS=1,1,1,1,1,1;MN=1,2;V=227.39;A=34.77;H=8.31;T=30.12;DS=1;P=100;SPD=0;PA=1;LES=1;OV=0;OCPS=0;BAT=0;SIM=1;POWER=1,2,3,4,4,6,5,4;WATER=1;APOWER=7.32;AKW=0.01;RELAY=2,1,1,1,1,1,1,1;CHV=227.39,227.39,227.39,227.39,227.39,227.39,227.39,227.39;CHA=34.75,0.03,0.15,0.11,0.21,0.24,0.44,1.27;ELEC=0.01,0.00,0.00,0.00,0.00,0.00,0.00,0.00;ur=0&&34##";
        ConvertHandler.convertReportData(msg2);
    }
}
