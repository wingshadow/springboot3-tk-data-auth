package com.hawk.framework.base;


import com.hawk.framework.plus.batch.insert.BatchInsertMapper;
import com.hawk.framework.plus.batch.update.BatchUpdateMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-17 15:02
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T>, BatchInsertMapper<T>, BatchUpdateMapper<T> {
}
