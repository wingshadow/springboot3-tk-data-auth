package com.hawk.controller.system.form;

import com.hawk.framework.common.core.form.BasePageForm;
import lombok.Data;

/**
 * 字典类型表 sys_dict_type
 *
 * @author Lion Li
 */

@Data
public class SysDictTypeForm extends BasePageForm {

    private String dictId;

    private String dictName;

    private String dictType;

    private String status;

    private String remark;

}
