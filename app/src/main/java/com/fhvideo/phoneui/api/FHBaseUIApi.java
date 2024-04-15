package com.fhvideo.phoneui.api;

/**
 * 主接口类
 * （基础类，提供公共功能接口（例如 会话中人脸出框 音视频回调 im消息））提供登录等接口，并提供View绑定接口
 */
public interface FHBaseUIApi {
    void initSDK();
    void login();
    void logout();
    /**
     * 初始化
     * 登录
     * 登出
     * View绑定
     */
    /**
     * 设置呼叫UI
     * @param callView
     */
    void setCallView(FHBaseCallView callView);

    /**
     * 设置会话UI
     * @param videoView
     */
    void setVideoView(FHBaseVideoView videoView);
}
