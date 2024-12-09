package com.hawk.biz.customer.entity;

import com.hawk.framework.common.core.base.BaseDataEntity;
import com.hawk.framework.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tk.mybatis.mapper.annotation.LogicDelete;


/**
 * 【请填写功能名称】对象 t_customer
 *
 * @author hawk
 * @date 2024-12-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_customer")
public class Customer extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 客户主键
     */
    @Id
    private Long csrId;
    /**
     * 客户姓名
     */
    private String name;
    /**
     * 删除：0-正常 1-删除
     */
    @LogicDelete
    private Integer isDeleted;

}
