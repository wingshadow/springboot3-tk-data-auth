package com.hawk.system.service;

import com.hawk.App;
import com.hawk.biz.customer.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-12-09 10:36
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class})
public class CustomerServiceTest {
    @Autowired
    private CustomerService customerService;

    @Test
    public void testDelete(){
        List<Long> list = Arrays.asList(1L,2L);
        customerService.deleteBatch(list);
    }

    @Test
    public void testLogicDelete(){
        List<Long> list = Arrays.asList(1L,2L);
        customerService.deleteLogicBatch(list);
    }
}
