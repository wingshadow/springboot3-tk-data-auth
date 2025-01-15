package com.hawk.framework.interceptor;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hawk.framework.annotation.scope.DataScope;
import com.hawk.framework.helper.LoginHelper;
import com.hawk.framework.model.LoginUser;
import com.hawk.utils.SqlBuilder;
import com.hawk.utils.StreamUtils;
import com.hawk.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-10-20 09:27
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DataPermissionInterceptor implements Interceptor {
    private final Map<String, DataScope> dataScopeMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");


        String statementId = mappedStatement.getId();
        if (!mappedStatement.getSqlCommandType().name().equalsIgnoreCase("SELECT")) {
            return invocation.proceed();
        }


        if (statementId.endsWith("_COUNT")) {
            // 去掉 _COUNT 后缀，获取原始查询方法的名称
            statementId = statementId.substring(0, statementId.lastIndexOf("_COUNT"));
        }
        Map<String, String> alias = findAnnotation(statementId);
        if (CollUtil.isEmpty(alias)) {
            return invocation.proceed();
        }


        BoundSql boundSql = statementHandler.getBoundSql();
        String originalSql = boundSql.getSql();
        log.info("Original SQL: " + originalSql);

        Object parameterObject = boundSql.getParameterObject();
        // 查询条件
//        String whereCause = findWhereCause(parameterObject);
        String whereCause = "";
        log.info("whereCause:{}", whereCause);
        // 权限条件
        String permissionSql = buildDataFilterSqlByLoginUser(alias);
        log.info("permissionSql:{}", permissionSql);

        // 开始拼接 SQL

        // 判断是否存在 WHERE 子句
        String originalSqlLower = originalSql.trim().toLowerCase();
        String sql = SqlBuilder.getSqlBeforeLimit(originalSqlLower);
        String limit = SqlBuilder.getSqlIncludingLimit(originalSqlLower);

        StringBuilder modifiedSqlBuilder = new StringBuilder(sql);


        boolean hasWhereClause = sql.contains("where");

        // 如果权限条件不为空，拼接权限条件
        if (StringUtils.isNotBlank(permissionSql)) {
            if (hasWhereClause) {
                modifiedSqlBuilder.append(" and (").append(permissionSql).append(")").append(" ");
            } else {
                modifiedSqlBuilder.append(" where (").append(permissionSql).append(")").append(" ");
                hasWhereClause = true; // 设定已添加 WHERE
            }
        }

        // 如果查询条件不为空，拼接查询条件
        if (StringUtils.isNotBlank(whereCause)) {
            if (hasWhereClause) {
                modifiedSqlBuilder.append(" and ").append(whereCause).append(" ");
            } else {
                modifiedSqlBuilder.append(" where ").append(whereCause).append(" ");
            }
        }

        // 获取拼接后的 SQL
        String modifiedSql = modifiedSqlBuilder.toString();
        log.info("Modified SQL: {}", modifiedSql);

        Field sqlField = BoundSql.class.getDeclaredField("sql");
        sqlField.setAccessible(true);
        sqlField.set(boundSql, modifiedSql + " " + limit);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    // 从登陆用户信息中获取管理部门
    public String buildDataFilterSqlByLoginUser(Map<String, String> alias) {
        StringJoiner whereCause = new StringJoiner("");
        if (LoginHelper.isAdmin()) {
            return "";
        }
        LoginUser loginUser = LoginHelper.getLoginUser();
        List<Long> sysDeptList = loginUser.getDepts();
        if (CollUtil.isNotEmpty(sysDeptList)) {
            whereCause.add(String.format("%s.dept_id in (%s)", alias.get("dept"),
                    StreamUtils.join(sysDeptList, d -> String.valueOf(d.longValue()))));
        } else {
            whereCause.add(" 1= 0 ");
        }
        return whereCause.toString();
    }

    private String findWhereCause(Object paramObj) {
        String whereCause = "";
        if (paramObj instanceof Map) {
            Map<String, String> paramMap = (Map<String, String>) paramObj;
            if (StringUtils.isNotBlank(paramMap.get("param1"))) {
                whereCause = paramMap.get("param1");
            }
        }
        return whereCause;
    }

    private Map<String, String> findAnnotation(String mappedStatementId) {
        StringBuilder sb = new StringBuilder(mappedStatementId);
        int index = sb.lastIndexOf(".");
        String clazzName = sb.substring(0, index);
        String methodName = sb.substring(index + 1, sb.length());
        Class<?> clazz = ClassUtil.loadClass(clazzName);
        List<Method> methods = Arrays.stream(ClassUtil.getDeclaredMethods(clazz))
                .filter(method -> method.getName().equals(methodName)).collect(Collectors.toList());
        DataScope dataScope;

        for (Method method : methods) {
            dataScope = dataScopeMap.get(mappedStatementId);
            if (ObjectUtil.isNotNull(dataScope)) {
                return getDataScopeValue(dataScope);
            }
            if (AnnotationUtil.hasAnnotation(method, DataScope.class)) {
                dataScope = AnnotationUtil.getAnnotation(method, DataScope.class);
                dataScopeMap.put(mappedStatementId, dataScope);
                return getDataScopeValue(dataScope);
            }
        }

        if (AnnotationUtil.hasAnnotation(clazz, DataScope.class)) {
            dataScope = AnnotationUtil.getAnnotation(clazz, DataScope.class);
            return getDataScopeValue(dataScope);
        }
        return null;
    }

    private Map<String, String> getDataScopeValue(DataScope dataScope) {
        Map<String, String> attributes = new HashMap<>();
        if (dataScope != null) {
            if (StringUtils.isNotBlank(dataScope.deptAlias())) {
                attributes.put("dept", dataScope.deptAlias());
            }
            if (StringUtils.isNotBlank(dataScope.userAlias())) {
                attributes.put("user", dataScope.userAlias());
            }
        }
        return attributes;
    }
}
