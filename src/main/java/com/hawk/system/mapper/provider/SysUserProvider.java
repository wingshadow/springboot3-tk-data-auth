package com.hawk.system.mapper.provider;

import cn.hutool.core.util.StrUtil;
import com.hawk.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-01-14 16:53
 */
public class SysUserProvider {

    public String selectUnallocatedList(@Param("param1") String param1){
        String sql = " select distinct u.user_id,\n" +
                "                            u.dept_id,\n" +
                "                            u.user_name,\n" +
                "                            u.nick_name,\n" +
                "                            u.email,\n" +
                "                            u.mobile,\n" +
                "                            u.status,\n" +
                "                            u.create_time\n" +
                "            from sys_user u\n" +
                "                     left join sys_dept d on u.dept_id = d.dept_id\n" +
                "                     left join sys_user_role sur on u.user_id = sur.user_id\n" +
                "                     left join sys_role r on r.role_id = sur.role_id";
        if(StrUtil.isNotBlank(param1)){
            sql = sql + " where " + param1;
        }
        return sql;
    }

    public String selectAllocatedList(@Param("param1") String param1){
        String sql = "select distinct u.user_id,\n" +
                "                            u.dept_id,\n" +
                "                            u.user_name,\n" +
                "                            u.nick_name,\n" +
                "                            u.email,\n" +
                "                            u.mobile,\n" +
                "                            u.status,\n" +
                "                            u.create_time\n" +
                "            from sys_user u\n" +
                "                     left join sys_dept d on u.dept_id = d.dept_id\n" +
                "                     left join sys_user_role sur on u.user_id = sur.user_id\n" +
                "                     left join sys_role r on r.role_id = sur.role_id";
        if(StrUtil.isNotBlank(param1)){
            sql = sql + " where " + param1;
        }
        return sql;
    }

    public String selectUserList(@Param("param1") String param1){
        String sql = "select u.user_id,\n" +
                "        u.dept_id,\n" +
                "        u.user_name,\n" +
                "        u.nick_name,\n" +
                "        u.user_account,\n" +
                "        u.email,\n" +
                "        u.avatar,\n" +
                "        u.mobile,\n" +
                "        u.gender,\n" +
                "        u.user_type,\n" +
                "        u.status,\n" +
                "        u.is_deleted,\n" +
                "        u.create_by,\n" +
                "        u.create_time,\n" +
                "        d.dept_name\n" +
                "        from sys_user u\n" +
                "        left join sys_dept d on u.dept_id = d.dept_id";
        if(StrUtil.isNotBlank(param1)){
            sql = sql + " where " + param1;
        }
        return sql;
    }
}
