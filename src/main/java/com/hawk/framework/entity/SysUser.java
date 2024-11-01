package com.hawk.framework.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hawk.framework.common.constant.UserConstants;
import com.hawk.framework.common.core.base.BaseDataEntity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2023-02-14 14:52
 */

@Getter
@Setter
@Table(name = "sys_user")
public class SysUser extends BaseDataEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long deptId;

    /**
     * 用户账户
     */
    private String userAccount;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户姓名
     */
    private String userName;

    private String nickName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer gender;

    private String mobile;

    private String email;

    private String avatar;

    private String userType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer delFlag;

    @JsonIgnore
    private List<SysRole> sysRoleList;

    private SysDept dept;

    private List<SysRole> roles;

    @Transient
    private Long roleId;

    public boolean isAdmin() {
        return UserConstants.ADMIN_ID.equals(this.userId);
    }
}