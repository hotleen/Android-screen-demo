package com.fhvideo.phoneui;

import java.io.Serializable;

public class FHAiMsg implements Serializable {
    private String type;//类型  tts asr(预留)
    private String timestamp;//推送时间戳(预留)
    private String msg;//提示信息

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
