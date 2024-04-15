package com.fhvideo.phoneui.busi;

public interface FHEventListener {

    /**
     * UI库事件
     * @param type 事件类型
     * @param msg 事件内容
     */
    void onVideoEvent(String type, String msg);

}
