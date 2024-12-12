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

import java.util.Date;
import java.util.Map;

/**
 * @program: springboot3-tk-data-auth
 * @description: 创建时间和创建人、更新时间和更新人自己插入拦截器
 * @author: zhb
 * @create: 2024-12-03 15:28
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class CreateAndUpdateMetaObjectInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object metaObject = invocation.getArgs()[1];

        // 根据操作类型填充字段
        try {
            if (sqlCommandType == SqlCommandType.INSERT) {
                fillFields(metaObject, true);
            } else if (sqlCommandType == SqlCommandType.UPDATE) {
                fillFields(metaObject, false);
            }
        } catch (Exception e) {
            throw new ServiceException("自动填充异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }

        return invocation.proceed();
    }

    /**
     * 通用字段填充方法
     *
     * @param metaObject   参数对象
     * @param isInsert     是否为插入操作
     */
    private void fillFields(Object metaObject, boolean isInsert) {
        if (metaObject instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) metaObject;
            metaObject = paramMap.get("record");
        }

        if (metaObject instanceof BaseDataEntity) {
            BaseDataEntity entity = (BaseDataEntity) metaObject;
            Date current = new Date();

            if (isInsert) {
                entity.setCreateTime(ObjectUtil.isNotNull(entity.getCreateTime()) ? entity.getCreateTime() : current);
                entity.setCreateBy(StringUtils.isNotBlank(entity.getCreateBy()) ? entity.getCreateBy() : getLoginUsername());
            }

            entity.setUpdateTime(current);
            entity.setUpdateBy(StringUtils.isNotBlank(entity.getUpdateBy()) ? entity.getUpdateBy() : getLoginUsername());
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
