package com.hawk.framework.common.constant;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-05-05 08:36
 */
public interface DataScopeType {
    //查询所有
    String DATA_SCOPE_ALL = "1";
    //自定义
    String DATA_SCOPE_CUSTOM = "2";
    //本部门
    String DATA_SCOPE_DEPT = "3";
    //本部门以及子部门
    String DATA_SCOPE_DEPT_AND_CHILD = "4";
    //只能查看自己的用户信息，不能查看部门信息
    String DATA_SCOPE_SELF = "5";
}
