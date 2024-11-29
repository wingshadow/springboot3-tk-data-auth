package com.hawk.generator.controller;

import com.github.pagehelper.PageInfo;
import com.hawk.framework.base.BaseController;
import com.hawk.generator.controller.form.GenTableForm;
import com.hawk.generator.entity.GenTable;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2024-11-29 15:29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tool/gen")
public class GenController extends BaseController {

    @GetMapping("/list")
    public PageInfo<GenTable> genList(GenTableForm form) {
        return new PageInfo<GenTable>();
    }
}
