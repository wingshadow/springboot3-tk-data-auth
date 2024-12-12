package com.hawk.controller.system.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hawk.framework.common.core.form.BaseForm;
import jakarta.persistence.Id;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-12-12 09:43
 */
@Data
public class SysDeptForm extends BaseForm {
    private String deptId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String parentId;

    @Size(min = 0, max = 30, message = "部门名称长度不能超过{max}个字符")
    private String deptName;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    @Size(min = 0, max = 11, message = "联系电话长度不能超过{max}个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    private String email;

    private String ancestors;

    @NotNull(message = "显示顺序不能为空")
    private String orderNum;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String isDeleted;
}
