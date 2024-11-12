package com.hawk.utils;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import tk.mybatis.mapper.entity.Example;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-18 10:48
 */
public class SqlUtils {

    private final Example.Criteria criteria;

    // 私有构造函数，传入 Criteria 对象
    private SqlUtils(Example.Criteria criteria) {
        this.criteria = criteria;
    }

    // 静态方法获取实例
    public static SqlUtils builder(Example.Criteria criteria) {
        return new SqlUtils(criteria);
    }

    public <T> SqlUtils eq(Func1<T, ?> func, Object value) {
        String fieldName = LambdaUtil.getFieldName(func);
        return eq(true, fieldName, value);
    }

    public <T> SqlUtils eq(String propertyName, Object value) {
        return eq(true, propertyName, value);
    }

    public <T> SqlUtils eq(boolean condition, Func1<T, ?> func, Object value) {
        if (condition) {
            String propertyName = LambdaUtil.getFieldName(func);
            criteria.andEqualTo(propertyName, value);
        }
        return this;
    }

    // 添加等值查询条件
    public SqlUtils eq(boolean condition, String propertyName, Object value) {
        if (condition) {
            criteria.andEqualTo(propertyName, value);
        }
        return this;
    }

    public SqlUtils notEq(boolean condition, String propertyName, Object value) {
        if (condition) {
            criteria.andNotEqualTo(propertyName, value);
        }
        return this;
    }

    public <T> SqlUtils like(boolean condition, Func1<T, ?> func, String value) {
        return like(condition, LambdaUtil.getFieldName(func), value);
    }

    // 添加 LIKE 查询条件
    public SqlUtils like(boolean condition, String propertyName, String value) {
        if (condition) {
            criteria.andLike(propertyName, value);
        }
        return this;
    }

    public <T> SqlUtils leftLike(Func1<T, ?> func, String value) {
        return leftLike(true, LambdaUtil.getFieldName(func), value);
    }

    public SqlUtils leftLike(boolean condition, String propertyName, String value) {
        if (condition) {
            criteria.andLike(propertyName, "%" + value);
        }
        return this;
    }

    public <T> SqlUtils rightLike(boolean condition, Func1<T, ?> func, String value) {
        if (condition) {
            criteria.andLike(LambdaUtil.getFieldName(func), value + "%");
        }
        return this;
    }

    public SqlUtils rightLike(boolean condition, String propertyName, String value) {
        if (condition) {
            criteria.andLike(propertyName, value + "%");
        }
        return this;
    }

    public SqlUtils pattenLike(boolean condition, String propertyName, String value) {
        if (condition) {
            criteria.andLike(propertyName, "%" + value + "%");
        }
        return this;
    }

    public <T> SqlUtils between(boolean condition, Func1<T, ?> func, Object value1, Object value2) {
        if (condition) {
            criteria.andBetween(LambdaUtil.getFieldName(func), value1, value2);
        }
        return this;
    }

    // 添加 BETWEEN 查询条件
    public SqlUtils between(boolean condition, String propertyName, Object value1, Object value2) {
        if (condition) {
            criteria.andBetween(propertyName, value1, value2);
        }
        return this;
    }

    public SqlUtils and(String condition) {
        criteria.andCondition(condition);
        return this;
    }

    public SqlUtils and(String condition,Object value) {
        criteria.andCondition(condition,value);
        return this;
    }

    public <T> SqlUtils in(boolean condition,Func1<T, ?> func, Iterable value) {
        if (condition) {
            criteria.andIn(LambdaUtil.getFieldName(func), value);
        }
        return this;
    }
    public <T> SqlUtils in(Func1<T, ?> func, Iterable value) {
        criteria.andIn(LambdaUtil.getFieldName(func), value);
        return this;
    }

    public <T> SqlUtils notIn(boolean condition, Func1<T, ?> func, Iterable value) {
        if (condition) {
            criteria.andNotIn(LambdaUtil.getFieldName(func), value);
        }
        return this;
    }

    public <T> SqlUtils notIn(boolean condition, String properties, Iterable value) {
        if (condition) {
            criteria.andNotIn(properties, value);
        }
        return this;
    }

    public <T> SqlUtils notIn(Func1<T, ?> func, Iterable value) {
        criteria.andNotIn(LambdaUtil.getFieldName(func), value);
        return this;
    }

    public <T> SqlUtils notIn(String properties, Iterable value) {
        criteria.andNotIn(properties, value);
        return this;
    }

    public <T> SqlUtils ne(Func1<T, ?> func, Object value) {
        return ne(true, LambdaUtil.getFieldName(func), value);
    }

    public <T> SqlUtils ne(boolean condition, Func1<T, ?> func, Object value) {
        return ne(condition, LambdaUtil.getFieldName(func), value);
    }

    public SqlUtils ne(boolean condition, String propertyName, Object value) {
        if (condition) {
            criteria.andNotEqualTo(propertyName, value);
        }
        return this;
    }

    public <T> SqlUtils orEq(Func1<T, ?> func, Object value) {
        return orEq(true, LambdaUtil.getFieldName(func), value);
    }

    public <T> SqlUtils orEq(boolean condition, Func1<T, ?> func, Object value) {
        return orEq(condition, LambdaUtil.getFieldName(func), value);
    }

    public SqlUtils orEq(boolean condition, String propertyName, Object value) {
        if (condition) {
            criteria.orEqualTo(propertyName, value);
        }
        return this;
    }


    // 最后返回构建好的 Criteria
    public Example.Criteria build() {
        return this.criteria;
    }

}

