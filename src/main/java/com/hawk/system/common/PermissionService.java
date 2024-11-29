package com.hawk.system.common;

import cn.hutool.core.collection.CollUtil;
import com.hawk.system.entity.SysUser;
import com.hawk.system.service.SysMenuService;
import com.hawk.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-23 17:09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    private final SysRoleService roleService;
    private final SysMenuService menuService;

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user) {
        Set<String> roles = new HashSet<>();
        // 管理员拥有所有权限
        if (user.isAdmin()) {
            roles.add("admin");
        } else {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
            // 默认---角色
            if (CollUtil.isEmpty(roles)) {
                roles.add("workflowDefault");
            }
        }
        return roles;
    }

    public Set<String> getMenuPermission(SysUser user) {
        Set<String> perms = new HashSet<>();
        // 管理员拥有所有权限
        if (user.isAdmin()) {
            perms.add("*:*:*");
        } else {
            //
            perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId()));
        }

        return perms;
    }
}
