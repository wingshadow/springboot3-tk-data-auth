package com.hawk.utils;

import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.digest.BCrypt;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-30 14:58
 */
public class Test {
    private static String pad(String hexStr, int length) {
        while (hexStr.length() < length * 2) {
            hexStr = "0" + hexStr;
        }

        return hexStr;
    }

    private static String strToHexCharCode(String str) {
        StringBuilder hex = new StringBuilder();

        for (char c : str.toCharArray()) {
            hex.append(Integer.toHexString(c));
        }

        return hex.toString();
    }

    public static byte[] hexToByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];

        for (int i = 0; i < len; ++i) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }

        return result;
    }

    public static int CRC8(byte[] source, int offset, int length) {
        int wCRCin = 0;
        int wCPoly = 7;
        int i = offset;

        for (int cnt = offset + length; i < cnt; ++i) {
            for (int j = 0; j < 8; ++j) {
                boolean bit = (source[i] >> 7 - j & 1) == 1;
                boolean c07 = (wCRCin >> 7 & 1) == 1;
                wCRCin <<= 1;
                if (c07 ^ bit) {
                    wCRCin ^= wCPoly;
                }
            }
        }

        wCRCin &= 255;
        int var11;
        return var11 = wCRCin ^ 0;
    }

    public static void main(String[] args) {
//        String hexfw = "F0F011";
//        String SBIDfw = Integer.toHexString(660968);
//        String SBID2fw = pad(SBIDfw, 3);
//        String innerServerHost = "all##test1.fnwlw.net:6102";
//        String outerServerHost = "8157.fnwlw.net:6101";
//        String alldata = innerServerHost + "," + outerServerHost;
//        alldata = strToHexCharCode(alldata);
//        int alldatalength = alldata.length() / 2;
//        String alldatalength16 = Integer.toHexString(alldatalength);
//        System.out.println("length hex:"+ alldatalength16 );
//
//        alldatalength16 = pad(alldatalength16, 1);
//        hexfw = hexfw + SBID2fw + "F1FFFFFFFFFFFFFFFF" + alldatalength16 + alldata;
//        String s = "11" + SBID2fw + "F1FFFFFFFFFFFFFFFF" + alldatalength16 + alldata;
//        int ccc = CRC8(hexToByte(s), 0, s.length() / 2);
//        System.out.println(ccc);
//        String cccs = String.format("%2s", Integer.toHexString(ccc)).replace(" ", "0");
//        String zhiling = hexfw + cccs + "FFFF";
//        System.out.println(zhiling);

        double d = 123.456;
        String hexString = Double.toHexString(d);
        System.out.println(hexString);
    }
}
