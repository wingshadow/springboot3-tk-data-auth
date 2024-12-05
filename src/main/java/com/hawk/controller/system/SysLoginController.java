package com.hawk.controller.system;

import cn.dev33.satoken.annotation.SaIgnore;
import com.hawk.framework.common.constant.Constants;
import com.hawk.framework.web.resp.RespCode;
import com.hawk.system.entity.SysMenu;
import com.hawk.system.entity.SysUser;
import com.hawk.framework.helper.LoginHelper;
import com.hawk.framework.model.LoginBody;
import com.hawk.framework.model.LoginUser;
import com.hawk.framework.web.resp.R;
import com.hawk.system.common.LoginService;
import com.hawk.system.service.SysMenuService;
import com.hawk.system.service.SysUserService;
import com.hawk.system.vo.RouterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-04-28 16:18
 */
@Slf4j
@RestController
public class SysLoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysMenuService menuService;

    @SaIgnore
    @PostMapping("/login")
    public R<Map<String, Object>> login(@Validated @RequestBody LoginBody loginBody) {
        Map<String, Object> ajax = new HashMap<>();
        String token = loginService.login(loginBody.getUserAccount(), loginBody.getPassword());
        ajax.put(Constants.TOKEN, token);
        return R.ok(ajax);
    }

    @PostMapping(value = "logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok();
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public R<Map<String, Object>> getInfo() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        SysUser user = new SysUser();
        user.setUserId(loginUser.getUserId());
        user = userService.selectOne(user);
        Map<String, Object> ajax = new HashMap<>();
        ajax.put("user", user);
        ajax.put("roles", loginUser.getRolePermission());
        ajax.put("permissions", loginUser.getMenuPermission());
        return R.ok(ajax);
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public R<List<RouterVo>> getRouters() {
        Long userId = LoginHelper.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return R.ok(menuService.buildMenus(menus));
    }
}
