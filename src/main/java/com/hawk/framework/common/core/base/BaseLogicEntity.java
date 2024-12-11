package com.hawk.framework.common.core.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-12-11 10:12
 */
@Getter
@Setter
public class BaseLogicEntity extends BaseDataEntity{

    private Integer isDeleted;
}
