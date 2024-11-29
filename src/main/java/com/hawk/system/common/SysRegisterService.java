package com.hawk.system.common;

import cn.dev33.satoken.secure.BCrypt;
import com.hawk.framework.common.constant.CacheConstants;
import com.hawk.framework.common.constant.Constants;
import com.hawk.system.entity.SysUser;
import com.hawk.framework.enums.UserType;
import com.hawk.framework.exception.user.CaptchaException;
import com.hawk.framework.exception.user.CaptchaExpireException;
import com.hawk.framework.exception.user.UserException;
import com.hawk.framework.model.RegisterBody;
import com.hawk.system.service.SysConfigService;
import com.hawk.system.service.SysUserService;
import com.hawk.utils.MessageUtils;
import com.hawk.utils.StringUtils;
import com.hawk.utils.redis.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 注册校验方法
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysRegisterService {

    private final SysUserService userService;
    private final SysConfigService configService;

    /**
     * 注册
     */
    public void register(RegisterBody registerBody) {
        String userAccount = registerBody.getUserAccount();
        String password = registerBody.getPassword();
        // 校验用户类型是否存在
        String userType = UserType.getUserType(registerBody.getUserType()).getUserType();

        boolean captchaEnabled = configService.selectCaptchaEnabled();
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(userAccount, registerBody.getCode(), registerBody.getUuid());
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount(userAccount);
        sysUser.setPassword(BCrypt.hashpw(password));
        sysUser.setUserType(userType);

        if (!userService.checkUserAccountUnique(sysUser)) {
            throw new UserException("user.register.save.error", userAccount);
        }
        boolean regFlag = userService.registerUser(sysUser);
        if (!regFlag) {
            throw new UserException("user.register.error");
        }
        recordLogininfor(userAccount, Constants.REGISTER, MessageUtils.message("user.register.success"));
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
            recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    private void recordLogininfor(String username, String status, String message) {
//        LogininforEvent logininforEvent = new LogininforEvent();
//        logininforEvent.setUsername(username);
//        logininforEvent.setStatus(status);
//        logininforEvent.setMessage(message);
//        logininforEvent.setRequest(ServletUtils.getRequest());
//        SpringUtils.context().publishEvent(logininforEvent);
    }

}
