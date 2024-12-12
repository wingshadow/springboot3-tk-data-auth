package com.hawk.controller.system.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hawk.framework.common.core.form.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-12-12 10:51
 */
@Data
public class SysMenuForm extends BaseForm {
    /**
     * 菜单ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long menuId;

    /**
     * 菜单名称
     */
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过{max}个字符")
    private String menuName;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @Size(min = 0, max = 200, message = "路由地址不能超过{max}个字符")
    private String path;

    /**
     * 组件路径
     */
    @Size(min = 0, max = 200, message = "组件路径不能超过{max}个字符")
    private String component;

    /**
     * 路由参数
     */
    private String queryParam;

    /**
     * 是否为外链（1是 0否）
     */
    private String isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    private String isCache;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    /**
     * 显示状态（1显示 0隐藏）
     */
    private String visible;

    /**
     * 菜单状态（1正常 0停用）
     */
    private String status;

    /**
     * 权限字符串
     */
    @Size(min = 0, max = 100, message = "权限标识长度不能超过{max}个字符")
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;
}
