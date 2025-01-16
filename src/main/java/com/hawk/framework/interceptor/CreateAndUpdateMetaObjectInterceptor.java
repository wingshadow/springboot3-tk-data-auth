package com.hawk.framework.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.hawk.framework.common.core.base.BaseDataEntity;
import com.hawk.framework.exception.ServiceException;
import com.hawk.framework.helper.LoginHelper;
import com.hawk.framework.model.LoginUser;
import com.hawk.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: springboot3-tk-data-auth
 * @description: 创建时间和创建人、更新时间和更新人自己插入拦截器
 * @author: zhb
 * @create: 2024-12-03 15:28
 */
@Slf4j
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
            e.printStackTrace();
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

            // 遍历 keySet，获取所有的键
            for (Object key : paramMap.keySet()) {
                Object value = paramMap.get(key);
                // 如果需要，根据 key 进一步处理 value
                if ("record".equals(key)) {
                    // 如果找到 "record" 键，处理逻辑
                    Object record = value;
                    fillSingleEntity(record, isInsert);
                } else if ("recordList".equals(key)) {
                    // 如果是 "recordList" 键，处理集合
                    Object recordList = value;
                    if (recordList instanceof List) {
                        for (Object item : (List<?>) recordList) {
                            fillSingleEntity(item, isInsert);
                        }
                    }
                }
            }

            return; // 结束当前方法执行
        }
        fillSingleEntity(metaObject,isInsert);
    }

    /**
     * 处理单个实体对象的填充逻辑
     */
    private void fillSingleEntity(Object metaObject, boolean isInsert) {
        if (metaObject instanceof BaseDataEntity) {
            BaseDataEntity entity = (BaseDataEntity) metaObject;
            Date current = new Date();

            if (isInsert) {
                // 插入操作：设置 createTime 和 createBy
                entity.setCreateTime(ObjectUtil.isNotNull(entity.getCreateTime()) ? entity.getCreateTime() : current);
                entity.setCreateBy(StringUtils.isNotBlank(entity.getCreateBy()) ? entity.getCreateBy() : getLoginUsername());
            }

            // 无论是插入还是更新操作，设置 updateTime 和 updateBy
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
