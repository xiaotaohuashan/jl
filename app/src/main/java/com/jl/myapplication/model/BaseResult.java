package com.jl.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class BaseResult<T> {
    @SerializedName("code")
    private int code;
    @SerializedName("resultFlag")
    private Boolean resultFlag;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private T data;

    public int totalCount;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Boolean getResultFlag() {
        return resultFlag;
    }

    public void setResultFlag(Boolean resultFlag) {
        this.resultFlag = resultFlag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
