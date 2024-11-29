package com.hawk.system.service.impl;

import cn.hutool.core.lang.func.LambdaUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.hawk.framework.base.BaseServiceImpl;
import com.hawk.system.entity.SysDictData;
import com.hawk.system.mapper.SysDictDataMapper;
import com.hawk.system.service.SysDictDataService;
import com.hawk.utils.CriteriaUtils;
import com.hawk.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 字典 业务层处理
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysDictDataServiceImpl extends BaseServiceImpl<SysDictData> implements SysDictDataService {

    private final SysDictDataMapper baseMapper;

    @Override
    public PageInfo<SysDictData> selectPageDictDataList(SysDictData dictData, int pageNum,int pageSize) {
        Example example = new Example(SysDictData.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(StringUtils.isNotBlank(dictData.getDictType()), SysDictData::getDictType, dictData.getDictType())
                .like(StringUtils.isNotBlank(dictData.getDictLabel()), SysDictData::getDictLabel, dictData.getDictLabel())
                .eq(StringUtils.isNotBlank(dictData.getStatus()), SysDictData::getStatus, dictData.getStatus());
        example.orderBy(LambdaUtil.getFieldName(SysDictData::getDictSort)).asc();
        PageMethod.startPage(pageNum,pageSize);
        List<SysDictData> list = baseMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        Example example = new Example(SysDictData.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(StringUtils.isNotBlank(dictData.getDictType()), SysDictData::getDictType, dictData.getDictType())
                .like(StringUtils.isNotBlank(dictData.getDictLabel()), SysDictData::getDictLabel, dictData.getDictLabel())
                .eq(StringUtils.isNotBlank(dictData.getStatus()), SysDictData::getStatus, dictData.getStatus());
        example.orderBy(LambdaUtil.getFieldName(SysDictData::getDictSort)).asc();
        return baseMapper.selectByExample(example);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        Example example = new Example(SysDictData.class);
        CriteriaUtils.builder(example.createCriteria())
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue);
        return baseMapper.selectOneByExample(example).getDictLabel();
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return baseMapper.selectByPrimaryKey(dictCode);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictData data = selectDictDataById(dictCode);
            baseMapper.deleteByPrimaryKey(dictCode);
//            CacheUtils.evict(CacheNames.SYS_DICT, data.getDictType());
        }
    }

    /**
     * 新增保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
//    @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#data.dictType")
    @Override
    public List<SysDictData> insertDictData(SysDictData data) {
        int row = baseMapper.insert(data);
        if (row > 0) {
            return baseMapper.selectDictDataByType(data.getDictType());
        }
        throw new RuntimeException("操作失败");
    }

    /**
     * 修改保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
//    @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#data.dictType")
    @Override
    public List<SysDictData> updateDictData(SysDictData data) {
        int row = baseMapper.updateByPrimaryKeySelective(data);
        if (row > 0) {
            return baseMapper.selectDictDataByType(data.getDictType());
        }
        throw new RuntimeException("操作失败");
    }

}
