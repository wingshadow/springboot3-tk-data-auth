package com.hawk.system.mapper;

import com.hawk.framework.annotation.DataScope;
import com.hawk.framework.base.BaseMapper;
import com.hawk.framework.entity.SysRole;
import com.hawk.framework.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2023-02-14 14:53
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    List<SysRole> selectRoleByUserId(Long userId);

    List<SysUser> getAllUser(SysUser sysUser);

    SysUser selectUserByAccount(String userAccount);

    SysUser selectUserByMobile(String mobile);

    SysUser selectUserByEmail(String email);
    @DataScope(deptAlias = "d")
    @Select("select u.user_id," +
            "               u.dept_id," +
            "               u.user_name," +
            "               u.nick_name," +
            "               u.user_account," +
            "               u.email," +
            "               u.avatar," +
            "               u.mobile," +
            "               u.gender," +
            "               u.user_type," +
            "               u.status," +
            "               u.del_flag," +
            "               u.create_by," +
            "               u.create_time," +
            "               d.dept_name " +
            "        from sys_user u " +
            "                 left join sys_dept d on u.dept_id = d.dept_id ")
    List<SysUser> selectUserList(@Param("param1") String param1);

    @DataScope(deptAlias = "d")
    @Select("select distinct u.user_id," +
            "                        u.dept_id," +
            "                        u.user_name," +
            "                        u.nick_name," +
            "                        u.email," +
            "                        u.mobile," +
            "                        u.status," +
            "                        u.create_time" +
            "        from sys_user u " +
            "                 left join sys_dept d on u.dept_id = d.dept_id " +
            "                 left join sys_user_role sur on u.user_id = sur.user_id " +
            "                 left join sys_role r on r.role_id = sur.role_id ")
    List<SysUser> selectAllocatedList(@Param("param1") String param1);

    @DataScope(deptAlias = "d")
    @Select("select distinct u.user_id, " +
            "                        u.dept_id, " +
            "                        u.user_name, " +
            "                        u.nick_name, " +
            "                        u.email, " +
            "                        u.mobile, " +
            "                        u.status, " +
            "                        u.create_time " +
            "        from sys_user u " +
            "                 left join sys_dept d on u.dept_id = d.dept_id " +
            "                 left join sys_user_role sur on u.user_id = sur.user_id " +
            "                 left join sys_role r on r.role_id = sur.role_id ")
    List<SysUser> selectUnallocatedList(@Param("param1") String param1);

    default boolean exists(Example example) {
        int count = this.selectCountByExample(example);
        return count > 0;
    }
}