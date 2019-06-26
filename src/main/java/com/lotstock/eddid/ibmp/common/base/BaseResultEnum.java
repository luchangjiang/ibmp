package com.lotstock.eddid.ibmp.common.base;

import com.lotstock.eddid.ibmp.i18n.LocaleUtils;

/**
 * Created by dell on 2018-05-16.
 */
public enum BaseResultEnum {

    /**
     * 基础异常
     * 0-100
     */
    SUCCESS(0, "common.request.success"), // 成功
    FAIL(1, "common.request.fail"), // 失败
    PARAM_FAIL(2, "common.request.param.fail"),
    PARAM_RESOLVE_FAIL(3, "common.request.param.resolve.fail"), // 参数解析失败

    /**
     * 用户异常
     * 10xx
     */
    NOT_LOGIN(1001, "common.access.login.not"),
    LOGIN_EXPIRE(1002, "common.access.login.expire"),
    USER_NOT_FOUND(1003, "common.access.user.notfound"),
    UNAUTHORIZED(1004, "common.access.unauthorized"),
    LOGIN_FAIL(1005, "common.access.login.fail"), // 登录失败次数过多

    /**
     * 系统异常
     * 90xx
     */
    UNKNOWN_EXCEPTION(9001, "common.request.exception.unknown"),
    ILLEGAL_REQUEST(9002, "common.request.param.illegal"),
    TIMEOUT(9011, "common.request.timeout"),
    UNAVAILABLE(9012, "common.request.unavailable"),

    /**
     * 参数异常
     * 99xx-网关异常
     */
    PARAM_TOKEN_NULL(9901, "common.request.param.token.null"), // TOKEN空
    PARAM_TOKEN_EXPIRE(9902, "common.request.param.token.expire"),   // TOKEN过期
    PARAM_TOKEN_FAIL(9903, "common.request.param.token.fail"),   // TOKEN非法
    ;


    private int code;
    private int status;
    private String msg;

    BaseResultEnum(int code, String msg) {
        this.code = code;
        this.status = code;
        this.msg = msg;
    }

    public ResultModel<?> getResultModel() {
        return new ResultModel(code, msg);
    }

    public String getResultJsonString() {
        return String.format("{\"code\": \"%s\", \"message\": \"%s\"}", this.code, LocaleUtils.getMessage(this.msg));
    }
}
