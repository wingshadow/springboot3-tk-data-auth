package com.hawk.utils.iot;

import com.hawk.iot.common.ProtocolType;
import com.hawk.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-27 13:39
 */
public class IotUtils {

    /**
     * 检查消息head判断消息类型
     *
     * @param data
     * @return
     */
    public static int checkCmdHead(byte[] data) {
        if (data == null || data.length < 2) {
            return ProtocolType.UNKNOWN;
        }

        int first = Byte.toUnsignedInt(data[0]);
        int second = Byte.toUnsignedInt(data[1]);

        if (first == ProtocolType.HEART_HEAD && second == ProtocolType.HEART_HEAD) {
            return ProtocolType.HEARTBEAT;
        } else if (first == ProtocolType.REPORT_HEAD && second == ProtocolType.REPORT_HEAD) {
            return ProtocolType.DATA_REPORT;
        }

        return ProtocolType.UNKNOWN;
    }

    /**
     * 解析字符串，支持CP=&&...&&包裹内容，分号分隔的key=value格式
     * @param input 输入字符串
     * @return key-value映射
     */
    public static Map<String, String> parseKeyValueString(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        // 找到 CP=&& 标志
        int cpIndex = input.indexOf("CP=&&");
        int endDoubleAmp = -1;
        String combined;

        if (cpIndex != -1) {
            // 找 CP=&& 后第一个 && 结束位置
            endDoubleAmp = input.indexOf("&&", cpIndex + 4);
            if (endDoubleAmp != -1) {
                // 抽取前半部分不包含 CP=
                String beforeCP = input.substring(0, cpIndex);
                // 抽取 && 中间内容
                String cpContent = input.substring(cpIndex + 5, endDoubleAmp);
                // 抽取后面部分
                String afterCP = input.substring(endDoubleAmp + 2);
                // 合并去掉起止 &&
                combined = beforeCP + cpContent + afterCP;
            } else {
                // 没有找到结束&&，按原样处理
                combined = input;
            }
        } else {
            combined = input;
        }

        // 用分号分割
        String[] pairs = combined.split(";");

        Map<String, String> keyValueMap = new LinkedHashMap<>();

        for (String pair : pairs) {
            if (pair == null || pair.trim().isEmpty()) {
                continue;
            }
            if (pair.contains("=")) {
                String[] kv = pair.split("=", 2);
                keyValueMap.put(kv[0].trim(), kv[1].trim());
            } else {
                // 不是 key=value 格式，可以按需求忽略或存储空值
                keyValueMap.put(pair.trim(), "");
            }
        }

        return keyValueMap;
    }

    public static List<Integer> safeParseIntegerList(Map<String, String> map, String key) {
        String value = map.get(key);
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public static List<Double> safeParseDoubleList(Map<String, String> map, String key) {
        String value = map.get(key);
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }
    public static Integer safeParseInteger(Map<String, String> map, String key) {
        try {
            String val = map.get(key);
            return StringUtils.isNotBlank(val) ? Integer.valueOf(val.trim()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Double safeParseDouble(Map<String, String> map, String key) {
        try {
            String val = map.get(key);
            return StringUtils.isNotBlank(val) ? Double.valueOf(val.trim()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static int CRC(byte[] source, int offset, int length) {
        int crc = 0x00;       // 初始值
        int poly = 0x07;      // CRC8 多项式 x^8 + x^2 + x + 1（标准 CRC-8）

        for (int i = offset; i < offset + length; i++) {
            crc ^= source[i] & 0xFF; // 逐字节异或

            for (int j = 0; j < 8; j++) {
                if ((crc & 0x80) != 0) {
                    crc = (crc << 1) ^ poly;
                } else {
                    crc <<= 1;
                }
            }
        }

        return crc & 0xFF;
    }


}
