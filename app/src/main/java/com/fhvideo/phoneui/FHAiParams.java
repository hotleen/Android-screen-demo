package com.fhvideo.phoneui;

import java.io.Serializable;

public class FHAiParams implements Serializable {
    private int promptTime;//提示时间
    private String pushTime; //推送时间(预留)
    private String url;//资源URL
    private int expireTime;//展示时间(预留)
    private String msg;//提示信息

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getPromptTime() {
        return promptTime;
    }

    public void setPromptTime(int promptTime) {
        this.promptTime = promptTime;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }
}
