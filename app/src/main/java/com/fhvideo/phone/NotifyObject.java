package com.fhvideo.phone;

import java.io.Serializable;

public class NotifyObject implements Serializable {
    public Integer type;
    public String title;
    public String subText;
    public String content;
    public String param;
    public Long firstTime;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Long getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Long firstTime) {
        this.firstTime = firstTime;
    }
}
