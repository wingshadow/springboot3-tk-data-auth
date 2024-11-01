package com.hawk.framework.base;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-17 14:06
 */
public class BaseServiceImpl<T> implements BaseService<T> {


    @Autowired
    private BaseMapper<T> baseMapper;

    @Override
    public T selectOne(T var) {
        return null;
    }

    @Override
    public List<T> select(T var1) {
        return null;
    }

    @Override
    public List<T> selectAll() {
        return null;
    }

    @Override
    public T selectByPrimaryKey(Long var1) {
        return null;
    }

    @Override
    public int insert(T var1) {
        return 0;
    }

    @Override
    public int insertSelective(T var1) {
        return 0;
    }

    @Override
    public int updateByPrimaryKey(T var1) {
        return 0;
    }

    @Override
    public int updateByPrimaryKeySelective(T var1) {
        return 0;
    }

    @Override
    public int delete(T var1) {
        return 0;
    }

    @Override
    public int deleteByPrimaryKey(Long var1) {
        return 0;
    }
}
