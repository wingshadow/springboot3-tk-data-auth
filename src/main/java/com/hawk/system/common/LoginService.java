package com.hawk.system.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hawk.framework.common.constant.CacheConstants;
import com.hawk.framework.common.constant.Constants;
import com.hawk.framework.dto.RoleDTO;
import com.hawk.framework.entity.SysDept;
import com.hawk.framework.entity.SysRole;
import com.hawk.framework.entity.SysUser;
import com.hawk.framework.enums.LoginType;
import com.hawk.framework.enums.LoginWay;
import com.hawk.framework.enums.UserStatus;
import com.hawk.framework.exception.user.CaptchaException;
import com.hawk.framework.exception.user.CaptchaExpireException;
import com.hawk.framework.exception.user.UserException;
import com.hawk.framework.helper.LoginHelper;
import com.hawk.framework.model.LoginUser;
import com.hawk.framework.model.XcxLoginUser;
import com.hawk.framework.service.DeptService;
import com.hawk.system.mapper.SysUserMapper;
import com.hawk.system.service.SysConfigService;
import com.hawk.system.service.SysDeptService;
import com.hawk.system.service.SysRoleService;
import com.hawk.utils.SpringUtils;
import com.hawk.utils.SqlUtils;
import com.hawk.utils.StringUtils;
import com.hawk.utils.redis.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.hawk.framework.common.constant.DataScopeType.*;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-23 16:56
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final SysUserMapper userMapper;
    private final SysDeptService deptService;
    private final SysRoleService roleService;
    private final SysConfigService configService;
    private final PermissionService permissionService;

    @Value("${user.password.maxRetryCount}")
    private Integer maxRetryCount;

    @Value("${user.password.lockTime}")
    private Integer lockTime;

    public String login(String userAccount, String password) {
        SysUser user = loadUserByUsername(userAccount);
        // 密码检验
        checkLogin(LoginType.PASSWORD, userAccount, () -> !BCrypt.checkpw(password, user.getPassword()));
        LoginUser loginUser = buildLoginUser(user);
        LoginHelper.loginByDevice(loginUser, LoginWay.PC);
        return StpUtil.getTokenValue();
    }

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(username, code, uuid);
        }
        SysUser user = loadUserByUsername(username);
        checkLogin(LoginType.PASSWORD, username, () -> !BCrypt.checkpw(password, user.getPassword()));
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        LoginUser loginUser = buildLoginUser(user);
        // 生成token
        LoginHelper.loginByDevice(loginUser, LoginWay.PC);
        return StpUtil.getTokenValue();
    }

    public String smsLogin(String phonenumber, String smsCode) {
        // 通过手机号查找用户
        SysUser user = loadUserByMobile(phonenumber);

        checkLogin(LoginType.SMS, user.getUserName(), () -> !validateSmsCode(phonenumber, smsCode));
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        LoginUser loginUser = buildLoginUser(user);
        // 生成token
        LoginHelper.loginByDevice(loginUser, LoginWay.App);
        return StpUtil.getTokenValue();
    }

    public String emailLogin(String email, String emailCode) {
        // 通过手机号查找用户
        SysUser user = loadUserByEmail(email);

        checkLogin(LoginType.EMAIL, user.getUserName(), () -> !validateEmailCode(email, emailCode));
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        LoginUser loginUser = buildLoginUser(user);
        // 生成token
        LoginHelper.loginByDevice(loginUser, LoginWay.App);
        return StpUtil.getTokenValue();
    }

    public String xcxLogin(String xcxCode) {
        // xcxCode 为 小程序调用 wx.login 授权后获取
        // todo 以下自行实现
        // 校验 appid + appsrcret + xcxCode 调用登录凭证校验接口 获取 session_key 与 openid
        String openid = "";
        SysUser user = loadUserByOpenid(openid);

        // 此处可根据登录用户的数据不同 自行创建 loginUser
        XcxLoginUser loginUser = new XcxLoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUserName(user.getUserName());
        loginUser.setUserType(user.getUserType());
        loginUser.setOpenid(openid);
        // 生成token
        LoginHelper.loginByDevice(loginUser, LoginWay.WeChat);
        return StpUtil.getTokenValue();
    }

    /**
     * 退出登录
     */
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            StpUtil.logout();
            if (loginUser == null) {
                return;
            }
        } catch (NotLoginException ignored) {
        }
    }

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     */
//    private void recordLogininfor(String username, String status, String message) {
//        LogininforEvent logininforEvent = new LogininforEvent();
//        logininforEvent.setUsername(username);
//        logininforEvent.setStatus(status);
//        logininforEvent.setMessage(message);
//        logininforEvent.setRequest(ServletUtils.getRequest());
//        SpringUtils.context().publishEvent(logininforEvent);
//    }

    /**
     * 校验短信验证码
     */
    private boolean validateSmsCode(String phonenumber, String smsCode) {
        String code = RedisUtils.getCacheObject(CacheConstants.CAPTCHA_CODE_KEY + phonenumber);
        if (StringUtils.isBlank(code)) {
            throw new CaptchaExpireException();
        }
        return code.equals(smsCode);
    }

    /**
     * 校验邮箱验证码
     */
    private boolean validateEmailCode(String email, String emailCode) {
        String code = RedisUtils.getCacheObject(CacheConstants.CAPTCHA_CODE_KEY + email);
        if (StringUtils.isBlank(code)) {
            throw new CaptchaExpireException();
        }
        return code.equals(emailCode);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.defaultString(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException();
        }
    }

    private SysUser loadUserByUsername(String username) {
        Example example = new Example(SysUser.class);
        SqlUtils.builder(example.createCriteria())
                .eq(SysUser::getUserAccount, username)
                .orEq(SysUser::getMobile, username);

        SysUser user = userMapper.selectOneByExample(example);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UserException("user.not.exists", username);
        } else if (UserStatus.DISABLE.getCode() == (user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new UserException("user.blocked", username);
        }
        return addUserAttribute(user);
    }

    private SysUser loadUserByMobile(String mobile) {
        Example example = new Example(SysUser.class);
        SqlUtils.builder(example.createCriteria())
                .eq(SysUser::getMobile, mobile);
        SysUser user = userMapper.selectOneByExample(example);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", mobile);
            throw new UserException("user.not.exists", mobile);
        } else if (UserStatus.DISABLE.getCode() == (user.getStatus())) {
            log.info("登录用户：{} 已被停用.", mobile);
            throw new UserException("user.blocked", mobile);
        }
        return addUserAttribute(user);
    }

    private SysUser loadUserByEmail(String email) {
        Example example = new Example(SysUser.class);
        SqlUtils.builder(example.createCriteria())
                .eq(SysUser::getEmail, email);
        SysUser user = userMapper.selectOneByExample(example);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", email);
            throw new UserException("user.not.exists", email);
        } else if (UserStatus.DISABLE.getCode() == (user.getStatus())) {
            log.info("登录用户：{} 已被停用.", email);
            throw new UserException("user.blocked", email);
        }
        return addUserAttribute(user);
    }

    private SysUser loadUserByOpenid(String openid) {
        // 使用 openid 查询绑定用户 如未绑定用户 则根据业务自行处理 例如 创建默认用户
        // todo 自行实现 userService.selectUserByOpenid(openid);
        SysUser user = new SysUser();
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", openid);
            // todo 用户不存在 业务逻辑自行实现
        } else if (UserStatus.DISABLE.getCode() == user.getStatus()) {
            log.info("登录用户：{} 已被停用.", openid);
            // todo 用户已被停用 业务逻辑自行实现
        }
        return addUserAttribute(user);
    }

    private SysUser addUserAttribute(SysUser sysUser){
        if (!sysUser.isAdmin()) {
            SysDept sysDept = deptService.selectByPrimaryKey(sysUser.getDeptId());
            List<SysRole> roles = roleService.getRoleByUserId(sysUser.getUserId());
            sysUser.setRoles(roles);
            sysUser.setDept(sysDept);
        }
        return sysUser;
    }
    /**
     * 构建登录用户
     */
    private LoginUser buildLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUserAccount(user.getUserName());
        loginUser.setUserName(user.getNickName());
        loginUser.setUserType(user.getUserType());
        loginUser.setPhone(user.getMobile());
        loginUser.setMenuPermission(permissionService.getMenuPermission(user));
        loginUser.setRolePermission(permissionService.getRolePermission(user));
        loginUser.setDeptName(ObjectUtil.isNull(user.getDept()) ? "" : user.getDept().getDeptName());
        List<RoleDTO> roles = BeanUtil.copyToList(user.getRoles(), RoleDTO.class);
        loginUser.setRoles(roles);
        loginUser.setDept(user.getDept());
        if (!user.isAdmin()) {
            loginUser.setDepts(getDeptScope(roles, user));
        }

        return loginUser;
    }

    public List<Long> getDeptScope(List<RoleDTO> roles, SysUser user) {
        List<Long> deptIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(roles)) {
            for (RoleDTO sysRole : roles) {
                String sysRoleDataScope = sysRole.getDataScope();
                switch (sysRoleDataScope) {
                    case DATA_SCOPE_ALL -> {
                        // 管理所有部门
                        List<SysDept> list = deptService.selectAll();
                        deptIdList.addAll(list.stream().map(SysDept::getDeptId).toList());
                    }
                    case DATA_SCOPE_CUSTOM -> {
                        // 管理自定义部门
                        SysRoleService sysRoleService = SpringUtils.getBean(SysRoleService.class);
                        List<SysDept> list = sysRoleService.selectDeptByRoleId(sysRole.getRoleId());
                        deptIdList.addAll(list.stream().map(SysDept::getDeptId).toList());
                    }
                    case DATA_SCOPE_DEPT -> {
                        // 管理所属部门
                        deptIdList.add(user.getDeptId());
                    }
                    case DATA_SCOPE_DEPT_AND_CHILD -> {
                        // 管理本部门及子部门
                        DeptService deptService = SpringUtils.getBean(DeptService.class);
                        List<Long> idList = deptService.deptByParent(user.getDeptId());
                        deptIdList.addAll(idList);
                    }
                    default -> {
                        return null;
                    }
                }
            }
        }

        return deptIdList;
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId, String username) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
//        sysUser.setLoginIp(ServletUtils.getClientIP());
//        sysUser.setLoginDate(DateUtils.getNowDate());
        sysUser.setUpdateBy(username);
        userMapper.updateByPrimaryKey(sysUser);
    }

    /**
     * 登录校验
     */
    private void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier) {
        String errorKey = CacheConstants.PWD_ERR_CNT_KEY + username;
        String loginFail = Constants.LOGIN_FAIL;

        // 获取用户登录错误次数(可自定义限制策略 例如: key + username + ip)
        Integer errorNumber = RedisUtils.getCacheObject(errorKey);
        // 锁定时间内登录 则踢出
        if (ObjectUtil.isNotNull(errorNumber) && errorNumber.equals(maxRetryCount)) {
            throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
        }

        if (supplier.get()) {
            // 是否第一次
            errorNumber = ObjectUtil.isNull(errorNumber) ? 1 : errorNumber + 1;
            // 达到规定错误次数 则锁定登录
            if (errorNumber.equals(maxRetryCount)) {
                RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));
                throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
            } else {
                // 未达到规定错误次数 则递增
                RedisUtils.setCacheObject(errorKey, errorNumber);
                throw new UserException(loginType.getRetryLimitCount(), errorNumber);
            }
        }

        // 登录成功 清空错误次数
        RedisUtils.deleteObject(errorKey);
    }
}
