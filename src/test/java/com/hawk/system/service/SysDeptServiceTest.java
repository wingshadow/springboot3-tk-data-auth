package com.hawk.system.service;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.hawk.App;
import com.hawk.system.entity.SysDept;
import com.hawk.system.mapper.SysDeptMapper;
import com.hawk.utils.CriteriaUtils;
import com.hawk.utils.SqlBuilder;
import com.hawk.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class})
public class SysDeptServiceTest {

    @Autowired
    private SysDeptService deptService;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysRoleService roleService;
    @Test
    public void test(){
        SysDept dept = new SysDept();
        dept.setDeptName("总务部");
        /*List<SysDept> list =  deptService.selectDeptList(dept);
        log.info("{}", JSONUtil.toJsonStr(list));
*/
        PageInfo<SysDept> page =  deptService.selectPageList(dept, 1,1);
        log.info("{}", JSONUtil.toJsonStr(page));
    }

    @Test
    public void test2(){
        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).eq(SysDept::getDeptName,"销售部")
                        .in(SysDept::getDeptId, Arrays.asList("q","w"));
        System.out.println(SqlBuilder.buildWhereClause(example,"d"));
    }

    @Test
    public void test3(){
//        SysRoleService roleService = SpringUtils.getBean(SysRoleService.class);
//        List<SysRole> sysRoleList = roleService.getRoleByUserId(1783753327736221697L);
        Example example = new Example(SysDept.class);
        CriteriaUtils.builder(example.createCriteria()).eq(SysDept::getDeptId,1L);
        CriteriaUtils.builder(example.or()).eq(SysDept::getDeptId,1784405825652178946L);
        List<SysDept> list = deptMapper.selectByExample(example);
    }
}