package com.hawk.generator.mapper;


import com.hawk.framework.base.BaseMapper;
import com.hawk.generator.entity.GenTable;
import com.hawk.generator.entity.GenTableColumn;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 业务字段 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface GenTableColumnMapper extends BaseMapper<GenTableColumn> {
    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @return 列信息
     */
    List<GenTableColumn> selectDbTableColumnsByName(String tableName);

}
