package com.hawk.biz.customer.service;


import com.github.pagehelper.PageInfo;
import com.hawk.biz.customer.entity.Customer;
import com.hawk.framework.base.BaseService;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 *
 * @author hawk
 * @date 2024-12-06
 */
public interface CustomerService extends BaseService<Customer>  {

    PageInfo<Customer> selectPageList(Customer customer, int pageNum, int pageSize);

    List<Customer> selectList(Customer customer);

    void deleteBatch(List<Long> ids);

    int updateBatchSelective(List<Customer> list);

    int updateBatchSelectiveLimit(List<Customer> list,int size);

    int insertAllFieldBatch(List<Customer> list);

    int deleteLogicBatch(List<Long> ids);
}
