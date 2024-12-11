package com.hawk.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.hawk.framework.common.constant.UserConstants;
import com.hawk.framework.base.BaseServiceImpl;
import com.hawk.system.entity.*;
import com.hawk.system.mapper.SysRoleDeptMapper;
import com.hawk.system.mapper.SysRoleMapper;
import com.hawk.system.mapper.SysRoleMenuMapper;
import com.hawk.system.mapper.SysUserRoleMapper;
import com.hawk.system.service.SysRoleService;
import com.hawk.utils.CriteriaUtils;
import com.hawk.utils.StreamUtils;
import com.hawk.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-04-26 15:10
 */
@Service
public class SysRoleServiceImpl extends BaseServiceImpl<SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysRoleDeptMapper roleDeptMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public List<SysRole> getAllRoleList() {
        return sysRoleMapper.getAllRoleList();
    }

    @Override
    public List<SysDept> selectDeptByRoleId(Long roleId) {
        return sysRoleMapper.selectDeptByRoleId(roleId);
    }

    @Override
    public Set<String> getRolePermission(SysUser user) {
        Set<String> roles = new HashSet<>();
        if (user.isAdmin()) {
            roles.add("admin");
        } else {
            List<SysRole> perms = this.getRoleByUserId(user.getUserId());
            for (SysRole perm : perms) {
                if (ObjectUtil.isNotNull(perm)) {
                    roles.addAll(StringUtils.splitList(perm.getRoleName().trim()));
                }
            }
        }
        return roles;
    }

    @Override
    public List<SysRole> getRoleByUserId(Long userId) {
        return sysRoleMapper.selectRoleByUserId(userId);
    }

    private Example buildQueryExample(SysRole sysRole) {
        Example example = new Example(SysRole.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria)
                .eq(SysRole::getIsDeleted, UserConstants.USER_RETAIN)
                .eq(ObjectUtil.isNotNull(sysRole.getRoleId()), SysRole::getRoleId, sysRole.getRoleId())
                .like(StringUtils.isNotBlank(sysRole.getRoleName()), SysRole::getRoleName, sysRole.getRoleName());
        return example;
    }

    @Override
    public PageInfo<SysRole> selectRoleList(SysRole sysRole, int pageSize, int pageNum) {
        PageMethod.startPage(pageNum, pageSize);
        Example example = buildQueryExample(sysRole);
        List<SysRole> list = sysRoleMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public List<SysRole> selectRoleList(SysRole sysRole) {
        Example example = buildQueryExample(sysRole);
        return sysRoleMapper.selectByExample(example);
    }

    @Override
    public void checkRoleDataScope(Long roleId) {
        if (!UserConstants.ADMIN_ID.equals(1L)) {
            SysRole role = new SysRole();
            role.setRoleId(roleId);
            List<SysRole> roles = this.selectRoleList(role);
            if (CollUtil.isEmpty(roles)) {
                throw new RuntimeException("没有权限访问角色数据！");
            }
        }
    }

    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        Example example = new Example(SysRole.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).eq(SysRole::getRoleName, role.getRoleName())
                .ne(ObjectUtil.isNotNull(role.getRoleId()), SysRole::getRoleId, role.getRoleId());
        boolean exist = sysRoleMapper.exists(example);
        return !exist;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        Example example = new Example(SysRole.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).eq(SysRole::getRoleName, role.getRoleName())
                .ne(ObjectUtil.isNotNull(role.getRoleId()), SysRole::getRoleId, role.getRoleId());
        boolean exist = sysRoleMapper.exists(example);
        return !exist;
    }

    @Override
    public void checkRoleAllowed(SysRole role) {
        if (ObjectUtil.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new RuntimeException("不允许操作超级管理员角色");
        }
    }

    @Override
    public int insertRole(SysRole role) {
        // 新增角色信息
        sysRoleMapper.insertSelective(role);
        return insertRoleMenu(role);
    }

    public int insertRoleMenu(SysRole role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            rows = roleMenuMapper.insertList(list) > 0 ? list.size() : 0;
        }
        return rows;
    }

    public int insertRoleDept(SysRole role) {
        int rows = 1;
        // 新增角色与部门（数据权限）管理
        List<SysRoleDept> list = new ArrayList<SysRoleDept>();
        for (Long deptId : role.getDeptIds()) {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId());
            rd.setDeptId(deptId);
            list.add(rd);
        }
        if (list.size() > 0) {
            rows = roleDeptMapper.insertList(list) >0? list.size() : 0;
        }
        return rows;
    }

    @Override
    public int updateRole(SysRole role) {
        // 修改角色信息
        sysRoleMapper.updateByPrimaryKeySelective(role);
        // 删除角色与菜单关联
        Example example = new Example(SysRoleMenu.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).eq(SysRoleMenu::getRoleId, role.getRoleId());
        roleMenuMapper.deleteByExample(example);
        return insertRoleMenu(role);
    }

    @Override
    public int updateRoleStatus(SysRole role) {
        return sysRoleMapper.updateByPrimaryKey(role);
    }

    @Override
    public int authDataScope(SysRole role) {
        // 修改角色信息
        sysRoleMapper.updateByPrimaryKey(role);
        // 删除角色与部门关联
        Example example = new Example(SysRoleDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).eq(SysRoleDept::getRoleId, role.getRoleId());
        roleDeptMapper.deleteByExample(example);
        // 新增角色和部门信息（数据权限）
        return insertRoleDept(role);
    }

    @Override
    public int deleteRoleById(Long roleId) {
        Example example = new Example(SysRoleMenu.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).eq(SysRoleMenu::getRoleId, roleId);
        // 删除角色与菜单关联
        roleMenuMapper.deleteByExample(example);
        // 删除角色与部门关联
        Example example2 = new Example(SysRoleDept.class);
        Example.Criteria criteria2 = example.createCriteria();
        CriteriaUtils.builder(criteria2).eq(SysRoleMenu::getRoleId, roleId);
        roleDeptMapper.deleteByExample(example2);
        return sysRoleMapper.deleteByPrimaryKey(roleId);
    }

    @Override
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new SysRole(roleId));
            checkRoleDataScope(roleId);
            SysRole role = sysRoleMapper.selectByPrimaryKey(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new RuntimeException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        List<Long> ids = Arrays.asList(roleIds);

        // 删除角色与菜单关联
        Example example = new Example(SysRoleMenu.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).in(SysRoleMenu::getRoleId, ids);
        roleMenuMapper.deleteByExample(example);
        // 删除角色与部门关联
        Example example2 = new Example(SysRoleDept.class);
        Example.Criteria criteria2 = example.createCriteria();
        CriteriaUtils.builder(criteria2).in(SysRoleMenu::getRoleId, ids);
        roleDeptMapper.deleteByExample(example2);

        Example example3 = new Example(SysRole.class);
        Example.Criteria criteria3 = example3.createCriteria();
        CriteriaUtils.builder(criteria3).in(SysRoleMenu::getRoleId, ids);
        return sysRoleMapper.deleteByExample(example3);
    }

    @Override
    public long countUserRoleByRoleId(Long roleId) {
        Example example = new Example(SysUserRole.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).eq(SysUserRole::getRoleId, roleId);
        return userRoleMapper.selectCountByExample(example);
    }

    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRole> perms = sysRoleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (ObjectUtil.isNotNull(perm)) {
                permsSet.addAll(StringUtils.splitList(perm.getRoleKey().trim()));
            }
        }
        return permsSet;
    }

    @Override
    public int deleteAuthUser(SysUserRole userRole) {
        Example example = new Example(SysUserRole.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).eq(SysUserRole::getRoleId, userRole.getRoleId()).eq(SysUserRole::getUserId, userRole.getUserId());
        int rows = userRoleMapper.deleteByExample(example);
        if (rows > 0) {
            cleanOnlineUserByRole(userRole.getRoleId());
        }
        return rows;
    }

    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        Example example = new Example(SysUserRole.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).eq(SysUserRole::getRoleId, roleId).in(SysUserRole::getUserId, Arrays.asList(userIds));
        int rows = userRoleMapper.deleteByExample(example);
        if (rows > 0) {
            cleanOnlineUserByRole(roleId);
        }
        return rows;
    }

    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds) {
        // 新增用户与角色管理
        int rows = 1;
        List<SysUserRole> list = StreamUtils.toList(Arrays.asList(userIds), userId -> {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            return ur;
        });
        if (CollUtil.isNotEmpty(list)) {
            rows = userRoleMapper.insertList(list)>0 ? list.size() : 0;
        }
        if (rows > 0) {
            cleanOnlineUserByRole(roleId);
        }
        return rows;
    }

    @Override
    public void cleanOnlineUserByRole(Long roleId) {
//        List<String> keys = StpUtil.searchTokenValue("", 0, -1, false);
//        if (CollUtil.isEmpty(keys)) {
//            return;
//        }
//        // 角色关联的在线用户量过大会导致redis阻塞卡顿 谨慎操作
//        keys.parallelStream().forEach(key -> {
//            String token = StringUtils.substringAfterLast(key, ":");
//            // 如果已经过期则跳过
//            if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
//                return;
//            }
//            LoginUser loginUser = LoginHelper.getLoginUser(token);
//            if (loginUser.getRoles().stream().anyMatch(r -> r.getRoleId().equals(roleId))) {
//                try {
//                    StpUtil.logoutByTokenValue(token);
//                } catch (NotLoginException ignored) {
//                }
//            }
//        });
    }


}
