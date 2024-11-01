package com.hawk.framework.enums;

import com.hawk.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型
 * 针对多套 用户体系
 *
 * @author Lion Li
 */
@Getter
@AllArgsConstructor
public enum UserType {
    /**
     * 系统用户
     */
    sys_user("sys_user", "系统用户"),
    /**
     * 内部员工
     */
    employee("employee", "内部员工");


    private final String userType;

    private final String msg;

    public static UserType getUserType(String str) {
        for (UserType value : values()) {
            if (StringUtils.contains(str, value.getUserType())) {
                return value;
            }
        }
        throw new RuntimeException("'UserType' not found By " + str);
    }
}
