package com.hawk.system.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.hawk.framework.common.constant.UserConstants;
import com.hawk.framework.base.BaseServiceImpl;
import com.hawk.framework.entity.SysConfig;
import com.hawk.framework.service.ConfigService;
import com.hawk.system.mapper.SysConfigMapper;
import com.hawk.system.service.SysConfigService;
import com.hawk.utils.SpringUtils;
import com.hawk.utils.CriteriaUtils;
import com.hawk.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 参数配置 服务层实现
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfig> implements SysConfigService, ConfigService {

    private final SysConfigMapper baseMapper;

    @Override
    public PageInfo<SysConfig> selectPageConfigList(SysConfig config, int pageNum, int pageSize) {
        Map<String, Object> params = config.getParams();
        Example example = new Example(SysConfig.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).rightLike(StringUtils.isNotBlank(config.getConfigName()),
                        SysConfig::getConfigName, config.getConfigName())
                .eq(StringUtils.isNotBlank(config.getConfigType()),
                        SysConfig::getConfigType, config.getConfigType())
                .between(params.get("beginTime") != null && params.get("endTime") != null,
                        LambdaUtil.getFieldName(SysConfig::getCreateTime), params.get("beginTime"), params.get("endTime"));


        PageMethod.startPage(pageNum, pageSize);
        List<SysConfig> list = baseMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfig selectConfigById(Long configId) {
        return baseMapper.selectByPrimaryKey(configId);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        SysConfig retConfig = new SysConfig();
        retConfig.setConfigKey(configKey);
        retConfig = baseMapper.selectOne(retConfig);
        if (ObjectUtil.isNotNull(retConfig)) {
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled() {
        String captchaEnabled = SpringUtils.getAopProxy(this).selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled)) {
            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        Map<String, Object> params = config.getParams();

        Example example = new Example(SysConfig.class);
        example.createCriteria()
                .andLike(LambdaUtil.getFieldName(SysConfig::getConfigName), config.getConfigName())
                .andEqualTo(LambdaUtil.getFieldName(SysConfig::getConfigType), config.getConfigType())
                .andLike(LambdaUtil.getFieldName(SysConfig::getConfigKey), config.getConfigKey())
                .andBetween(LambdaUtil.getFieldName(SysConfig::getCreateTime), params.get("beginTime"), params.get("endTime"));

        return baseMapper.selectByExample(example);
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String insertConfig(SysConfig config) {
        int row = baseMapper.insert(config);
        if (row > 0) {
            return config.getConfigValue();
        }
        throw new RuntimeException("操作失败");
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String updateConfig(SysConfig config) {
        int row = 0;
        if (config.getConfigId() != null) {
            SysConfig temp = baseMapper.selectByPrimaryKey(config.getConfigId());
            if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey())) {
//                CacheUtils.evict(CacheNames.SYS_CONFIG, temp.getConfigKey());
            }
            row = baseMapper.updateByPrimaryKey(config);
        } else {
            Example example = new Example(SysConfig.class);
            example.createCriteria().andEqualTo(LambdaUtil.getFieldName(SysConfig::getConfigKey), config.getConfigKey());
            row = baseMapper.updateByExample(config, example);
        }
        if (row > 0) {
            return config.getConfigValue();
        }
        throw new RuntimeException("操作失败");
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {
                throw new RuntimeException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
//            CacheUtils.evict(CacheNames.SYS_CONFIG, config.getConfigKey());
        }
        Example example = new Example(SysConfig.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("configId", Arrays.asList(configIds));
        baseMapper.deleteByExample(example);
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configsList = selectConfigList(new SysConfig());
//        configsList.forEach(config ->
//            CacheUtils.put(CacheNames.SYS_CONFIG, config.getConfigKey(), config.getConfigValue()));
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
//        CacheUtils.clear(CacheNames.SYS_CONFIG);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        long configId = ObjectUtil.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = new SysConfig();
        info.setConfigKey(config.getConfigKey());
        info = baseMapper.selectOne(info);
        if (ObjectUtil.isNotNull(info) && info.getConfigId() != configId) {
            return false;
        }
        return true;
    }

    /**
     * 根据参数 key 获取参数值
     *
     * @param configKey 参数 key
     * @return 参数值
     */
    @Override
    public String getConfigValue(String configKey) {
        return SpringUtils.getAopProxy(this).selectConfigByKey(configKey);
    }

}
