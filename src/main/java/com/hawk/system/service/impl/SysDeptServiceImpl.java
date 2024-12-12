package com.hawk.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.hawk.framework.common.constant.UserConstants;
import com.hawk.framework.base.BaseServiceImpl;
import com.hawk.system.entity.SysDept;
import com.hawk.system.entity.SysRole;
import com.hawk.system.entity.SysUser;
import com.hawk.framework.helper.DataBaseHelper;
import com.hawk.framework.helper.LoginHelper;
import com.hawk.framework.service.DeptService;
import com.hawk.system.mapper.SysDeptMapper;
import com.hawk.system.mapper.SysRoleMapper;
import com.hawk.system.mapper.SysUserMapper;
import com.hawk.system.service.SysDeptService;
import com.hawk.system.vo.SysDeptVO;
import com.hawk.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: springboot3-mybatis
 * @description:
 * @author: zhb
 * @create: 2024-04-26 15:10
 */
@Service
public class SysDeptServiceImpl extends BaseServiceImpl<SysDept> implements SysDeptService, DeptService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Override
    public String selectDeptNameByIds(String deptIds) {
        List<String> list = new ArrayList<>();
        for (Long id : StringUtils.splitTo(deptIds, Convert::toLong)) {
            SysDept dept = SpringUtils.getAopProxy(this).selectDeptById(id);
            if (ObjectUtil.isNotNull(dept)) {
                list.add(dept.getDeptName());
            }
        }
        return String.join(StringUtils.SEPARATOR, list);
    }

    public SysDept selectDeptById(Long deptId) {
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        if (ObjectUtil.isNull(dept)) {
            return null;
        }
        SysDept parentDept = sysDeptMapper.selectByPrimaryKey(dept.getParentId());
        dept.setParentName(ObjectUtil.isNotNull(parentDept) ? parentDept.getDeptName() : null);
        return dept;
    }

    @Override
    public List<Long> deptByParent(Long parentDeptId) {
        List<Long> deptList = CollUtil.newArrayList(parentDeptId);
        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria)
                .eq(SysDept::getParentId, parentDeptId)
                .eq(SysDept::getIsDeleted, UserConstants.USER_RETAIN);

        deptList.addAll(CollUtil.emptyIfNull(sysDeptMapper.selectByExample(example)).stream().map(SysDept::getDeptId)
                .toList());
        return deptList;
    }

    @Override
    public List<Long> deptByAncestors(String ancestors) {
        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria)
                .leftLike(SysDept::getAncestors, ancestors)
                .eq(SysDept::getIsDeleted, UserConstants.USER_RETAIN);

        return CollUtil.emptyIfNull(sysDeptMapper.selectByExample(example)).stream().map(SysDept::getDeptId)
                .collect(Collectors.toList());
    }

    @Override
    public SysDept deptById(Long deptId) {
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        if (ObjectUtil.isNull(dept)) {
            return null;
        }
        SysDept parentDept = sysDeptMapper.selectByPrimaryKey(dept.getParentId());
        dept.setParentName(ObjectUtil.isNotNull(parentDept) ? parentDept.getDeptName() : null);
        return dept;
    }

    @Override
    public List<SysDept> getAllDeptList(SysDept sysDept) {
        return sysDeptMapper.getAllDeptList();
    }


    public String getDeptAndChild(Long deptId) {
        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).and(DataBaseHelper.findInSet(deptId, "ancestors"));
        List<SysDept> deptList = sysDeptMapper.selectByExample(example);

        List<Long> ids = StreamUtils.toList(deptList, SysDept::getDeptId);
        ids.add(deptId);

        example = new Example(SysDept.class);
        criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).in(SysDept::getDeptId, ids);
        List<SysDept> list = sysDeptMapper.selectByExample(example);

        if (CollUtil.isNotEmpty(list)) {
            return StreamUtils.join(list, d -> Convert.toStr(d.getDeptId()));
        }
        return null;
    }


    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria)
                .eq(ObjectUtil.isNotNull(dept.getDeptId()), SysDept::getDeptId, dept.getDeptId())
                .eq(ObjectUtil.isNotNull(dept.getParentId()), SysDept::getParentId, dept.getParentId())
                .like(StringUtils.isNotBlank(dept.getDeptName()), SysDept::getDeptName, dept.getDeptName())
                .eq(ObjectUtil.isNotEmpty(dept.getStatus()), SysDept::getStatus, dept.getStatus());
        example.orderBy(LambdaUtil.getFieldName(SysDept::getParentId)).asc();
        example.orderBy(LambdaUtil.getFieldName(SysDept::getOrderNum)).asc();

        String sql = SqlBuilder.buildWhereClause(example, "");

        return sysDeptMapper.selectDeptList(sql);
    }

    public PageInfo<SysDept> selectPageList(SysDept dept, int pageNum, int pageSize) {
        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria)
                .eq(ObjectUtil.isNotNull(dept.getDeptId()), SysDept::getDeptId, dept.getDeptId())
                .eq(ObjectUtil.isNotNull(dept.getParentId()), SysDept::getParentId, dept.getParentId())
                .like(StringUtils.isNotBlank(dept.getDeptName()), SysDept::getDeptName, dept.getDeptName())
                .eq(ObjectUtil.isNotEmpty(dept.getStatus()), SysDept::getStatus, dept.getStatus());

        String sql = SqlBuilder.buildWhereClause(example, "d");

        PageMethod.startPage(pageNum, pageSize);
        List<SysDept> list = sysDeptMapper.selectDeptList(sql);
        return new PageInfo<>(list);
    }

    @Override
    public List<Tree<String>> selectDeptTreeList(SysDept dept) {
        List<SysDept> deptList = this.selectDeptList(dept);
        return buildDeptTreeSelect(deptList);
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param deptList 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<Tree<String>> buildDeptTreeSelect(List<SysDept> deptList) {
        if (CollUtil.isEmpty(deptList)) {
            return CollUtil.newArrayList();
        }
        // 解决ID超过16位精度不足的问题
        List<SysDeptVO> list = BeanUtil.copyToList(deptList, SysDeptVO.class);

        return TreeBuildUtils.build(list, (deptVO, tree) ->
                tree.setId(deptVO.getDeptId())
                        .setParentId(deptVO.getParentId())
                        .setName(deptVO.getDeptName())
                        .setWeight(deptVO.getOrderNum()));
    }

    @Override
    public void checkDeptDataScope(Long deptId) {
        if (!LoginHelper.isAdmin()) {
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            List<SysDept> depts = this.selectDeptList(dept);
            if (CollUtil.isEmpty(depts)) {
                throw new RuntimeException("没有权限访问部门数据！");
            }
        }
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria)
                .eq(SysDept::getDeptName, dept.getDeptName())
                .eq(SysDept::getParentId, dept.getParentId())
                .ne(ObjectUtil.isNotNull(dept.getDeptId()), SysDept::getDeptId, dept.getDeptId());

        boolean exist = sysDeptMapper.exists(example);
        return !exist;
    }

    @Override
    public int insertDept(SysDept dept) {
        SysDept info = sysDeptMapper.selectByPrimaryKey(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus().toString())) {
            throw new RuntimeException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + StringUtils.SEPARATOR + dept.getParentId());
        return sysDeptMapper.insert(dept);
    }

    @Override
    public long selectNormalChildrenDeptById(Long deptId) {
        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria)
                .eq(SysDept::getStatus, UserConstants.DEPT_NORMAL)
                .and(DataBaseHelper.findInSet(deptId, "ancestors"));

        return sysDeptMapper.selectCountByExample(example);
    }

    @Override
    public int updateDept(SysDept dept) {
        SysDept newParentDept = sysDeptMapper.selectByPrimaryKey(dept.getParentId());
        SysDept oldDept = sysDeptMapper.selectByPrimaryKey(dept.getDeptId());
        if (ObjectUtil.isNotNull(newParentDept) && ObjectUtil.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + StringUtils.SEPARATOR + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = sysDeptMapper.updateByPrimaryKey(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus().toString()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals(UserConstants.DEPT_NORMAL, dept.getAncestors())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    private void updateParentDeptStatusNormal(SysDept dept) {
        String ancestors = dept.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);

        SysDept sysDept = new SysDept();
        sysDept.setStatus(Integer.valueOf(UserConstants.DEPT_NORMAL));

        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).in(SysDept::getDeptId, Arrays.asList(deptIds));
        // 批量更新指定字段
        sysDeptMapper.updateByExampleSelective(sysDept, example);
    }

    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).and(DataBaseHelper.findInSet(deptId, "ancestors"));
        List<SysDept> children = sysDeptMapper.selectByExample(example);
        List<SysDept> list = new ArrayList<>();
        for (SysDept child : children) {
            SysDept dept = new SysDept();
            dept.setDeptId(child.getDeptId());
            dept.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
            list.add(dept);
        }
        if (CollUtil.isNotEmpty(list)) {
            if (updateBatchById(list)) {
//                list.forEach(dept -> CacheUtils.evict(CacheNames.SYS_DEPT, dept.getDeptId()));
            }
        }
    }

    @Override
    public boolean hasChildByDeptId(Long deptId) {
        Example example = new Example(SysDept.class);
        Example.Criteria criteria = example.createCriteria();
        CriteriaUtils.builder(criteria).eq(SysDept::getParentId, deptId);
        return sysDeptMapper.exists(example);
    }

    @Override
    public boolean checkDeptExistUser(Long deptId) {
        Example example = new Example(SysUser.class);
        CriteriaUtils.builder(example.createCriteria()).eq(SysUser::getDeptId, deptId).build();
        return userMapper.exists(example);
    }

    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        SysRole role = roleMapper.selectByPrimaryKey(roleId);
        return sysDeptMapper.selectDeptListByRoleId(roleId, role.getDeptCheckStrictly());
    }

    public boolean updateBatchById(List<SysDept> list) {
        list.forEach(d -> {
            sysDeptMapper.updateByPrimaryKey(d);
        });
        return true;
    }
}
