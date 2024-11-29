package com.hawk.system.mapper;

import cn.hutool.core.lang.func.LambdaUtil;
import com.hawk.framework.common.constant.UserConstants;
import com.hawk.framework.base.BaseMapper;
import com.hawk.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

/**
 * 菜单表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户所有权限
     *
     * @return 权限列表
     */
    List<String> selectMenuPerms();

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 查询条件
     * @return 菜单列表
     */
    List<SysMenu> selectMenuListByUserId(@Param("userId") Long userId,@Param("menu") SysMenu menu);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    default List<SysMenu> selectMenuTreeAll() {
        Example example = new Example(SysMenu.class);
        String menuTypeName = LambdaUtil.getFieldName(SysMenu::getMenuType);
        example.createCriteria().andIn(menuTypeName, Arrays.asList(UserConstants.TYPE_DIR, UserConstants.TYPE_MENU))
                .andEqualTo(LambdaUtil.getFieldName(SysMenu::getStatus), UserConstants.MENU_NORMAL);
        example.orderBy(LambdaUtil.getFieldName(SysMenu::getParentId)).asc();
        example.orderBy(LambdaUtil.getFieldName(SysMenu::getOrderNum)).asc();
        return selectByExample(example);
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId            角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);

    @Select("select * from sys_menu where (menu_id in (${defaultMenu}) or parent_id in (${defaultMenu})) and menu_type in ('M', 'C') and status = '0'")
    List<SysMenu> defaultMenu(@Param("defaultMenu") String defaultMenu);

    default boolean exists(Example example) {
        int count = this.selectCountByExample(example);
        return count > 0;
    }
}
