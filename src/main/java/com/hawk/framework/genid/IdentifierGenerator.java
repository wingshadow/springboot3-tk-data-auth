package com.hawk.framework.genid;

import cn.hutool.core.util.IdUtil;

public class IdentifierGenerator {

    public Long genId() {
        return IdUtil.getSnowflakeNextId();
    }
}
