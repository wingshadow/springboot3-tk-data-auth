package com.hawk.utils;

import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class SqlUtils {

    private final List<String> sqlConditions = new ArrayList<>();

    // 构造方法，初始化 Criteria
    private SqlUtils() {
    }

    /**
     * 静态方法，创建 CriteriaUtils 对象
     *
     * @param criteria Example.Criteria 对象
     * @return CriteriaUtils 实例
     */
    public static SqlUtils build() {
        return new SqlUtils();
    }

    // 等于条件
    public SqlUtils eq(String field, Object value) {
        validate(field);
        String condition = field + " = " + formatValue(value);
        sqlConditions.add(condition);
        return this;
    }

    public SqlUtils eq(boolean flag, String field, Object value) {
        validate(field);
        if (flag) {
            String condition = field + " = " + formatValue(value);
            sqlConditions.add(condition);
        }
        return this;
    }

    // 不等于条件
    public SqlUtils ne(String field, Object value) {
        validate(field);
        String condition = field + " <> " + formatValue(value);
        sqlConditions.add(condition);
        return this;
    }

    // 大于条件
    public SqlUtils gt(String field, Object value) {
        validate(field);
        String condition = field + " > " + formatValue(value);
        sqlConditions.add(condition);
        return this;
    }

    // 小于条件
    public SqlUtils lt(String field, Object value) {
        validate(field);
        String condition = field + " < " + formatValue(value);
        sqlConditions.add(condition);
        return this;
    }

    // LIKE 条件
    public SqlUtils like(String field, String value) {
        validate(field);
        String condition = field + " LIKE " + formatValue("%" + value + "%");
        sqlConditions.add(condition);
        return this;
    }

    public SqlUtils like(boolean flag, String field, String value) {
        validate(field);
        if (flag) {
            String condition = field + " LIKE " + formatValue("%" + value + "%");
            sqlConditions.add(condition);
        }
        return this;
    }

    // 生成 SQL WHERE 子句
    public String toSQL() {
        if (sqlConditions.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(" AND ");
        for (String condition : sqlConditions) {
            joiner.add(condition);
        }
        return joiner.toString();
    }

    // 校验字段
    private void validate(String field) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("字段名称不能为空");
        }
    }

    // 格式化值
    private String formatValue(Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
        } else if (value == null) {
            return "NULL";
        } else {
            return value.toString();
        }
    }
}

