package com.fhvideo.phoneui.api;

public interface FHBaseCallView extends FHBaseViewApi {
    /**
     * 排队中
     * @param msg
     */
    void onQueue(String msg);

    /**
     * 振铃中
     * @param msg
     */
    void onRing(String msg);

    /**
     * 座席忙
     * @param msg
     */
    void onBusy(String msg);

    /**
     * 会话恢复
     * @param msg
     */
    void onResume(String msg);
    /**
     * 座席接听
     * @param msg
     */
    void onAnswer(String msg);

    /**
     * 座席拒接
     * @param msg
     */
    void onRefuse(String msg);
}
