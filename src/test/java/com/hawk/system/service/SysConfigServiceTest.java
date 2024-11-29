package com.hawk.system.service;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.hawk.App;
import com.hawk.system.entity.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class})
public class SysConfigServiceTest {
    @Autowired
    private SysConfigService service;

    @Test
    public void test() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigName("主框架页");
        sysConfig.setConfigType("Y");
        PageInfo<SysConfig> pageInfo = service.selectPageConfigList(sysConfig, 1, 10);
        log.info("{}", JSONUtil.toJsonStr(pageInfo));
    }
}