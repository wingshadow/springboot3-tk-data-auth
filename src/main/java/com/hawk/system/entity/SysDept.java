package com.hawk.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hawk.framework.common.core.base.TreeEntity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-04-26 15:04
 */
@Getter
@Setter
@Table(name = "sys_dept")
public class SysDept extends TreeEntity<SysDept> {

    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long deptId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    private String deptName;

    private String ancestors;

    private Integer orderNum;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isDeleted;
}
