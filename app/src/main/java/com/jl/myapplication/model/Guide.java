package com.jl.myapplication.model;

public class Guide {
    private int id;
    private String imgUrl;
    private int appType;
    private int deleteFlag;
    private String createTime;
    private int phoneModel;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(int phoneModel) {
        this.phoneModel = phoneModel;
    }

}
