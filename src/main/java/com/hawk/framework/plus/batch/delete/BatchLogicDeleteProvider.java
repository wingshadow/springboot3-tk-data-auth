package com.hawk.framework.plus.batch.delete;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-12-05 15:35
 */
public class BatchLogicDeleteProvider extends MapperTemplate {

    public BatchLogicDeleteProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String deleteLogicBatch(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        // 找到主键列
        Set<EntityColumn> pkColumns = EntityHelper.getPKColumns(entityClass);

        EntityColumn deleteColumn = SqlHelper.getLogicDeleteColumn(entityClass);
        // 开始拼接 SQL
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(" set ").append(deleteColumn.getColumn()).append(" = ");
        sql.append(SqlHelper.getLogicDeletedValue(deleteColumn,true));
        sql.append(" where ");
        int count = 0;
        for (EntityColumn pk : pkColumns) {
            sql.append(pk.getColumn());
            if (count < pkColumns.size() - 1) {
                sql.append(", ");
            }
            count++;
        }
        sql.append(" IN ");
        sql.append("<trim prefix=\"(\" suffix=\")\">");
        sql.append("<foreach collection=\"list\" separator=\",\" item=\"i\" index=\"index\"  >");
        sql.append("#{i").append("}");
        sql.append("</foreach>");
        sql.append("</trim>");
        return sql.toString();
    }
}
