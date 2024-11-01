package com.hawk.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.github.pagehelper.PageInfo;
import com.hawk.framework.base.BaseService;
import com.hawk.framework.entity.SysDept;
import com.hawk.system.mapper.SysDeptMapper;

import java.util.List;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2023-02-14 14:58
 */
public interface SysDeptService extends BaseService<SysDept> {

    List<SysDept> getAllDeptList(SysDept sysDept);
    String getDeptAndChild(Long deptId);

    List<SysDept> selectDeptList(SysDept dept);

    List<Tree<String>> selectDeptTreeList(SysDept dept);

    List<Tree<String>> buildDeptTreeSelect(List<SysDept> depts);

    void checkDeptDataScope(Long deptId);

    boolean checkDeptNameUnique(SysDept dept);

    int insertDept(SysDept dept);

    long selectNormalChildrenDeptById(Long deptId);

    int updateDept(SysDept dept);

    boolean hasChildByDeptId(Long deptId);

    boolean checkDeptExistUser(Long deptId);

    List<Long> selectDeptListByRoleId(Long roleId);

    PageInfo<SysDept> selectPageList(SysDept dept, int pageNum, int pageSize);
}