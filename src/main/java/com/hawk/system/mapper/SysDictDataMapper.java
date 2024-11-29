package com.hawk.system.mapper;

import cn.hutool.core.lang.func.LambdaUtil;
import com.hawk.framework.common.constant.UserConstants;
import com.hawk.framework.base.BaseMapper;
import com.hawk.system.entity.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 字典表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    default List<SysDictData> selectDictDataByType(String dictType) {
        Example example = new Example(SysDictData.class);
        String dictTypeName = LambdaUtil.getFieldName(SysDictData::getDictType);
        String statusName = LambdaUtil.getFieldName(SysDictData::getStatus);
        example.createCriteria().andEqualTo(dictTypeName,dictType).andEqualTo(statusName,UserConstants.DICT_NORMAL);
        example.orderBy(LambdaUtil.getFieldName(SysDictData::getDictSort)).asc();
        return selectByExample(example);
    }

    default boolean exists(Example example) {
        int count = this.selectCountByExample(example);
        return count > 0;
    }
}
