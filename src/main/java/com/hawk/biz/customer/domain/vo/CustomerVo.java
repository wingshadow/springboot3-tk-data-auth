package com.hawk.biz.customer.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


/**
 * 【请填写功能名称】视图对象 t_customer
 *
 * @author hawk
 * @date 2024-12-06
 */
@Data
@ExcelIgnoreUnannotated
public class CustomerVo {

    private static final long serialVersionUID = 1L;

    /**
     * 客户主键
     */
    @ExcelProperty(value = "客户主键")
    private Long csrId;

    /**
     * 客户姓名
     */
    @ExcelProperty(value = "客户姓名")
    private String name;

    /**
     * 删除：0-正常 1-删除
     */
    @ExcelProperty(value = "删除：0-正常 1-删除")
    private Integer isDeleted;

}
