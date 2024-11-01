package com.hawk.framework.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-05-16 14:17
 */
@Getter
@AllArgsConstructor
public enum LoginWay {

    PC("PC"),

    App("App"),

    Mobile("mobile"),

    WeChat("WeChat");

    private final String loginType;

}
