package com.hawk.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hawk.framework.common.core.base.TreeEntity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 菜单权限表 sys_menu
 *
 * @author Lion Li
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "sys_menu")
public class SysMenu extends TreeEntity<SysMenu> {

    /**
     * 菜单ID
     */
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
