package com.hawk.framework.plus.batch.update;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-12-05 10:25
 */
@RegisterMapper
public interface BatchUpdateLimitMapper<T> {
    /**
     * 批量更新方法
     * @param list 要更新的实体集合
     * @return 更新记录数
     */
    @UpdateProvider(type = BatchUpdateLimitProvider.class, method = "dynamicSQL")
    int updateBatchSelectiveLimit(List<T> list, int size);
}
