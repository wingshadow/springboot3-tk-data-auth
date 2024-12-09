package com.hawk.biz.customer.service;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.hawk.biz.customer.entity.Customer;
import com.hawk.biz.customer.mapper.CustomerMapper;
import com.hawk.framework.base.BaseServiceImpl;
import com.hawk.system.entity.SysDictData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author hawk
 * @date 2024-12-06
 */
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer> implements CustomerService {

    private final CustomerMapper baseMapper;


    @Override
    public PageInfo<Customer> selectPageList(Customer customer, int pageNum, int pageSize) {
        Example example = new Example(SysDictData.class);
        PageMethod.startPage(pageNum,pageSize);
        List<Customer> list = baseMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public List<Customer> selectList(Customer customer) {
        Example example = new Example(SysDictData.class);
        List<Customer> list = baseMapper.selectByExample(example);
        return list;
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        baseMapper.deleteBatch(ids);
    }

    @Override
    public int updateBatchSelective(List<Customer> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    public int updateBatchSelectiveLimit(List<Customer> list, int size) {
        return baseMapper.updateBatchSelectiveLimit(list,size);
    }

    @Override
    public int insertAllFieldBatch(List<Customer> list) {
        return baseMapper.insertAllFieldBatch(list);
    }

    @Override
    public int deleteLogicBatch(List<Long> ids) {
        return baseMapper.deleteLogicBatch(ids);
    }
}
