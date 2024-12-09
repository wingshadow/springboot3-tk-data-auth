package com.hawk.generator.mapper;


import com.hawk.framework.base.BaseMapper;
import com.hawk.generator.entity.GenTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业务 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface GenTableMapper extends BaseMapper<GenTable> {

    List<GenTable> selectPageDbTableList(@Param("genTable") GenTable genTable);
    /**
     * 查询据库列表
     *
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    List<GenTable> selectDbTableListByNames(String[] tableNames);

}
