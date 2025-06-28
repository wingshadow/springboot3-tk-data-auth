package com.hawk.utils.iot;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 16进制与2进制转换工具类
 * 
 * @author hawk
 * @version 1.0.0 2017年3月30日 下午2:46:36 created
 */
public class HexUtil {
    
    /**
     * log
     */
    private static final Logger log = LoggerFactory.getLogger(HexUtil.class);
    
    /**
     * 将二进制字节数据转换成16进制字符串
     * 
     * @param byteData 字节数组
     * @return 转换后的16进制字符串表示形式
     */
    public static String encode(byte[] byteData) {
        if (null == byteData) {
            return null;
        }
        
        return new String(Hex.encodeHex(byteData, false));
    }
    
    /**
     * 将16进制的字符串转换成对应的二进制数据
     * 
     * @param hexStr 16进制字符串
     * @return 转换后的字节数组。转换不成功或参数为空则返回null
     */
    public static byte[] decode(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            return new byte[0];
        }
        try {
            return Hex.decodeHex(hexStr.toCharArray());
        } catch (DecoderException e) {
            log.error(e.getMessage(), e);
            return new byte[0];
        }
    }

    public static String strToHexCharCode(String str) {
        StringBuilder hex = new StringBuilder();
        for (char c : str.toCharArray()) {
            // 保证补足两位并大写
            hex.append(String.format("%02X", (int) c));
        }
        return hex.toString();
    }

    public static String intToHex(int value, int width) {
        return String.format("%0" + width + "X", value);
    }

    public static String ipToHex(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        int ipInt = (Integer.parseInt(ip[0]) << 24) |
                (Integer.parseInt(ip[1]) << 16) |
                (Integer.parseInt(ip[2]) << 8)  |
                Integer.parseInt(ip[3]);
        // 使用 long 避免负数补码问题，& 0xFFFFFFFFL 确保无符号转换
        return String.format("%08X", ipInt & 0xFFFFFFFFL);
    }

    public static String macToHex(String mac) {
        String hex = mac.replace(":", "").replace("-", "").toUpperCase();
        long value = Long.parseUnsignedLong(hex, 16);
        return String.format("%012X", value);
    }


}
