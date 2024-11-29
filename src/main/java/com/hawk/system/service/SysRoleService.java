package com.hawk.system.service;

import com.github.pagehelper.PageInfo;
import com.hawk.framework.base.BaseService;
import com.hawk.system.entity.SysDept;
import com.hawk.system.entity.SysRole;
import com.hawk.system.entity.SysUser;
import com.hawk.system.entity.SysUserRole;

import java.util.List;
import java.util.Set;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2023-02-14 14:58
 */
public interface SysRoleService extends BaseService<SysRole> {

    List<SysRole> getAllRoleList();

    List<SysDept> selectDeptByRoleId(Long roleId);

    Set<String> getRolePermission(SysUser user);

    List<SysRole> getRoleByUserId(Long userId);

    PageInfo<SysRole> selectRoleList(SysRole sysRole , int pageSize, int pageNum);

    List<SysRole> selectRoleList(SysRole sysRole);

    void checkRoleDataScope(Long roleId);

    boolean checkRoleNameUnique(SysRole role);

    boolean checkRoleKeyUnique(SysRole role);

    void checkRoleAllowed(SysRole role);

    int insertRole(SysRole role);

    int updateRole(SysRole role);

    int updateRoleStatus(SysRole role);

    int authDataScope(SysRole role);

    int deleteRoleById(Long roleId);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    int deleteAuthUser(SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    int deleteAuthUsers(Long roleId, Long[] userIds);

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    int insertAuthUsers(Long roleId, Long[] userIds);

    void cleanOnlineUserByRole(Long roleId);

    long countUserRoleByRoleId(Long roleId);

    public Set<String> selectRolePermissionByUserId(Long userId);
}