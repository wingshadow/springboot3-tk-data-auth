package com.hawk.framework.service;



import com.hawk.framework.entity.SysDept;

import java.util.List;

/**
 * 通用 部门服务
 *
 * @author Lion Li
 */
public interface DeptService {

    /**
     * 通过部门ID查询部门名称
     *
     * @param deptIds 部门ID串逗号分隔
     * @return 部门名称串逗号分隔
     */
    String selectDeptNameByIds(String deptIds);

    /**
     * 获得父部门以及子部门的部门ID
     *
     * @param parentDeptId 父部门
     * @return
     */
    List<Long> deptByParent(Long parentDeptId);

    /**
     * 根据祖级列表获得部门ID
     *
     * @param ancestors 0,3701,370100
     * @return 部门Id
     */
    List<Long> deptByAncestors(String ancestors);


    SysDept deptById(Long deptId);

}
