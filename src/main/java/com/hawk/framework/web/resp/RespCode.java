package com.hawk.framework.web.resp;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2023-02-14 15:08
 */
public enum RespCode {

    SUCCESS(200, "成功"),
    FAIL(500, "内部错误");
    /**
     * 状态码
     */
    private Integer value;
    /**
     * 状态描述
     */
    private String msg;

    RespCode(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer code) {
        this.value = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}