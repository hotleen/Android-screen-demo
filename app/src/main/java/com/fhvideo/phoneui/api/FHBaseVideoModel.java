package com.fhvideo.phoneui.api;

import android.content.Intent;

import com.fhvideo.bank.FHSurfaceView;
import com.fhvideo.fhcommon.FHBusiCallBack;

public interface FHBaseVideoModel {
    /**
     * 设置视频画布
     *
     * @param sv_main
     * @param sv_local
     * @param sv_third
     * @param sv_fourth
     */
    void setSurface(FHSurfaceView sv_main, FHSurfaceView sv_local, FHSurfaceView sv_third, FHSurfaceView sv_fourth);

    /**
     * 离开房间
     *
     * @param callBack
     */
    void leaveRoom(FHBusiCallBack callBack);

    /**
     * 开启会话
     *
     * @param callBack
     */
    void startSession(FHBusiCallBack callBack);

    /**
     * 结束会话
     *
     * @param callBack
     */
    void closeSession(FHBusiCallBack callBack);

    /**
     * 开启摄像头
     */
    void startCamera();

    /**
     * 关闭摄像头
     */
    void stopCamera();

    /**
     * 切换前后摄像头
     */
    void switchCamera();

    /**
     * 释放摄像头资源
     */
    void releaseCamera();

    /**
     * 重启摄像头
     */
    void resetCamera();

    /**
     * 切换投屏模式
     */
    void changeScreen();

    /**
     * 开启投屏
     */
    void startScreen(int resCode, Intent data);

    /**
     * 结束投屏
     */
    void stopScreen();

    /**
     * 最小化视频页
     */
    void minVideo();

    /**
     * 发送文字聊天
     */
    void sendTextMsg(String msg);

    /**
     * 本地静音
     *
     * @param isMute 默认：false
     * @brif true:静音   false:取消静音
     */
    void muteLocalAudio(boolean isMute);

    /**
     * 暂停本地视频推流
     *
     * @param isMute 默认：false
     * @brif true:暂停   false:恢复
     */
    void muteLocalVideo(boolean isMute);
}
