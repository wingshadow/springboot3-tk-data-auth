package com.hawk.system.service.impl;

import cn.dev33.satoken.context.SaHolder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.hawk.framework.common.constant.CacheConstants;
import com.hawk.framework.common.constant.UserConstants;
import com.hawk.framework.base.BaseServiceImpl;
import com.hawk.system.entity.SysDictData;
import com.hawk.system.entity.SysDictType;
import com.hawk.framework.service.DictService;
import com.hawk.system.mapper.SysDictDataMapper;
import com.hawk.system.mapper.SysDictTypeMapper;
import com.hawk.system.service.SysDictTypeService;
import com.hawk.utils.SpringUtils;
import com.hawk.utils.CriteriaUtils;
import com.hawk.utils.StreamUtils;
import com.hawk.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysDictTypeServiceImpl extends BaseServiceImpl<SysDictType> implements SysDictTypeService, DictService {

    private final SysDictTypeMapper baseMapper;
    private final SysDictDataMapper dictDataMapper;

    @Override
    public PageInfo<SysDictType> selectPageDictTypeList(SysDictType dictType, int pageNum, int pageSize) {
        Map<String, Object> params = dictType.getParams();
        Example example = new Example(SysDictType.class);
        CriteriaUtils.builder(example.createCriteria())
                .like(StringUtils.isNotBlank(dictType.getDictName()), SysDictType::getDictName, dictType.getDictName())
                .eq(StringUtils.isNotBlank(dictType.getStatus()), SysDictType::getStatus, dictType.getStatus())
                .like(StringUtils.isNotBlank(dictType.getDictType()), SysDictType::getDictType, dictType.getDictType())
                .between(params.get("beginTime") != null && params.get("endTime") != null,
                        SysDictType::getCreateTime, params.get("beginTime"), params.get("endTime"));

        PageMethod.startPage(pageNum, pageSize);
        List<SysDictType> list = baseMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        Map<String, Object> params = dictType.getParams();
        Example example = new Example(SysDictType.class);
        CriteriaUtils.builder(example.createCriteria())
                .like(StringUtils.isNotBlank(dictType.getDictName()), SysDictType::getDictName, dictType.getDictName())
                .eq(StringUtils.isNotBlank(dictType.getStatus()), SysDictType::getStatus, dictType.getStatus())
                .like(StringUtils.isNotBlank(dictType.getDictType()), SysDictType::getDictType, dictType.getDictType())
                .between(params.get("beginTime") != null && params.get("endTime") != null,
                        SysDictType::getCreateTime, params.get("beginTime"), params.get("endTime"));
        return baseMapper.selectByExample(example);

    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll() {
        return baseMapper.selectAll();
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
//    @Cacheable(cacheNames = CacheNames.SYS_DICT, key = "#dictType")
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType);
        if (CollUtil.isNotEmpty(dictDatas)) {
            return dictDatas;
        }
        return null;
    }

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return baseMapper.selectByPrimaryKey(dictId);
    }

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
//    @Cacheable(cacheNames = CacheNames.SYS_DICT, key = "#dictType")
    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        Example example = new Example(SysDictType.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(SysDictType::getDictType, dictType);
        return baseMapper.selectOneByExample(example);
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = selectDictTypeById(dictId);
            Example example = new Example(SysDictData.class);
            CriteriaUtils.builder(example.createCriteria())
                    .eq(SysDictData::getDictType, dictType.getDictType());

            if (dictDataMapper.exists(example)) {
                throw new RuntimeException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
//            CacheUtils.evict(CacheNames.SYS_DICT, dictType.getDictType());
        }
        Example example = new Example(SysDictType.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn(LambdaUtil.getFieldName(SysDictType::getDictId), Arrays.asList(dictIds));
        baseMapper.deleteByExample(example);
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {
        Example example = new Example(SysDictData.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(SysDictData::getStatus, UserConstants.DICT_NORMAL);
        List<SysDictData> dictDataList = dictDataMapper.selectByExample(example);
        Map<String, List<SysDictData>> dictDataMap = StreamUtils.groupByKey(dictDataList, SysDictData::getDictType);

        dictDataMap.forEach((k, v) -> {
            List<SysDictData> dictList = StreamUtils.sorted(v, Comparator.comparing(SysDictData::getDictSort));
//            CacheUtils.put(CacheNames.SYS_DICT, k, dictList);
        });
    }

    /**
     * 清空字典缓存数据
     */
    @Override
    public void clearDictCache() {
//        CacheUtils.clear(CacheNames.SYS_DICT);
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
//    @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#dict.dictType")
    @Override
    public List<SysDictData> insertDictType(SysDictType dict) {
        int row = baseMapper.insert(dict);
        if (row > 0) {
            return new ArrayList<>();
        }
        throw new RuntimeException("操作失败");
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
//    @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#dict.dictType")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SysDictData> updateDictType(SysDictType dict) {
        SysDictType oldDict = baseMapper.selectByPrimaryKey(dict.getDictId());

        Example example = new Example(SysDictData.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(SysDictData::getDictType, oldDict.getDictType());
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType(dict.getDictType());
        dictDataMapper.updateByExampleSelective(sysDictData, example);
        int row = baseMapper.updateByPrimaryKey(dict);
        if (row > 0) {
//            CacheUtils.evict(CacheNames.SYS_DICT, oldDict.getDictType());
            return dictDataMapper.selectDictDataByType(dict.getDictType());
        }
        throw new RuntimeException("操作失败");
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public boolean checkDictTypeUnique(SysDictType dict) {
        Example example = new Example(SysDictType.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(SysDictType::getDictType, dict.getDictType())
                .ne(ObjectUtil.isNotNull(dict.getDictId()), SysDictType::getDictId, dict.getDictId());
        boolean exist = baseMapper.exists(example);
        return !exist;
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    @SuppressWarnings("unchecked cast")
    @Override
    public String getDictLabel(String dictType, String dictValue, String separator) {
        // 优先从本地缓存获取
        List<SysDictData> datas = (List<SysDictData>) SaHolder.getStorage().get(CacheConstants.SYS_DICT_KEY + dictType);
        if (ObjectUtil.isNull(datas)) {
            datas = SpringUtils.getAopProxy(this).selectDictDataByType(dictType);
            SaHolder.getStorage().set(CacheConstants.SYS_DICT_KEY + dictType, datas);
        }

        Map<String, String> map = StreamUtils.toMap(datas, SysDictData::getDictValue, SysDictData::getDictLabel);
        if (StringUtils.containsAny(dictValue, separator)) {
            return Arrays.stream(dictValue.split(separator))
                    .map(v -> map.getOrDefault(v, StringUtils.EMPTY))
                    .collect(Collectors.joining(separator));
        } else {
            return map.getOrDefault(dictValue, StringUtils.EMPTY);
        }
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    @SuppressWarnings("unchecked cast")
    @Override
    public String getDictValue(String dictType, String dictLabel, String separator) {
        // 优先从本地缓存获取
        List<SysDictData> datas = (List<SysDictData>) SaHolder.getStorage().get(CacheConstants.SYS_DICT_KEY + dictType);
        if (ObjectUtil.isNull(datas)) {
            datas = SpringUtils.getAopProxy(this).selectDictDataByType(dictType);
            SaHolder.getStorage().set(CacheConstants.SYS_DICT_KEY + dictType, datas);
        }

        Map<String, String> map = StreamUtils.toMap(datas, SysDictData::getDictLabel, SysDictData::getDictValue);
        if (StringUtils.containsAny(dictLabel, separator)) {
            return Arrays.stream(dictLabel.split(separator))
                    .map(l -> map.getOrDefault(l, StringUtils.EMPTY))
                    .collect(Collectors.joining(separator));
        } else {
            return map.getOrDefault(dictLabel, StringUtils.EMPTY);
        }
    }

}
