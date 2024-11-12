package com.hawk.framework.common.core.base;

import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree基类
 *
 * @author Lion Li
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class TreeEntity<T> extends BaseDataEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单名称
     */
    @Transient
    private String parentName;

    /**
     * 父菜单ID
     */
//    @Transient
    private Long parentId;

    /**
     * 子部门
     */
    @Transient
    private List<T> children = new ArrayList<>();

}
