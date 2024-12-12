package com.hawk.controller.system.form;

import com.hawk.framework.annotation.sensitive.Sensitive;
import com.hawk.framework.annotation.xss.Xss;
import com.hawk.framework.common.core.form.BasePageForm;
import com.hawk.framework.enums.SensitiveStrategy;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-05-20 10:34
 */
@Data
public class SysUserForm extends BasePageForm {

    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过{max}个字符")
    private String userAccount;

    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过{max}个字符")
    private String userName;

    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String mobile;

    private String deptId;

    private String status;
}
