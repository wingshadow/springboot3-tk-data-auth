package com.hawk.framework.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hawk.framework.common.core.base.BaseEntity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-04-26 15:20
 */
@Getter
@Setter
@Table(name = "sys_user_role")
public class SysUserRole extends BaseEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roleId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
}
