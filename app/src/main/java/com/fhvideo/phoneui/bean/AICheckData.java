package com.fhvideo.phoneui.bean;

public class AICheckData {
    private float time;
    private String msg;

    public AICheckData(float time, String msg) {
        this.time = time;
        this.msg = msg;
    }

    public AICheckData() {
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
