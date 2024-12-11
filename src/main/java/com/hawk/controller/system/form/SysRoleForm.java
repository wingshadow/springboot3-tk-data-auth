package com.hawk.controller.system.form;

import com.hawk.framework.common.core.form.BasePageForm;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-05-21 15:00
 */
@Data
public class SysRoleForm extends BasePageForm {

    private String roleId;

    private String roleName;

    private String roleKey;

    private String dataScope;

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    private Boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）
     */
    private Boolean deptCheckStrictly;

    private Integer sort;

    private Integer status;

    private Long[] menuIds;

    private Long[] deptIds;
}
