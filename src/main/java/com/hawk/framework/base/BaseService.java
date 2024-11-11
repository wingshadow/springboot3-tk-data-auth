package com.hawk.framework.base;

import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-17 14:06
 */
public interface BaseService <T>{
    T selectOne(T var);

    List<T> select(T var1);

    List<T> selectAll();

    T selectByPrimaryKey(Long var1);

    int insert(T var1);

    int insertSelective(T var1);

    int updateByPrimaryKey(T var1);

    int updateByPrimaryKeySelective(T var1);

    int delete(T var1);

    int deleteByPrimaryKey(Long var1);

    void deleteBatchByPrimaryKeys(List<Long> ids);
}
