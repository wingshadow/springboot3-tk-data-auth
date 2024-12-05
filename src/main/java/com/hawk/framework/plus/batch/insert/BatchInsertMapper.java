package com.hawk.framework.plus.batch.insert;

import com.hawk.framework.plus.batch.update.BatchUpdateProvider;
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
public interface BatchInsertMapper<T> {
    @InsertProvider(type = BatchInsertProvider.class, method = "dynamicSQL")
    int insertAllFieldBatch(List<T> list);
}
