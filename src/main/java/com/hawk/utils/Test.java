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
    public static void main(String[] args) {
//        System.out.println(BCrypt.hashpw("123456"));
        System.out.println(Validator.isNumber("2"));
    }
}
