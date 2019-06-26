package com.lotstock.eddid.ibmp.common.base;

import com.lotstock.eddid.ibmp.i18n.LocaleUtils;

import java.io.Serializable;

/**
 * Created by dell on 2018-05-16.
 */
public class ResultModel<T> implements Serializable {

    private int code;
    private String msg;
    private T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public ResultModel() {
        // 序列化
    }

    public ResultModel(int code, String msg) {
        this(code, msg, null);
    }

    public ResultModel(int code, String msg, T result) {
        this.code = code;
        if (msg != null) {
            this.msg = LocaleUtils.getMessage(msg);
        }
        this.result = result;
    }

    public static <T> ResultModel<T> ok(T result) {
        return new ResultModel<T>(0, "common.request.success", result);
    }

    public static ResultModel<Void> fail(int code, String msg) {
        return new ResultModel<Void>(code, msg, null);
    }

    public static ResultModel<Void> fail(String msg) {
        return fail(-1, msg);
    }


    public boolean isSuccess() {
        return code == 0;
    }

    @Override
    public String toString() {
        return "ResultModel{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }


}
