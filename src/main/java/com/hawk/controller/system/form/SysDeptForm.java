package com.hawk.controller.system.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hawk.framework.common.core.form.BaseForm;
import jakarta.persistence.Id;
import lombok.Data;

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

    private String deptName;

    private String ancestors;

    private String orderNum;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String isDeleted;
}
