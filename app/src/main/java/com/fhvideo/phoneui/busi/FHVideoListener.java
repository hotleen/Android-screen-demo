package com.fhvideo.phoneui.busi;

import com.fhvideo.bank.FHSurfaceView;

public interface FHVideoListener {

    void uptVideoType(String msg);

    void setSurface(FHSurfaceView mainSurface, FHSurfaceView local, FHSurfaceView third, FHSurfaceView fourth);

    void switchCamera();

    void sendMsg(String msg);

    void rotationRemote(boolean rotation);

    /**
     * 最小化窗口
     * type 是否是单独的最小化   min 最小化   share 投屏
     */
    void minVideo(String type);

    /**
     * 移除悬浮窗
     */
    void removeFloatView();


    void leave();

    /**
     * 自己结束会话
     */
    void closeVideo(boolean isteller);
}
