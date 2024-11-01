package com.hawk.system.mapper;


import com.hawk.framework.base.BaseMapper;
import com.hawk.framework.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.entity.Example;

/**
 * 角色与菜单关联表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
    default boolean exists(Example example) {
        int count = this.selectCountByExample(example);
        return count > 0;
    }
}
