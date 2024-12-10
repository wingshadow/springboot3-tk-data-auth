package com.hawk.framework.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
        return baseMapper.selectOne(var);
    }

    @Override
    public List<T> select(T var1) {
        return baseMapper.select(var1);
    }

    @Override
    public List<T> selectAll() {
        return baseMapper.selectAll();
    }

    @Override
    public T selectByPrimaryKey(Long var1) {
        return baseMapper.selectByPrimaryKey(var1);
    }

    @Override
    public int insert(T var1) {
        return baseMapper.insert(var1);
    }

    @Override
    public int insertSelective(T var1) {
        return baseMapper.insertSelective(var1);
    }

    @Override
    public int updateByPrimaryKey(T var1) {
        return baseMapper.updateByPrimaryKey(var1);
    }

    @Override
    public int updateByPrimaryKeySelective(T var1) {
        return baseMapper.updateByPrimaryKeySelective(var1);
    }

    @Override
    public int delete(T var1) {
        return baseMapper.delete(var1);
    }

    @Override
    public int deleteByPrimaryKey(Long var1) {
        return baseMapper.deleteByPrimaryKey(var1);
    }

    public void deleteBatchByPrimaryKeys(Long[] ids) {
        baseMapper.deleteBatch(Arrays.asList(ids));
    }

    public void deleteLogicBatchByPrimaryKeys(Long[] ids){
        baseMapper.deleteLogicBatch(Arrays.asList(ids));
    }
}
