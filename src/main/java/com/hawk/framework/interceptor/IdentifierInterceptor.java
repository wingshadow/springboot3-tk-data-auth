package com.hawk.framework.interceptor;

import com.hawk.framework.genid.IdentifierGenerator;
import jakarta.persistence.Id;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Component
public class IdentifierInterceptor implements Interceptor {

    private final IdentifierGenerator snowflakeIdGen;

    public IdentifierInterceptor(IdentifierGenerator snowflakeIdGen) {
        this.snowflakeIdGen = snowflakeIdGen;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameter = invocation.getArgs()[1];

        if (parameter != null) {
            Class<?> clazz = parameter.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    Object value = field.get(parameter);

                    if (value == null) { // 如果主键为空，自动生成
                        field.set(parameter, snowflakeIdGen.genId());
                    }
                }
            }
        }

        return invocation.proceed();
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


