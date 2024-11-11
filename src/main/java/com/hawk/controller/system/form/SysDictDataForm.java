package com.hawk.controller.system.form;

import com.hawk.framework.common.core.form.BasePageForm;
import lombok.Data;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-05-29 09:09
 */
@Data
public class SysDictDataForm extends BasePageForm {
    private String dictCode;

    private String dictSort;

    private String dictLabel;

    private String dictValue;

    private String dictType;

    private String cssClass;

    private String listClass;

    private String isDefault;

    private String status;

    private String remark;
}
