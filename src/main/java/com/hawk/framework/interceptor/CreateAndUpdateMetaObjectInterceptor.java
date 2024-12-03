package com.hawk.framework.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.hawk.framework.common.core.base.BaseDataEntity;
import com.hawk.framework.exception.ServiceException;
import com.hawk.framework.helper.LoginHelper;
import com.hawk.framework.model.LoginUser;
import com.hawk.utils.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-12-03 15:28
 */
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class CreateAndUpdateMetaObjectInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取方法参数
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object metaObject = invocation.getArgs()[1];
        // 判断操作类型
        switch (sqlCommandType) {
            case INSERT -> insertFill(metaObject);
            case UPDATE -> updateFill(metaObject);
            default -> {
            }
        }
        return invocation.proceed();
    }

    public void insertFill(Object metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject instanceof BaseDataEntity) {
                BaseDataEntity baseEntity = (BaseDataEntity) metaObject;
                Date current = ObjectUtil.isNotNull(baseEntity.getCreateTime())
                        ? baseEntity.getCreateTime() : new Date();
                baseEntity.setCreateTime(current);
                baseEntity.setUpdateTime(current);
                String username = StringUtils.isNotBlank(baseEntity.getCreateBy())
                        ? baseEntity.getCreateBy() : getLoginUsername();
                // 当前已登录 且 创建人为空 则填充
                baseEntity.setCreateBy(username);
                // 当前已登录 且 更新人为空 则填充
                baseEntity.setUpdateBy(username);
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    public void updateFill(Object metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject instanceof BaseDataEntity) {
                BaseDataEntity baseEntity = (BaseDataEntity) metaObject;
                Date current = new Date();
                // 更新时间填充(不管为不为空)
                baseEntity.setUpdateTime(current);
                String username = getLoginUsername();
                // 当前已登录 更新人填充(不管为不为空)
                if (StringUtils.isNotBlank(username)) {
                    baseEntity.setUpdateBy(username);
                }
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    private String getLoginUsername() {
        LoginUser loginUser;
        try {
            loginUser = LoginHelper.getLoginUser();
        } catch (Exception e) {
            return null;
        }
        return ObjectUtil.isNotNull(loginUser) ? loginUser.getUserAccount() : null;
    }
}
