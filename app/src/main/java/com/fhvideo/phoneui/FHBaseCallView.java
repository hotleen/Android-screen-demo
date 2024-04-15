package com.fhvideo.phoneui;

import android.view.View;

public interface FHBaseCallView {
    /**
     * 获取UI控件
     * @return
     */
    View getView();

    /**
     * 呼叫事件回调
     * @param event  事件类型
     * @param msg    事件内容
     */
    void onCallEvent(String event, String msg);

    /**
     * 出错
     * @param msg
     */
    void onError(String msg);

    /**
     * 警告
     * @param msg
     */
    void warn(String msg);

    /**
     * 释放资源
     */
    void release();
}
