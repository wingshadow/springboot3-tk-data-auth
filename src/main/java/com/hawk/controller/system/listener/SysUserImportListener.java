package com.hawk.controller.system.listener;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hawk.controller.system.vo.SysUserImportVO;
import com.hawk.framework.excel.ExcelListener;
import com.hawk.framework.excel.ExcelResult;
import com.hawk.framework.exception.ServiceException;
import com.hawk.framework.helper.LoginHelper;
import com.hawk.system.entity.SysUser;
import com.hawk.system.service.SysConfigService;
import com.hawk.system.service.SysUserService;
import com.hawk.utils.SpringUtils;
import com.hawk.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 系统用户自定义导入
 *
 * @author Lion Li
 */
@Slf4j
public class SysUserImportListener extends AnalysisEventListener<SysUserImportVO> implements ExcelListener<SysUserImportVO> {

    private final SysUserService userService;

    private final String password;

    private final Boolean isUpdateSupport;

    private final String operName;

    private int successNum = 0;
    private int failureNum = 0;
    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();

    public SysUserImportListener(Boolean isUpdateSupport) {
        String initPassword = SpringUtils.getBean(SysConfigService.class).selectConfigByKey("sys.user.initPassword");
        this.userService = SpringUtils.getBean(SysUserService.class);
        this.password = BCrypt.hashpw(initPassword);
        this.isUpdateSupport = isUpdateSupport;
        this.operName = LoginHelper.getUserAccount();
    }

    @Override
    public void invoke(SysUserImportVO userVo, AnalysisContext context) {
        SysUser user = this.userService.selectUserByUserName(userVo.getUserAccount());
        try {
            // 验证是否存在这个用户
            if (ObjectUtil.isNull(user)) {
                user = BeanUtil.toBean(userVo, SysUser.class);
                ValidatorUtils.validate(user);
                user.setPassword(password);
                user.setCreateBy(operName);
                userService.insertSelective(user);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 导入成功");
            } else if (isUpdateSupport) {
                Long userId = user.getUserId();
                user = BeanUtil.toBean(userVo, SysUser.class);
                user.setUserId(userId);
                ValidatorUtils.validate(user);
                userService.checkUserAllowed(user);
                userService.checkUserDataScope(user.getUserId());
                user.setUpdateBy(operName);
                userService.updateByPrimaryKeySelective(user);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 更新成功");
            } else {
                failureNum++;
                failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName()).append(" 已存在");
            }
        } catch (Exception e) {
            failureNum++;
            String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
            failureMsg.append(msg).append(e.getMessage());
            log.error(msg, e);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    @Override
    public ExcelResult<SysUserImportVO> getExcelResult() {
        return new ExcelResult<SysUserImportVO>() {

            @Override
            public String getAnalysis() {
                if (failureNum > 0) {
                    failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
                    throw new ServiceException(failureMsg.toString());
                } else {
                    successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
                }
                return successMsg.toString();
            }

            @Override
            public List<SysUserImportVO> getList() {
                return null;
            }

            @Override
            public List<String> getErrorList() {
                return null;
            }
        };
    }
}
