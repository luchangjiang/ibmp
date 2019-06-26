package com.lotstock.eddid.ibmp.common.base;

import java.beans.Transient;
import java.util.HashMap;

public class AjaxResult<T> extends HashMap<String, Object> {

    // 允许空字段
    public static boolean IS_NULL_FIELD = true;
    protected static String CODE_NAME = "code";
    protected static String MSG_NAME = "message";
    protected static String DATA_NAME = "result";

    public static void config(String codeName, String msgName, String dataName) {
        if (codeName != null) {
            CODE_NAME = codeName;
        }
        if (msgName != null) {
            MSG_NAME = msgName;
        }
        if (dataName != null) {
            DATA_NAME = dataName;
        }
    }

    public AjaxResult() {
    }

    @Transient
    public int getCode() {
        return (int) super.get(CODE_NAME);
    }

    @Transient
    public String getMsg() {
        return (String) super.get(MSG_NAME);
    }

    @Transient
    public T getResult() {
        return (T) super.get(DATA_NAME);
    }

    public AjaxResult(int code, String msg) {
        this(code, msg, null);
    }

    public AjaxResult(int code, String msg, T data) {
        super.put(CODE_NAME, code);
        if (IS_NULL_FIELD || msg != null) {
            super.put(MSG_NAME, msg);
        }
        if (IS_NULL_FIELD || data != null) {
            super.put(DATA_NAME, data);
        }
    }


    public boolean isSuccess() {
        return new Integer(0).equals(get(CODE_NAME));
    }


    public static <T> AjaxResult<T> result(int code, String msg, T data) {
        return new AjaxResult<>(code, msg, data);
    }

    public static <T> AjaxResult<T> ok(T data) {
        return result(0, null, data);
    }

    public static <T> AjaxResult<T> fail(String msg) {
        return result(-1, msg, null);
    }

    public static <T> AjaxResult<T> fail(int code, String msg) {
        return result(code, msg, null);
    }


}
