package com.hawk.framework.annotation.scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-04-28 10:44
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataScope {
    /**
     * 部门表别名
     * @return
     */
    String deptAlias() default "";

    /**
     * 用户表别名
     * @return
     */
    String userAlias() default "";
}
