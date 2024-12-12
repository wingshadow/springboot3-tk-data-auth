package com.hawk.controller.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hawk.framework.common.core.base.BaseLogicEntity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-12-12 15:52
 */
@Setter
@Getter
public class SysUserVO extends BaseLogicEntity {

    private String userId;

    private String deptId;

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
    private String gender;

    private String mobile;

    private String email;

    private String avatar;

    private String userType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String status;

    private String deptName;
}
