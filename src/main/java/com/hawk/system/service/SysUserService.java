package com.hawk.system.service;

import com.github.pagehelper.PageInfo;
import com.hawk.framework.base.BaseService;
import com.hawk.system.entity.SysRole;
import com.hawk.system.entity.SysUser;

import java.util.List;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2023-02-14 14:58
 */
public interface SysUserService extends BaseService<SysUser> {
    List<SysUser> getAllUser(SysUser sysUser);

    List<SysRole> selectRoleByUserId(Long userId);

    PageInfo<SysUser> selectPageUserList(SysUser params, int pageSize, int pageNum);

    PageInfo<SysUser> selectAllocatedList(SysUser params, int pageSize, int pageNum);

    PageInfo<SysUser>  selectUnallocatedList(SysUser params, int pageSize, int pageNum);

    List<SysUser> selectUserList(SysUser user);

    SysUser selectUserByUserName(String userName);

    void checkUserDataScope(Long userId);

    boolean checkUserAccountUnique(SysUser user);

    boolean checkPhoneUnique(SysUser user);

    boolean checkEmailUnique(SysUser user);

    void checkUserAllowed(SysUser user);

    int resetPwd(SysUser user);

    int updateUserStatus(SysUser user);

    void insertUserAuth(Long userId, Long[] roleIds);

    void insertUserRole(Long userId, Long[] roleIds);

    boolean registerUser(SysUser user);

}