package com.hawk.system.mapper;

import com.hawk.framework.annotation.scope.DataScope;
import com.hawk.framework.base.BaseMapper;
import com.hawk.system.entity.SysRole;
import com.hawk.system.entity.SysUser;
import com.hawk.system.mapper.provider.SysUserProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
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
    @SelectProvider(type = SysUserProvider.class, method = "selectUserList")
    List<SysUser> selectUserList(@Param("param1") String param1);

    @DataScope(deptAlias = "d")
    @SelectProvider(type = SysUserProvider.class, method = "selectAllocatedList")
    List<SysUser> selectAllocatedList(@Param("param1") String param1);

    @DataScope(deptAlias = "d")
    @SelectProvider(type = SysUserProvider.class, method = "selectUnallocatedList")
    List<SysUser> selectUnallocatedList(@Param("param1") String param1);

    default boolean exists(Example example) {
        int count = this.selectCountByExample(example);
        return count > 0;
    }
}