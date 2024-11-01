package com.hawk.system.mapper;


import com.hawk.framework.base.BaseMapper;
import com.hawk.framework.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-04-26 15:09
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    List<Long> selectUserIdsByRoleId(Long roleId);

}
