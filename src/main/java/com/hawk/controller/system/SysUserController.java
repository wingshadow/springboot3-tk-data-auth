package com.hawk.controller.system;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.hawk.controller.system.form.SysUserForm;
import com.hawk.controller.system.form.UserAddForm;
import com.hawk.framework.base.BaseController;
import com.hawk.framework.entity.SysDept;
import com.hawk.framework.entity.SysRole;
import com.hawk.framework.entity.SysUser;
import com.hawk.framework.helper.LoginHelper;
import com.hawk.framework.web.resp.R;
import com.hawk.system.service.SysDeptService;
import com.hawk.system.service.SysRoleService;
import com.hawk.system.service.SysUserService;
import com.hawk.utils.StreamUtils;
import com.hawk.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2023-02-14 15:06
 */
@RestController
@RequestMapping(value = "/system/user")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysDeptService deptService;

    @GetMapping(value = "/list")
    private R<PageInfo<SysUser>> list(SysUserForm form) {
        SysUser sysUser = BeanUtil.copyProperties(form, SysUser.class);
        return R.ok(userService.selectPageUserList(sysUser, form.getPageSize(), form.getPageNum()));
    }

    @GetMapping(value = {"/", "/{userId}"})
    public R<Map<String, Object>> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        Map<String, Object> ajax = new HashMap<>();
        userService.checkUserDataScope(userId);
        List<SysRole> roles = roleService.getAllRoleList();
        ajax.put("roles", LoginHelper.isAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isAdmin()));
        if (ObjectUtil.isNotNull(userId)) {
            SysUser sysUser = userService.selectByPrimaryKey(userId);
            ajax.put("user", sysUser);
            ajax.put("roleIds", StreamUtils.toList(roleService.getRoleByUserId(userId), SysRole::getRoleId));
        }
        return R.ok(ajax);
    }

    @PostMapping
    public R<Void> add(@Validated @RequestBody UserAddForm form) {
        SysUser user = BeanUtil.copyProperties(form, SysUser.class);
        if (!userService.checkUserAccountUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getMobile()) && !userService.checkPhoneUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return toAjax(userService.insert(user));
    }

    @PutMapping
    public R<Void> edit(@Validated @RequestBody UserAddForm form) {
        SysUser user = BeanUtil.copyProperties(form, SysUser.class);
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (!userService.checkUserAccountUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getMobile()) && !userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        return toAjax(userService.updateByPrimaryKeySelective(user));
    }

    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        if (ArrayUtil.contains(userIds, getUserId())) {
            return R.fail("当前用户不能删除");
        }
        List<Long> idList = Arrays.asList(userIds);
        userService.deleteByPrimaryKey(idList);
        return R.ok();
    }

    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return toAjax(userService.resetPwd(user));
    }

    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        return toAjax(userService.updateUserStatus(user));
    }

    @GetMapping("/authRole/{userId}")
    public R<Map<String, Object>> authRole(@PathVariable Long userId) {
        SysUser user = userService.selectByPrimaryKey(userId);
        List<SysRole> roles = roleService.getRoleByUserId(userId);
        Map<String, Object> ajax = new HashMap<>();
        ajax.put("user", user);
        ajax.put("roles", LoginHelper.isAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isAdmin()));
        return R.ok(ajax);
    }

    @PutMapping("/authRole")
    public R<Void> insertAuthRole(Long userId, Long[] roleIds) {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return R.ok();
    }

    @GetMapping("/deptTree")
    public R<List<Tree<String>>> deptTree(SysDept dept) {
        if (dept == null) {
            dept = LoginHelper.getLoginUser().getDept();
        }
        return R.ok(deptService.selectDeptTreeList(dept));
    }
}