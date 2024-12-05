package com.hawk.system.service;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.hawk.App;
import com.hawk.generator.entity.GenTable;
import com.hawk.generator.mapper.GenTableMapper;
import com.hawk.system.entity.SysConfig;
import com.hawk.system.entity.SysDictData;
import com.hawk.system.mapper.SysDictDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class})
public class SysConfigServiceTest {
    @Autowired
    private SysConfigService service;

    @Autowired
    private GenTableMapper genTableMapper;

    @Autowired
    private SysDictDataMapper sysDictDataMapper;

    @Test
    public void test() {
//        SysConfig sysConfig = new SysConfig();
//        sysConfig.setConfigName("主框架页");
//        sysConfig.setConfigType("Y");
//        PageInfo<SysConfig> pageInfo = service.selectPageConfigList(sysConfig, 1, 10);

//        GenTable gentable = genTableMapper.selectByPrimaryKey(1863844597178748928L);
//        log.info("{}", JSONUtil.toJsonStr(gentable));

        SysDictData data = new SysDictData();
        data.setDictCode(1L);
        data.setStatus("1");

        SysDictData data2 = new SysDictData();
        data2.setDictCode(2L);
        data2.setStatus("1");

        List<SysDictData> list = new ArrayList<>();
        list.add(data2);
        list.add(data);
        sysDictDataMapper.updateBatchSelective(list);
    }
}