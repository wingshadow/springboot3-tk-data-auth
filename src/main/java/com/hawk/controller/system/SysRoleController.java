package com.hawk.controller.system;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageInfo;
import com.hawk.controller.system.form.SysRoleForm;
import com.hawk.controller.system.form.SysUserForm;
import com.hawk.framework.base.BaseController;
import com.hawk.framework.entity.SysDept;
import com.hawk.framework.entity.SysRole;
import com.hawk.framework.entity.SysUser;
import com.hawk.framework.entity.SysUserRole;
import com.hawk.framework.web.resp.R;
import com.hawk.system.service.SysDeptService;
import com.hawk.system.service.SysRoleService;
import com.hawk.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-05-21 14:58
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysDeptService deptService;

    @GetMapping("/list")
    public R<PageInfo<SysRole>> list(SysRoleForm form) {
        SysRole sysRole = BeanUtil.copyProperties(form, SysRole.class);
        return R.ok(roleService.selectRoleList(sysRole, form.getPageSize(),form.getPageNum()));
    }

    @GetMapping(value = "/{roleId}")
    public R<SysRole> getInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(roleId);
        return R.ok(roleService.selectByPrimaryKey(roleId));
    }

    @PostMapping
    public R<Void> add(@Validated @RequestBody SysRole role) {
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        return toAjax(roleService.insertRole(role));

    }

    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }

        if (roleService.updateRole(role) > 0) {
            roleService.cleanOnlineUserByRole(role.getRoleId());
            return R.ok();
        }
        return R.fail("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    @PutMapping("/dataScope")
    public R<Void> dataScope(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        return toAjax(roleService.authDataScope(role));
    }

    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        return toAjax(roleService.updateRoleStatus(role));
    }

    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable Long[] roleIds) {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    @GetMapping("/optionselect")
    public R<List<SysRole>> optionselect() {
        return R.ok(roleService.getAllRoleList());
    }

    @GetMapping("/authUser/allocatedList")
    public R<PageInfo<SysUser>> allocatedList(SysUserForm form) {
        SysUser sysUser = BeanUtil.copyProperties(form, SysUser.class);
        return R.ok(userService.selectAllocatedList(sysUser,form.getPageSize(),form.getPageNum()));
    }

    @GetMapping("/authUser/unallocatedList")
    public PageInfo<SysUser> unallocatedList(SysUserForm form) {
        SysUser sysUser = BeanUtil.copyProperties(form, SysUser.class);
        return userService.selectUnallocatedList(sysUser,form.getPageSize(),form.getPageNum());
    }

    @PutMapping("/authUser/cancel")
    public R<Void> cancelAuthUser(@RequestBody SysUserRole userRole) {
        return toAjax(roleService.deleteAuthUser(userRole));
    }

    @PutMapping("/authUser/cancelAll")
    public R<Void> cancelAuthUserAll(Long roleId, Long[] userIds) {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds));
    }

    @PutMapping("/authUser/selectAll")
    public R<Void> selectAuthUserAll(Long roleId, Long[] userIds) {
        roleService.checkRoleDataScope(roleId);
        return toAjax(roleService.insertAuthUsers(roleId, userIds));
    }

    @GetMapping(value = "/deptTree/{roleId}")
    public R<Map<String, Object>> roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
        Map<String, Object> ajax = new HashMap<>();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("depts", deptService.selectDeptTreeList(new SysDept()));
        return R.ok(ajax);
    }
}
