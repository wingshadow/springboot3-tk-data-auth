package com.hawk.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.hawk.framework.common.constant.UserConstants;
import com.hawk.framework.base.BaseServiceImpl;
import com.hawk.system.entity.SysDept;
import com.hawk.system.entity.SysRole;
import com.hawk.system.entity.SysUser;
import com.hawk.system.entity.SysUserRole;
import com.hawk.framework.helper.DataBaseHelper;
import com.hawk.framework.helper.LoginHelper;
import com.hawk.system.mapper.SysDeptMapper;
import com.hawk.system.mapper.SysUserMapper;
import com.hawk.system.mapper.SysUserRoleMapper;
import com.hawk.system.service.SysUserService;
import com.hawk.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2023-02-14 15:01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends BaseServiceImpl<SysUser> implements SysUserService {
    private final SysUserRoleMapper userRoleMapper;
    private final SysDeptMapper deptMapper;
    private final SysUserMapper baseMapper;

    @Override
    public List<SysUser> getAllUser(SysUser sysUser) {
        return baseMapper.getAllUser(sysUser);
    }

    @Override
    public List<SysRole> selectRoleByUserId(Long userId) {
        return baseMapper.selectRoleByUserId(userId);
    }

    private String buildQueryExample(SysUser user) {
        List<Long> ids = null;
        if (ObjectUtil.isNotNull(user.getDeptId())) {
            Example deptExample = new Example(SysDept.class);
            CriteriaUtils.builder(deptExample.createCriteria())
                    .and(DataBaseHelper.findInSet(user.getDeptId(), "ancestors"));
            List<SysDept> deptList = deptMapper.selectByExample(deptExample);
            ids = StreamUtils.toList(deptList, SysDept::getDeptId);
            ids.add(user.getDeptId());
        }

        Map<String, Object> params = user.getParams();
        Example example = new Example(SysUser.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(SysUser::getDelFlag, UserConstants.USER_RETAIN)
                .eq(ObjectUtil.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId())
                .like(StringUtils.isNotBlank(user.getUserName()), SysUser::getUserName, user.getUserName())
                .eq(ObjectUtil.isNotEmpty(user.getStatus()), SysUser::getStatus, user.getStatus())
                .like(StringUtils.isNotBlank(user.getMobile()), SysUser::getMobile, user.getMobile())
                .between(params.get("beginTime") != null && params.get("endTime") != null,
                        "u.create_time", params.get("beginTime"), params.get("endTime"))
                .in(ObjectUtil.isNotNull(ids), SysUser::getDeptId, ids);
        return SqlBuilder.buildWhereClause(example, "u");
    }

    @Override
    public PageInfo<SysUser> selectPageUserList(SysUser sysUser, int pageNum, int pageSize) {
        String whereCause = buildQueryExample(sysUser);
        PageMethod.startPage(pageNum, pageSize);
        List<SysUser> list = baseMapper.selectUserList(whereCause);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<SysUser> selectAllocatedList(SysUser user, int pageSize, int pageNum) {
        String whereCause = SqlUtils.build().eq("u.del_flag", UserConstants.USER_RETAIN)
                .eq(ObjectUtil.isNotNull(user.getRoleId()), "r.role_id", user.getRoleId())
                .like(StringUtils.isNotBlank(user.getUserName()), "u.user_name", user.getUserName())
                .eq(ObjectUtil.isNotEmpty(user.getStatus()), "u.status", user.getStatus())
                .like(StringUtils.isNotBlank(user.getMobile()), "u.mobile", user.getMobile())
                .toSQL();

        List<SysUser> list = baseMapper.selectAllocatedList(whereCause);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<SysUser> selectUnallocatedList(SysUser user, int pageSize, int pageNum) {
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(user.getRoleId());
        String whereCause = SqlUtils.build().eq("u.del_flag", UserConstants.USER_RETAIN)
                .and("(r.role_id !=" + user.getRoleId() + " OR r.role_id IS NULL)")
                .eq(ObjectUtil.isNotNull(user.getRoleId()), "r.role_id", user.getRoleId())
                .notIn(CollUtil.isNotEmpty(userIds), "u.user_id", userIds)
                .like(StringUtils.isNotBlank(user.getUserAccount()), "u.user_account", user.getUserAccount())
                .like(StringUtils.isNotBlank(user.getMobile()), "u.mobile", user.getMobile()).toSQL();

        PageMethod.startPage(pageNum, pageSize);
        List<SysUser> list = baseMapper.selectUnallocatedList(whereCause);
        return new PageInfo<>(list);
    }

    @Override
    public List<SysUser> selectUserList(SysUser sysUser) {
        String whereCause = buildQueryExample(sysUser);
        return baseMapper.selectUserList(whereCause);
    }

    @Override
    public void checkUserDataScope(Long userId) {
        if (!LoginHelper.isAdmin()) {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = this.selectUserList(user);
            if (CollUtil.isEmpty(users)) {
                throw new RuntimeException("没有权限访问用户数据！");
            }
        }
    }

    @Override
    public boolean checkUserAccountUnique(SysUser user) {
        Example example = new Example(SysUser.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(SysUser::getUserAccount, user.getUserAccount())
                .ne(ObjectUtil.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId());
        boolean exist = baseMapper.exists(example);
        return !exist;
    }

    @Override
    public boolean checkPhoneUnique(SysUser user) {
        Example example = new Example(SysUser.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(SysUser::getMobile, user.getMobile())
                .ne(ObjectUtil.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId());
        boolean exist = baseMapper.exists(example);
        return !exist;
    }

    @Override
    public boolean checkEmailUnique(SysUser user) {
        Example example = new Example(SysUser.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(SysUser::getEmail, user.getEmail())
                .ne(ObjectUtil.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId());
        boolean exist = baseMapper.exists(example);
        return !exist;
    }

    @Override
    public void checkUserAllowed(SysUser user) {
        if (ObjectUtil.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new RuntimeException("不允许操作超级管理员用户");
        }
    }

    @Override
    public int resetPwd(SysUser user) {
        return baseMapper.updateByPrimaryKey(user);
    }

    @Override
    public int updateUserStatus(SysUser user) {
        return baseMapper.updateByPrimaryKey(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserAuth(Long userId, Long[] roleIds) {
        Example example = new Example(SysUserRole.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(SysUserRole::getUserId, userId);
        userRoleMapper.deleteByExample(example);
        insertUserRole(userId, roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (ArrayUtil.isNotEmpty(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = StreamUtils.toList(Arrays.asList(roleIds), roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            });
            userRoleMapper.insertList(list);
        }
    }

    @Override
    public boolean registerUser(SysUser user) {
        user.setCreateBy(user.getUserAccount());
        user.setUpdateBy(user.getUserAccount());
        return baseMapper.insert(user) > 0;
    }

}