package com.hawk.framework.dto;

import cn.hutool.core.lang.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DictDTO {

    private Pair<String, String> type;

    private List<Pair<String, String>> data;
}
