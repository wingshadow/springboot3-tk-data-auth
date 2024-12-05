package com.hawk.framework.plus.batch.update;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Set;

@Slf4j
public class BatchUpdateLimitProvider extends MapperTemplate {


    public BatchUpdateLimitProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String updateBatchSelectiveLimit(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        // 开始拼接 SQL
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append("<trim prefix=\"set\" suffixOverrides=\",\">");

        // 获取全部列
        Set<EntityColumn> allColumns = EntityHelper.getColumns(entityClass);
        // 获取主键列
        Set<EntityColumn> pkColumns = EntityHelper.getPKColumns(entityClass);

        // 拼接可更新列的 CASE 语句
        for (EntityColumn column : allColumns) {
            if (!column.isId() && column.isUpdatable()) {
                appendCaseStatement(sql, column, pkColumns);
            }
        }
        sql.append("</trim>");

        // WHERE 子句
        sql.append("WHERE ");
        appendPrimaryKeyWhereClause(sql, pkColumns);
        // 加入 LIMIT 子句，限制更新数量
        sql.append("<if test='size != null'> LIMIT #{size}</if>");
        return sql.toString();
    }

    /**
     * 拼接 CASE 语句
     */
    private void appendCaseStatement(StringBuilder sql, EntityColumn column, Set<EntityColumn> pkColumns) {
        sql.append("  <trim prefix=\"").append(column.getColumn()).append(" = CASE\" suffix=\"END,\">");
        sql.append("     <foreach collection=\"list\" item=\"i\" index=\"index\">");
        sql.append(this.getIfNotNull("i", column, true));
        sql.append("         WHEN ");
        int count = 0;
        for (EntityColumn pk : pkColumns) {
            if (count != 0) {
                sql.append(" AND ");
            }
            sql.append(pk.getColumn()).append(" = #{i.").append(pk.getProperty()).append("}");
            count++;
        }
        sql.append(" THEN ").append(column.getColumnHolder("i")).append(" ");
        sql.append("     </if>");
        sql.append("     </foreach>");
        sql.append("  </trim>");
    }

    /**
     * 拼接主键 WHERE 子句
     */
    private void appendPrimaryKeyWhereClause(StringBuilder sql, Set<EntityColumn> pkColumns) {
        int count = 0;
        for (EntityColumn pk : pkColumns) {
            sql.append(pk.getColumn());
            if (count < pkColumns.size() - 1) {
                sql.append(", ");
            }
            count++;
        }
        sql.append(" IN (");
        sql.append("  <foreach collection=\"list\" separator=\",\" item=\"i\" index=\"index\">");
        for (EntityColumn pk : pkColumns) {
            sql.append("  #{i.").append(pk.getProperty()).append("}");
        }
        sql.append("  </foreach>");
        sql.append(")");
    }


    private String getIfNotNull(String entityName, EntityColumn column, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("   <if test=\"");
        if (StringUtil.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" != null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" and ");
            if (StringUtil.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" != '' ");
        }
        sql.append("\">");
        return sql.toString();
    }

}

