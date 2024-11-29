package com.hawk.system.mapper;


import com.hawk.framework.base.BaseMapper;
import com.hawk.system.entity.SysDictType;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.entity.Example;

/**
 * 字典表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    default boolean exists(Example example) {
        int count = this.selectCountByExample(example);
        return count > 0;
    }
}
