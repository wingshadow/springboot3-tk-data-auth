package com.hawk.controller.system.form;

import com.hawk.framework.annotation.sensitive.Sensitive;
import com.hawk.framework.annotation.xss.Xss;
import com.hawk.framework.common.core.form.BaseForm;
import com.hawk.framework.enums.SensitiveStrategy;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserAddForm extends BaseForm {

    /**
     * 用户ID
     */
    @Id
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户账号
     */
    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过{max}个字符")
    private String userAccount;

    /**
     * 用户昵称
     */
    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过{max}个字符")
    private String userName;

    /**
     * 用户类型（sys_user系统用户 employee内部员工 外部员工  挂靠  亲戚）
     */
    private String userType;

    /**
     * 用户邮箱
     */
    @Sensitive(strategy = SensitiveStrategy.EMAIL)
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    private String email;

    /**
     * 手机号码
     */
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String mobile;

    /**
     * 用户性别
     */
    private String gender;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;


    /**
     * 备注
     */
    private String remark;

    /**
     * 角色组
     */
    private Long[] roleIds;


}
