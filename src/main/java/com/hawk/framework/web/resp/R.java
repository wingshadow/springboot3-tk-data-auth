package com.hawk.framework.web.resp;

import lombok.Data;

import static com.hawk.framework.web.resp.RespCode.SUCCESS;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2023-02-14 15:08
 */
@Data
public class R<T> {
    private int code;

    private String msg;

    private T data;

    public R() {
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R(int code) {
        this.code = code;
    }

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public R(RespCode respCode) {
        this.code = respCode.getValue();
        this.msg = respCode.getMsg();
    }

    public R(RespCode respCode, T data) {
        this.code = respCode.getValue();
        this.msg = respCode.getMsg();
        this.data = data;
    }

    public static <T> R<T> ok() {
        return new R<T>(SUCCESS);
    }

    public static <T> R<T> ok(T data) {
        return new R<T>(SUCCESS, data);
    }

    public static <T> R<T> ok(String msg) {
        return new R<T>( SUCCESS.getValue(), msg,null);
    }
    public static <T> R<T> ok(String msg, T data) {
        return new R<T>(SUCCESS.getValue(), msg, data);
    }

    public static <T> R<T> fail() {
        return new R<T>(RespCode.FAIL);
    }

    public static <T> R<T> fail(String msg) {
        return new R<T>(RespCode.FAIL.getValue(), msg,null);
    }

    public static <T> R<T> fail(RespCode respCode) {
        return new R<T>(respCode);
    }

    public static <T> R<T> fail(int code) {
        return new R<T>(code);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<T>(code, msg);
    }
}