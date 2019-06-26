package com.lotstock.eddid.ibmp.common.base;

import com.lotstock.eddid.ibmp.i18n.LocaleUtils;

public class BaseController {


    /**
     * 获取国际化信息
     *
     * @param code 唯一编码
     * @param args
     * @return
     */
    protected String getMessage(String code, Object... args) {
        return LocaleUtils.getMessage(code, args);
    }


}
