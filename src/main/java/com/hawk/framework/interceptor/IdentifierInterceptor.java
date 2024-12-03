package com.hawk.framework.interceptor;

import com.hawk.framework.annotation.database.TableId;
import com.hawk.framework.genid.IdentifierGenerator;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

/**
 * @program: springboot3-tk-data-auth
 * @description: 主键自动填充拦截器
 * @author: zhb
 * @create: 2024-12-03 15:28
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class IdentifierInterceptor implements Interceptor {

    private final IdentifierGenerator snowflakeIdGen;

    public IdentifierInterceptor(IdentifierGenerator snowflakeIdGen) {
        this.snowflakeIdGen = snowflakeIdGen;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取方法参数
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        // 判断是否是插入操作
        if (isInsertMethod(mappedStatement.getId())) {
            if (parameter instanceof Map) {
                // 如果参数是 Map，可能是批量操作
                handleBatchInsert((Map<?, ?>) parameter);
            } else {
                // 单个实体类插入
                handleSingleInsert(parameter);
            }
        }
        // 执行原方法
        return invocation.proceed();
    }

    /**
     * 判断是否为插入方法
     */
    private boolean isInsertMethod(String methodName) {
        return methodName.endsWith("insert") || methodName.endsWith("insertSelective") || methodName.endsWith("insertList");
    }

    /**
     * 处理单个实体类插入
     */
    private void handleSingleInsert(Object parameter) {
        if (parameter == null) {
            return;
        }

        // 反射设置主键
        for (Field field : parameter.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(TableId.class)) {
                field.setAccessible(true);
                try {
                    // 检查主键是否为空
                    if (field.get(parameter) == null) {
                        // 设置主键（例如使用雪花算法）
                        field.set(parameter, snowflakeIdGen.genId());
                        System.out.println("Generated ID for " + parameter.getClass().getSimpleName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to set ID field", e);
                }
            }
        }
    }

    /**
     * 处理批量插入
     */
    private void handleBatchInsert(Map<?, ?> parameterMap) {
        Object listParam = parameterMap.get("list");
        if (listParam instanceof Iterable) {
            for (Object entity : (Iterable<?>) listParam) {
                handleSingleInsert(entity);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可扩展：从配置中读取自定义属性
    }
}


