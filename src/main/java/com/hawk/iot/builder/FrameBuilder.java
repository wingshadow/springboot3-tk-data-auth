package com.hawk.iot.builder;

import com.hawk.utils.iot.HexUtil;
import com.hawk.utils.iot.IotUtils;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-28 10:42
 */
public class FrameBuilder {
    private static final String FRAME_HEAD = "F0F0";
    private static final String FRAME_TAIL = "FFFF";
    private static final String FRAME_VERSION = "11";
    private static final String FRAME_RESERVED = "FFFFFFFFFFFFFFFF";

    /**
     * 构建完整帧
     * @param subCmd 子命令码，如"F1"
     * @param deviceSn 设备序列号，整数
     * @param dataPayloadHex 数据部分，已转为 HEX 字符串
     * @return 完整帧的 HEX 字符串
     */
    public static String buildFrame(String subCmd, int deviceSn, String dataPayloadHex) {
        String frameBody = buildFrameBody(FRAME_VERSION, subCmd, FRAME_RESERVED, deviceSn, dataPayloadHex);

        // 计算 CRC
        byte[] bytesForCrc = HexUtil.decode(frameBody);
        int crc = IotUtils.CRC(bytesForCrc, 0, bytesForCrc.length);
        String crcHex = HexUtil.intToHex(crc, 2);

        // 拼接完整帧
        return FRAME_HEAD + frameBody + crcHex + FRAME_TAIL;
    }

    /**
     * 构建帧体（不含帧头帧尾和CRC）
     */
    private static String buildFrameBody(String cmdCode, String subCmd, String reserved, int deviceSn, String dataPayloadHex) {
        StringBuilder sb = new StringBuilder();

        sb.append(cmdCode);
        sb.append(HexUtil.intToHex(deviceSn, 6));
        sb.append(subCmd);
        sb.append(reserved);

        // 长度是 dataPayloadHex 字节长度，2字节HEX表示
        int length = dataPayloadHex.length() / 2;
        sb.append(HexUtil.intToHex(length, 2));

        sb.append(dataPayloadHex);

        return sb.toString();
    }

    /**
     * 将字符串转成HEX ASCII码
     */
    public static String strToHex(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            sb.append(String.format("%02X", (int) c));
        }
        return sb.toString();
    }

}
