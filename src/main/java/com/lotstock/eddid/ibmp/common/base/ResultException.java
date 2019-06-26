package com.lotstock.eddid.ibmp.common.base;

public class ResultException extends RuntimeException {

    private ResultModel resultModel;

    public ResultException(ResultModel resultModel) {
        super(resultModel.getMsg());
        this.resultModel = resultModel;
    }

    public ResultException(Throwable cause, ResultModel resultModel) {
        super(cause);
        this.resultModel = resultModel;
//        this.resultModel.setResult(cause);
    }

    public ResultModel getResultModel() {
        return resultModel;
    }




}
