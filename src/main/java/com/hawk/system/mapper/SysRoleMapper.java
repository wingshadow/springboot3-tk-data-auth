package com.hawk.system.mapper;

import com.hawk.framework.base.BaseMapper;
import com.hawk.framework.entity.SysDept;
import com.hawk.framework.entity.SysRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
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
public interface SysRoleMapper extends BaseMapper<SysRole> {
    List<SysRole> getAllRoleList();

    List<SysDept> selectDeptByRoleId(Long roleId);

    List<SysRole> selectRoleByUserId(Long userId);

    List<SysRole> selectRoleList(SysRole sysRole);

    @Select("select distinct r.role_id,\n" +
            "                        r.role_name,\n" +
            "                        r.role_key,\n" +
            "                        r.data_scope,\n" +
            "                        r.menu_check_strictly,\n" +
            "                        r.dept_check_strictly,\n" +
            "                        r.status,\n" +
            "                        r.del_flag,\n" +
            "                        r.create_time \n" +
            "        from sys_role r " +
            "                 left join sys_user_role sur on sur.role_id = r.role_id " +
            "                 left join sys_user u on u.user_id = sur.user_id " +
            "        where u.user_id = #{userId}")
    List<SysRole> selectRolePermissionByUserId(Long userId);

    default boolean exists(Example example) {
        int count = this.selectCountByExample(example);
        return count > 0;
    }
}
