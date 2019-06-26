package com.lotstock.eddid.ibmp.common.base;

import lombok.Data;

import java.util.List;

@Data
public class BaseFilterProperties {

    /**
     * 拦截路径
     */
    private List<String> pathPatterns;

}
