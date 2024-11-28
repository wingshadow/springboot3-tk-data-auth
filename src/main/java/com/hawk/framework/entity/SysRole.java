package com.hawk.framework.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hawk.framework.common.constant.UserConstants;
import com.hawk.framework.common.core.base.BaseEntity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-04-26 15:14
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sys_role")
public class SysRole extends BaseEntity {

    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roleId;

    private String roleKey;

    private String roleName;

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

    private String status;

    @Transient
    private Long[] menuIds;

    @Transient
    private Long[] deptIds;

    private int delFlag;

    public SysRole(Long roleId){
        this.roleId = roleId;
    }
    public boolean isAdmin() {
        return UserConstants.ADMIN_ID.equals(this.roleId);
    }
}
