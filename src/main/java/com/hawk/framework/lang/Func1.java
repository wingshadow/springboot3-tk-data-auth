package com.hawk.framework.lang;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-19 08:49
 */
@FunctionalInterface
public interface Func1<T, R> {
    R apply(T t);
}

