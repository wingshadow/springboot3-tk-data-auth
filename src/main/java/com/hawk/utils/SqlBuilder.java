package com.hawk.utils;

import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class SqlBuilder {

    public static String buildWhereClause(Example example, String alias) {
        return example.getOredCriteria().stream()
                .flatMap(oredCriteria -> oredCriteria.getCriteria().stream())
                .map(criterion -> {
                    String condition = criterion.getCondition();
                    Object value = criterion.getValue();
                    Object secondValue = criterion.getSecondValue();
                    String sql = "";

                    if (condition.contains("between") && value != null && secondValue != null) {
                        // 处理 BETWEEN 的逻辑
                        String formattedValue1 = formatValue(value);
                        String formattedValue2 = formatValue(secondValue);
                        sql = condition + formattedValue1 + " AND " + formattedValue2;
                    }  else if (value instanceof String) {
                        // 对 String 类型的值进行适当的转义处理，防止 SQL 注入
                        sql = condition + " '" + escapeString((String) value) + "'";
                    } else if (value instanceof Long || value instanceof Integer || value instanceof BigDecimal) {
                        // 数字类型可以直接拼接到 SQL 中
                        sql = condition + " " + value;
                    } else if (value instanceof Boolean) {
                        // 布尔类型根据数据库的需求，可以将 true/false 转为 1/0
                        sql = condition + " " + ((Boolean) value ? 1 : 0);
                    } else if (value instanceof Date) {
                        // 日期类型可以根据需要进行格式化
                        sql = condition + " '" + new java.sql.Timestamp(((Date) value).getTime()) + "'";
                    } else if (value instanceof Collection<?>) {
                        // 集合类型用于 IN 语句，拼接成类似 "column IN (value1, value2)"
                        String inClause = ((Collection<?>) value).stream()
                                .map(SqlBuilder::formatValue)
                                .collect(Collectors.joining(", "));
                        sql = condition + " (" + inClause + ")";

                    } else if (value instanceof Object[]) {
                        // 数组类型类似集合，处理 IN 语句
                        String inClause = Arrays.stream((Object[]) value)
                                .map(SqlBuilder::formatValue)
                                .collect(Collectors.joining(", "));
                        sql = condition + " (" + inClause + ")";
                    } else {
                        // 其他类型可以进一步扩展
                        sql = condition + " " + value;
                    }
                    sql = StringUtils.isNotBlank(alias) ? alias + "." + sql : "" + sql;
                    return sql;
                })
                .reduce((first, second) -> first + " AND " + second)
                .orElse("");
    }

    // 辅助方法：转义字符串，防止简单的 SQL 注入
    private static String escapeString(String input) {
        return input.replace("'", "''");
    }

    // 辅助方法：根据不同类型返回适当的 SQL 片段
    private static String formatValue(Object value) {
        if (value instanceof String) {
            return "'" + escapeString((String) value) + "'";
        } else if (value instanceof Date) {
            return "'" + new java.sql.Timestamp(((Date) value).getTime()) + "'";
        } else if (value instanceof Boolean) {
            return ((Boolean) value ? "1" : "0");
        } else {
            return value.toString();
        }
    }

    public static String getSqlBeforeLimit(String originalSqlLower) {
        // 查找 LIMIT 关键字的位置
        int limitIndex = originalSqlLower.indexOf("limit");

        // 如果找到了 LIMIT，返回 LIMIT 之前的子串
        if (limitIndex != -1) {
            return originalSqlLower.substring(0, limitIndex).trim();
        }

        // 如果没有 LIMIT，返回原 SQL
        return originalSqlLower;
    }

    public static String getSqlIncludingLimit(String originalSqlLower) {
        // 查找 LIMIT 关键字的位置
        int limitIndex = originalSqlLower.indexOf("limit");

        // 如果找到了 LIMIT，返回包含 LIMIT 及其之后的子串
        if (limitIndex != -1) {
            return originalSqlLower.substring(limitIndex).trim();
        }

        // 如果没有 LIMIT，返回空字符串
        return "";
    }
}



