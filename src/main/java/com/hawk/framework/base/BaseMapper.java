package com.hawk.framework.base;


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-17 15:02
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
