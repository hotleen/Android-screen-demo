package com.fhvideo.phoneui;

import com.fhvideo.FHLiveCallBack;
import com.fhvideo.bean.UiEvent;

import org.greenrobot.eventbus.EventBus;

public class FHPhoneLiveCallBack implements FHLiveCallBack {
    /**
     * 呼叫事件回调
     *
     * @param event   事件类型
     * @param content 事件内容，无内容时为空
     */
    @Override
    public void onCallEvent(String event, String content) {
        EventBus.getDefault().post(new UiEvent(content,event));
    }

    /**
     * 会话事件回调
     *
     * @param event   事件类型
     * @param content 事件内容，无内容时为空
     */
    @Override
    public void onSessionEvent(String event, String content) {
        EventBus.getDefault().post(new UiEvent(content,event));

    }

    /**
     * 用户事件回调
     *
     * @param event   事件类型
     * @param content 事件内容，无内容时为空
     */
    @Override
    public void onUserEvent(String event, String content) {
        EventBus.getDefault().post(new UiEvent(content,event));

    }

    /**
     * 交易事件回调
     *
     * @param content 事件内容，无内容时为空
     */
    @Override
    public void onTransEvent(String content) {
        EventBus.getDefault().post(new UiEvent(content, FHUIConstants.SHOW_TRANS));

    }

    /**
     * 互动事件回调
     *
     * @param event   事件类型
     * @param content 事件内容，无内容时为空
     */
    @Override
    public void onInteractEvent(String event, String content) {
        EventBus.getDefault().post(new UiEvent(content,event));

    }
}
