package com.hawk.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hawk.framework.common.core.base.BaseDataEntity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 参数配置表 sys_config
 *
 * @author Lion Li
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "sys_config")
public class SysConfig extends BaseDataEntity {

    /**
     * 参数主键
     */
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long configId;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    private String configType;

    /**
     * 备注
     */
    private String remark;

}
