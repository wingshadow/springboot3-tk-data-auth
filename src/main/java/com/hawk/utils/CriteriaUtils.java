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
public class CriteriaUtils {

    private final Example.Criteria criteria;

    // 私有构造函数，传入 Criteria 对象
    private CriteriaUtils(Example.Criteria criteria) {
        this.criteria = criteria;
    }

    // 静态方法获取实例
    public static CriteriaUtils builder(Example.Criteria criteria) {
        return new CriteriaUtils(criteria);
    }

    public <T> CriteriaUtils eq(Func1<T, ?> func, Object value) {
        String fieldName = LambdaUtil.getFieldName(func);
        return eq(true, fieldName, value);
    }

    public <T> CriteriaUtils eq(String propertyName, Object value) {
        return eq(true, propertyName, value);
    }

    public <T> CriteriaUtils eq(boolean condition, Func1<T, ?> func, Object value) {
        if (condition) {
            String propertyName = LambdaUtil.getFieldName(func);
            criteria.andEqualTo(propertyName, value);
        }
        return this;
    }

    // 添加等值查询条件
    public CriteriaUtils eq(boolean condition, String propertyName, Object value) {
        if (condition) {
            criteria.andEqualTo(propertyName, value);
        }
        return this;
    }

    public CriteriaUtils notEq(boolean condition, String propertyName, Object value) {
        if (condition) {
            criteria.andNotEqualTo(propertyName, value);
        }
        return this;
    }

    public <T> CriteriaUtils like(boolean condition, Func1<T, ?> func, String value) {
        return like(condition, LambdaUtil.getFieldName(func), value);
    }

    // 添加 LIKE 查询条件
    public CriteriaUtils like(boolean condition, String propertyName, String value) {
        if (condition) {
            criteria.andLike(propertyName, value);
        }
        return this;
    }

    public <T> CriteriaUtils leftLike(Func1<T, ?> func, String value) {
        return leftLike(true, LambdaUtil.getFieldName(func), value);
    }

    public CriteriaUtils leftLike(boolean condition, String propertyName, String value) {
        if (condition) {
            criteria.andLike(propertyName, "%" + value);
        }
        return this;
    }

    public <T> CriteriaUtils rightLike(boolean condition, Func1<T, ?> func, String value) {
        if (condition) {
            criteria.andLike(LambdaUtil.getFieldName(func), value + "%");
        }
        return this;
    }

    public CriteriaUtils rightLike(boolean condition, String propertyName, String value) {
        if (condition) {
            criteria.andLike(propertyName, value + "%");
        }
        return this;
    }

    public CriteriaUtils pattenLike(boolean condition, String propertyName, String value) {
        if (condition) {
            criteria.andLike(propertyName, "%" + value + "%");
        }
        return this;
    }

    public <T> CriteriaUtils between(boolean condition, Func1<T, ?> func, Object value1, Object value2) {
        if (condition) {
            criteria.andBetween(LambdaUtil.getFieldName(func), value1, value2);
        }
        return this;
    }

    // 添加 BETWEEN 查询条件
    public CriteriaUtils between(boolean condition, String propertyName, Object value1, Object value2) {
        if (condition) {
            criteria.andBetween(propertyName, value1, value2);
        }
        return this;
    }

    public CriteriaUtils and(String condition) {
        criteria.andCondition(condition);
        return this;
    }

    public CriteriaUtils and(String condition,Object value) {
        criteria.andCondition(condition,value);
        return this;
    }

    public <T> CriteriaUtils in(boolean condition,Func1<T, ?> func, Iterable value) {
        if (condition) {
            criteria.andIn(LambdaUtil.getFieldName(func), value);
        }
        return this;
    }
    public <T> CriteriaUtils in(Func1<T, ?> func, Iterable value) {
        criteria.andIn(LambdaUtil.getFieldName(func), value);
        return this;
    }

    public <T> CriteriaUtils notIn(boolean condition, Func1<T, ?> func, Iterable value) {
        if (condition) {
            criteria.andNotIn(LambdaUtil.getFieldName(func), value);
        }
        return this;
    }

    public <T> CriteriaUtils notIn(boolean condition, String properties, Iterable value) {
        if (condition) {
            criteria.andNotIn(properties, value);
        }
        return this;
    }

    public <T> CriteriaUtils notIn(Func1<T, ?> func, Iterable value) {
        criteria.andNotIn(LambdaUtil.getFieldName(func), value);
        return this;
    }

    public <T> CriteriaUtils notIn(String properties, Iterable value) {
        criteria.andNotIn(properties, value);
        return this;
    }

    public <T> CriteriaUtils ne(Func1<T, ?> func, Object value) {
        return ne(true, LambdaUtil.getFieldName(func), value);
    }

    public <T> CriteriaUtils ne(boolean condition, Func1<T, ?> func, Object value) {
        return ne(condition, LambdaUtil.getFieldName(func), value);
    }

    public CriteriaUtils ne(boolean condition, String propertyName, Object value) {
        if (condition) {
            criteria.andNotEqualTo(propertyName, value);
        }
        return this;
    }

    public <T> CriteriaUtils orEq(Func1<T, ?> func, Object value) {
        return orEq(true, LambdaUtil.getFieldName(func), value);
    }

    public <T> CriteriaUtils orEq(boolean condition, Func1<T, ?> func, Object value) {
        return orEq(condition, LambdaUtil.getFieldName(func), value);
    }

    public CriteriaUtils orEq(boolean condition, String propertyName, Object value) {
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

