package com.hawk.system.mapper;

import com.hawk.framework.annotation.scope.DataScope;
import com.hawk.framework.base.BaseMapper;
import com.hawk.framework.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-04-26 15:09
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    List<SysDept> getAllDeptList();

    @DataScope(deptAlias = "d")
    List<SysDept> selectPageDeptList(@Param("param1") String param1);

    @DataScope(deptAlias = "d")
    @Select("select * from sys_dept d ")
    List<SysDept> selectDeptList(@Param("param1") String param1);

    List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId, @Param("deptCheckStrictly") boolean deptCheckStrictly);

    default boolean exists(Example example) {
        int count = this.selectCountByExample(example);
        return count > 0;
    }
}
