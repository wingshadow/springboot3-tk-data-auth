package com.hawk.iot.builder;

import com.hawk.utils.iot.HexUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-28 08:16
 */
public class CommandBuilder {

    /**
     * 配置服务器IP（域名）、端口
     *
     * @param deviceSn
     * @param domainList
     * @return
     */
    public static String buildServerConfigFrame(int deviceSn, List<String> domainList) {
        String domains = String.join(",", domainList);
        String domainHex = FrameBuilder.strToHex(domains);

        return FrameBuilder.buildFrame(
                "F1",
                deviceSn,
                domainHex
        );
    }

    /**
     * 配置传输模式
     *
     * @param deviceSn
     * @param mode 1-内网 2-外网 3-双服 4-单服
     * @return
     */
    public static String buildTranMode(int deviceSn, int mode) {
        String domainHex = HexUtil.intToHex(mode, 2);
        return FrameBuilder.buildFrame(
                "F3",
                deviceSn,
                domainHex
        );
    }

    /**
     * 配置设备IP\子网掩码\网关
     *
     * @param deviceSn
     * @param ip       192.168.1.1
     * @param mask
     * @param gateway
     * @return
     */
    public static String buildTerminalConfig(int deviceSn, String ip, String mask, String gateway) {
        String ipStr = HexUtil.ipToHex(ip);
        String maskStr = HexUtil.ipToHex(mask);
        String gateStr = HexUtil.ipToHex(gateway);
        return FrameBuilder.buildFrame(
                "F4",
                deviceSn,
                ipStr + maskStr + gateStr
        );
    }

    /**
     * 配置设备MAC
     *
     * @param deviceSn
     * @param mac      00:1A:2B:3C:4D:5E
     * @return
     */
    public static String buildTerminalMac(int deviceSn, String mac) {
        String macStr = HexUtil.macToHex(mac);
        return FrameBuilder.buildFrame(
                "FD",
                deviceSn,
                macStr
        );
    }

    /**
     * 配置摄像机
     *
     * @param deviceSn
     * @param seq      摄像机编号
     * @param ip
     * @return
     */
    public static String buildConfigCamera(int deviceSn, int seq, String ip) {
        String seqStr = HexUtil.intToHex(seq, 2);
        String ipStr = HexUtil.ipToHex(ip);
        return FrameBuilder.buildFrame(
                "F7",
                deviceSn,
                seqStr + ipStr
        );
    }

    /**
     * 配置风扇启停阈值 （温度）
     *
     * @param deviceSn
     * @param startTemper 0-255
     * @param stopTemper 0-255
     * @return
     */
    public static String buildFnTemperThreshold(int deviceSn, int startTemper, int stopTemper) {
        String startStr = HexUtil.intToHex(startTemper, 2);
        String stopStr = HexUtil.intToHex(stopTemper, 2);
        return FrameBuilder.buildFrame(
                "F6",
                deviceSn,
                startStr + stopStr
        );
    }

    /**
     * 配置风扇启停阈值 （湿度）
     *
     * @param deviceSn
     * @param startHumidity 0-255
     * @param stopHumidity 0-255
     * @return
     */
    public static String buildFnHumidityThreshold(int deviceSn, int startHumidity, int stopHumidity) {
        String startStr = HexUtil.intToHex(startHumidity, 2);
        String stopStr = HexUtil.intToHex(stopHumidity, 2);
        return FrameBuilder.buildFrame(
                "F6",
                deviceSn,
                startStr + stopStr
        );
    }

    /**
     * 配置阈值
     *
     * @param deviceSn
     * @param high     高压 0x0000-0xFFFF
     * @param low      低压 0x0000-0xFFFF
     * @param ele      电流毫安 0x0000-0xFFFF
     * @param angle    旋转姿态 0x00-0xFF
     * @param leakage  漏电流 绝缘体中微小电流 0x0000-0xFFFF
     * @return
     */
    public static String buildTerminalThreshold(int deviceSn, int high, int low, int ele, int angle, int leakage) {
        String highStr = HexUtil.intToHex(high, 4);
        String lowStr = HexUtil.intToHex(low, 4);
        String eleStr = HexUtil.intToHex(ele, 4);
        String angleStr = HexUtil.intToHex(angle, 2);
        String leakageStr = HexUtil.intToHex(leakage, 4);
        return FrameBuilder.buildFrame(
                "A4",
                deviceSn,
                highStr + lowStr + eleStr + angleStr + leakageStr
        );
    }

    /**
     * 断电重启
     *
     * @param deviceSn
     * @param camera 0x00-0xFF
     * @return
     */
    public static String buildRestartCamera(int deviceSn, int camera) {
        String seqStr = HexUtil.intToHex(camera, 2);
        return FrameBuilder.buildFrame(
                "DA",
                deviceSn,
                seqStr
        );
    }

    /**
     * 配置主网检测IP
     *
     * @param deviceSn
     * @param ip1
     * @param ip2
     * @return
     */
    public static String buildConfigMainIp(int deviceSn, String ip1, String ip2) {
        String ip1Str = HexUtil.ipToHex(ip1);
        String ip2Str = HexUtil.ipToHex(ip2);
        return FrameBuilder.buildFrame(
                "F8",
                deviceSn,
                ip1Str + ip2Str
        );
    }

    /**
     * 设备重启
     *
     * @param deviceSn
     * @return
     */
    public static String buildRestartTerminal(int deviceSn) {
        String s = HexUtil.intToHex(0, 2);
        return FrameBuilder.buildFrame(
                "F8",
                deviceSn,
                s
        );
    }

    /**
     * 更新系统
     *
     * @param deviceSn
     * @return
     */
    public static String buildUpdateSystem(int deviceSn) {
        String s = HexUtil.intToHex(0, 2);
        return FrameBuilder.buildFrame(
                "B3",
                deviceSn,
                s
        );
    }

    /**
     * 配置搜索方式
     * @param deviceSn
     * @param mod 1-ip搜索 2-网络协议搜索
     * @return
     */
    public static String buildSearchMod(int deviceSn, int mod) {
        String s = HexUtil.intToHex(mod, 2);
        return FrameBuilder.buildFrame(
                "A5",
                deviceSn,
                s
        );
    }

    /**
     * 配置搜索周期
     * @param deviceSn
     * @param duration 0x05-0xFF 5-255
     * @return
     */
    public static String buildSearchModDuration(int deviceSn, int duration) {
        String s = HexUtil.intToHex(duration, 2);
        return FrameBuilder.buildFrame(
                "A5",
                deviceSn,
                s
        );
    }

    /**
     * 查询设备软硬件版本
     * @param deviceSn
     * @return
     */
    public static String buildQuerySystemVersion(int deviceSn) {
        String s = HexUtil.intToHex(0, 2);
        return FrameBuilder.buildFrame(
                "E3",
                deviceSn,
                s
        );
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("all##test1.fnwlw.net:6102");
        list.add("8157.fnwlw.net:6101");

        String cmd = CommandBuilder.buildTerminalMac(660968, "00:1A:2B:3C:4D:5E");
        System.out.println(HexUtil.decode(cmd));
    }
}
