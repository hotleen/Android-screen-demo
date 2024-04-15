package com.fhvideo.phoneui.api;

public interface FHBaseVideoView extends FHBaseViewApi {
    /**
     * 交易事件回调
     * @param msg 交易信息
     */
    void onTransEvent(String msg);

    void onSessionEvent(String type,String msg);

    /**
     * 会话开启成功
     * @param msg
     */
    void onStartSessionSuccess(String msg);
}
