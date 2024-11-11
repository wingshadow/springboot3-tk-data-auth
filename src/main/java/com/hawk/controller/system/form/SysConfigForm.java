package com.hawk.controller.system.form;

import com.hawk.framework.common.core.form.BasePageForm;
import lombok.Data;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-05-29 08:58
 */
@Data
public class SysConfigForm extends BasePageForm {
    private String configId;

    private String configName;

    private String configKey;

    private String configValue;

    private String configType;

    private String remark;
}
