package com.hawk.framework.plus.batch.delete;

import org.apache.ibatis.annotations.InsertProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-12-05 11:40
 */
@RegisterMapper
public interface BatchLogicDeleteMapper<T> {
    @InsertProvider(type = BatchLogicDeleteProvider.class, method = "dynamicSQL")
    int deleteLogicBatch(List<Long> list);
}
