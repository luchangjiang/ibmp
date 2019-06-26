package com.lotstock.eddid.ibmp.param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lotstock.eddid.ibmp.common.base.BaseController;
import com.lotstock.eddid.ibmp.common.base.ResultModel;
import com.lotstock.eddid.ibmp.common.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 *
 */
@Slf4j
@RequestMapping("${gateway-path}/param")
@Controller("paramController")
public class ParamController extends BaseController {

    @Autowired
    private ParamManager paramManager;


    /**
     * 客户端请求ID：加密用
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/save_key")
    public ResultModel<?> getClientRequestKey(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String contentType = request.getHeader("Content-Type");
        String paramKey = null;
        if (contentType == null || contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            paramKey = request.getParameter("paramKey");
        } else if (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            paramKey = JSONObject.parseObject(IOUtils.readToString(request.getInputStream())).getString("paramKey");
        } else {
            paramKey = IOUtils.readToString(request.getInputStream());
        }
//        if (StringUtils.isBlank(paramKey)) {
//            paramKey = IOUtils.readToString(request.getInputStream());
//        }
        if (StringUtils.isBlank(paramKey)) {
            return ResultModel.fail("common.request.param.fail");
        }
        try {
            return ResultModel.ok(paramManager.saveParamKey(request, response, paramKey));
        } catch (Exception e) {
            log.error("参数Key解密异常", e);
            return ResultModel.fail("common.request.param.illegal");
        }
    }


}
