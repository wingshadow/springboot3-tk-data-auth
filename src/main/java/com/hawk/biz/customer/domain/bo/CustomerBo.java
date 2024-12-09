package com.hawk.biz.customer.domain.bo;


import com.hawk.framework.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;
import com.hawk.framework.validate.AddGroup;
import com.hawk.framework.validate.EditGroup;


/**
 * 【请填写功能名称】业务对象 t_customer
 *
 * @author hawk
 * @date 2024-12-06
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerBo extends BaseEntity {

    /**
     * 客户主键 修改必填
     */
    @NotNull(message = "客户主键不能为空", groups = { EditGroup.class })
    private Long csrId;

    /**
     * 客户姓名 新增修改必填
     */
    @NotBlank(message = "客户姓名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 删除：0-正常 1-删除 新增修改必填
     */
    @NotNull(message = "删除：0-正常 1-删除不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer isDeleted;


}
