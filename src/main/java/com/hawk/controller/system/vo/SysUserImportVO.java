package com.hawk.controller.system.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.hawk.framework.annotation.excel.ExcelDictFormat;
import com.hawk.framework.excel.convert.ExcelDictConvert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-12-13 15:07
 */
@Data
@NoArgsConstructor
public class SysUserImportVO implements Serializable {

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户序号")
    private Long userId;

    /**
     * 部门ID
     */
    @ExcelProperty(value = "部门编号")
    private Long deptId;

    /**
     * 用户账号
     */
    @ExcelProperty(value = "登录名称")
    private String userAccount;

    /**
     * 用户昵称
     */
    @ExcelProperty(value = "用户名称")
    private String userName;

    /**
     * 用户邮箱
     */
    @ExcelProperty(value = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @ExcelProperty(value = "手机号码")
    private String mobile;

    /**
     * 用户性别
     */
    @ExcelProperty(value = "用户性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_user_sex")
    private String gender;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ExcelProperty(value = "帐号状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;
}
