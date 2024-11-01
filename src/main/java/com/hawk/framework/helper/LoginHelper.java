package com.hawk.framework.helper;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hawk.framework.common.constant.UserConstants;
import com.hawk.framework.enums.LoginWay;
import com.hawk.framework.enums.UserType;
import com.hawk.framework.model.LoginUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-22 14:44
 */
public class LoginHelper {

    public static final String LOGIN_USER_KEY = "loginUser";
    public static final String USER_KEY = "userId";
    private static final Map<Long, List<Long>> deptList = new HashMap<>();

    public static List<Long> getUserDeptId(String jti) {
        // 从登陆用户信息获取用户管理部门ID,超级管理员不返回
        return null;
    }

    public static void loginByDevice(LoginUser loginUser, LoginWay loginWay) {
        SaStorage storage = SaHolder.getStorage();
        storage.set(LOGIN_USER_KEY, loginUser);
        storage.set(USER_KEY, loginUser.getUserId());
        SaLoginModel model = new SaLoginModel();
        if (loginWay != null) {
            model.setDevice(loginWay.getLoginType());
        }
        StpUtil.login(loginUser.getLoginId(), model.setExtra(USER_KEY, loginUser.getUserId()));
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 登录系统
     *
     * @param loginUser 登录用户信息
     */
    public static void login(LoginUser loginUser) {
        loginByDevice(loginUser, null);
    }

    /**
     * 获取用户(多级缓存)
     */
    public static LoginUser getLoginUser() {
        try {
            LoginUser loginUser = (LoginUser) SaHolder.getStorage().get(LOGIN_USER_KEY);
            if (loginUser != null) {
                return loginUser;
            }
            Object user = StpUtil.getTokenSession().get(LOGIN_USER_KEY);
            if (user == null) {
                return null;
            }
            if(user instanceof LoginUser){
                loginUser = (LoginUser) user;
                SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
                return loginUser;
            }
            loginUser = JSONUtil.toBean((JSONObject) user,LoginUser.class);
            SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
            return loginUser;
        } catch (Exception e) {
            e.printStackTrace();
            if (!StpUtil.isLogin()) {
                return null;
            }
        }
        return null;
    }

    public static LoginUser getLoginUser(String token) {
        return (LoginUser) StpUtil.getTokenSessionByToken(token).get(LOGIN_USER_KEY);
    }

    /**
     * 获取用户id
     */
    public static Long getUserId() {
        Long userId;
        try {
            userId = Convert.toLong(SaHolder.getStorage().get(USER_KEY));
            if (ObjectUtil.isNull(userId)) {
                userId = Convert.toLong(StpUtil.getExtra(USER_KEY));
                SaHolder.getStorage().set(USER_KEY, userId);
            }
        } catch (Exception e) {
            return null;
        }
        return userId;
    }

    /**
     * 获取部门ID
     */
    public static Long getDeptId() {
        return getLoginUser().getDeptId();
    }

    /**
     * 获取用户账户
     */
    public static String getUserAccount() {
        return getLoginUser().getUserAccount();
    }

    /**
     * 获取用户昵称
     */
    public static String getUserName() {
        return getLoginUser().getUserName();
    }

    public static UserType getUserType() {
        String loginId = StpUtil.getLoginIdAsString();
        return UserType.getUserType(loginId);
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return UserConstants.ADMIN_ID.equals(userId);
    }

    public static boolean isAdmin() {
        return isAdmin(getUserId());
    }

}
